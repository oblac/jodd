// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.ClassReader;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static jodd.proxetta.asm.ProxettaAsmUtil.*;
import static jodd.proxetta.asm.ProxettaNaming.*;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyPointcut;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * Data of single aspect.
 */
@SuppressWarnings({"ParameterNameDiffersFromOverriddenParameter", "AnonymousClassVariableHidesContainingMethodVariable"})
final class ProxyAspectData {

	final ClassReader adviceClassReader;
	final ProxyAspect aspect;
	final Class<? extends ProxyAdvice> advice;
	final ProxyPointcut pointcut;
	
	final int aspectIndex;
	final WorkData wd;    // destination class writer

	String adviceReference;     // advice reference
	boolean ready;              // is advice ready for manipulation?
	int maxLocalVarOffset;      // first next local var offset

	ProxyAspectData(WorkData wd, ProxyAspect aspect, int aspectIndex) {
		this.aspect = aspect;
		this.advice = aspect.getAdvice();
		this.pointcut = aspect.getPointcut();
		this.aspectIndex = aspectIndex;
		this.wd = wd;
		adviceClassReader = getCachedAdviceClassReader(advice);
		readAdviceData();
	}

	/**
	 * Delegates to aspects pointcut.
	 */
	boolean apply(MethodInfo msign) {
		return pointcut.apply(msign);
	}

	// ---------------------------------------------------------------- advice reader cache

	private static Map<Class<? extends ProxyAdvice>, ClassReader> adviceClassReaderCache;


	/**
	 * Creates advice's class reader.
	 */
	private ClassReader createAdviceClassReader(Class<? extends ProxyAdvice> advice) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(advice);
			return new ClassReader(inputStream);
		} catch (IOException ioex) {
			throw new ProxettaException(ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}

	/**
	 * Returns class reader for advice.
	 */
	private ClassReader getCachedAdviceClassReader(Class<? extends ProxyAdvice> advice) {
		if (adviceClassReaderCache == null) {
			adviceClassReaderCache = new HashMap<Class<? extends ProxyAdvice>, ClassReader>();
		}
		ClassReader adviceReader = adviceClassReaderCache.get(advice);
		if (adviceReader == null) {
			adviceReader = createAdviceClassReader(advice);
			adviceClassReaderCache.put(advice, adviceReader);
		}
		return adviceReader;
	}

	/**
	 * Returns class reader for advice.
	 */
	ClassReader getAdviceClassReader() {
		return adviceClassReader;
	}

	// ---------------------------------------------------------------- read

	/**
	 * Parse advice class to gather some advice data. Should be called before any advice use.
	 * Must be called only *once* per advice.
	 */
	private void readAdviceData() {
		if (ready == true) {
			return;
		}

		adviceClassReader.accept(new EmptyVisitor() {

			/**
			 * Stores advice reference.
			 */
			@Override
			public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
				adviceReference = name;
				super.visit(version, access, name, signature, supername, interfaces);
			}

			/**
			 * Prevents advice to have inner classes.
			 */
			@Override
			public void visitInnerClass(String name, String outerName, String innerName, int access) {
				throw new ProxettaException("Proxetta doesn't allow inner classes in/for advice: '" + advice.getName() + "'.");
			}

			/**
			 * Clones advices fields to destination.
			 */
			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				wd.dest.visitField(access, adviceFieldName(name, aspectIndex), desc, signature, value);     // [A5]
				return super.visitField(access, name, desc, signature, value);
			}

			/**
			 * Copies advices methods to destination.
			 */
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				if (name.equals(CLINIT) == true) {              // [A6]
					if (desc.equals(DESC_VOID) == false) {
						throw new ProxettaException("Invalid static initialization block description for advice: '" + advice.getName() + "'.");
					}
					name = CLINIT_METHOD_NAME + METHOD_DIVIDER + aspectIndex;
					access |= MethodInfo.ACC_PRIVATE | MethodInfo.ACC_FINAL;
					wd.addAdviceClinitMethod(name);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						@Override
						public void visitMethodInsn(int opcode, String owner, String name, String desc) {
							if (opcode == INVOKESTATIC) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							}
							super.visitMethodInsn(opcode, owner, name, desc);
						}

						@Override
						public void visitFieldInsn(int opcode, String owner, String name, String desc) { // [F6]
							if (owner.equals(adviceReference)) {
								owner = wd.thisReference;              // [F5]
								name = adviceFieldName(name, aspectIndex);
							}
							super.visitFieldInsn(opcode, owner, name, desc);
						}
					};
				} else

				if (name.equals(INIT) == true) { // [A7]
					if (desc.equals(DESC_VOID) == false) {
						throw new ProxettaException("Advices can have only default constructors. Invalid advice: '" + advice.getName() + "'.");
					}

					name = INIT_METHOD_NAME + METHOD_DIVIDER + aspectIndex;
					access = ProxettaAsmUtil.makePrivateFinalAccess(access);
					wd.addAdviceInitMethod(name);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						int state; // used to detect and to ignore the first super call()

						@Override
						public void visitVarInsn(int opcode, int var) {                      // [F7]
							if ((state == 0) && (opcode == ALOAD) && (var == 0)) {
								state++;
								return;
							}
							super.visitVarInsn(opcode, var);
						}

						@Override
						public void visitMethodInsn(int opcode, String owner, String name, String desc) {
							if ((state == 1) && (opcode == INVOKESPECIAL)) {
							    state++;
								return;
							}
							if ((opcode == INVOKEVIRTUAL) || (opcode == INVOKEINTERFACE)) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							} else

							if (opcode == INVOKESTATIC) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							}
							super.visitMethodInsn(opcode, owner, name, desc);
						}

						@Override
						public void visitFieldInsn(int opcode, String owner, String name, String desc) { // [F7]
							if (owner.equals(adviceReference)) {
								owner = wd.thisReference;              // [F5]
								name = adviceFieldName(name, aspectIndex);
							}
							super.visitFieldInsn(opcode, owner, name, desc);
						}
					};

				} else

				// other methods
				if (name.equals(EXECUTE_METHOD_NAME) == false) {
					name = adviceMethodName(name, aspectIndex);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						@Override
						public void visitMethodInsn(int opcode, String owner, String name, String desc) {
							if ((opcode == INVOKEVIRTUAL) || (opcode == INVOKEINTERFACE)) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							} else

							if (opcode == INVOKESTATIC) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							}
							super.visitMethodInsn(opcode, owner, name, desc);
						}

						@Override
						public void visitFieldInsn(int opcode, String owner, String name, String desc) {        // replace field references
							if (owner.equals(adviceReference)) {
								owner = wd.thisReference;
								name = adviceFieldName(name, aspectIndex);
							}
							super.visitFieldInsn(opcode, owner, name, desc);
						}
					};
				}

				// Parse EXECUTE method, just to gather some info, real parsing will come later
				return new MethodAdapter(this) {
					@Override
					public void visitVarInsn(int opcode, int var) {
						if (isStoreOpcode(opcode)) {
							if (var > maxLocalVarOffset) {
								maxLocalVarOffset = var;          // find max local var offset
							}
						}
						super.visitVarInsn(opcode, var);
					}
				};
//					return super.visitMethod(access, name, desc, signature, exceptions);
			}
		}, 0);
		maxLocalVarOffset += 2;       // increment offset by 2 because var on last index may be a dword value
		ready = true;
	}
}
