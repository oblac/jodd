// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.proxetta.asm;

import jodd.asm.AsmUtil;
import jodd.asm.EmptyClassVisitor;
import jodd.asm.EmptyMethodVisitor;
import jodd.asm.MethodAdapter;
import jodd.asm5.ClassReader;
import jodd.asm5.FieldVisitor;
import jodd.asm5.Label;
import jodd.asm5.MethodVisitor;
import jodd.io.StreamUtil;
import jodd.proxetta.JoddProxetta;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxyPointcut;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static jodd.asm5.Opcodes.ALOAD;
import static jodd.asm5.Opcodes.INVOKEINTERFACE;
import static jodd.asm5.Opcodes.INVOKESPECIAL;
import static jodd.asm5.Opcodes.INVOKESTATIC;
import static jodd.asm5.Opcodes.INVOKEVIRTUAL;
import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.adviceFieldName;
import static jodd.proxetta.asm.ProxettaAsmUtil.adviceMethodName;
import static jodd.proxetta.asm.ProxettaAsmUtil.isStoreOpcode;

/**
 * Data of single aspect.
 */
@SuppressWarnings({"AnonymousClassVariableHidesContainingMethodVariable"})
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
			adviceClassReaderCache = new HashMap<>();
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
		if (ready) {
			return;
		}

		adviceClassReader.accept(new EmptyClassVisitor() {

			/**
			 * Stores advice reference.
			 */
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
				adviceReference = name;
				super.visit(version, access, name, signature, superName, interfaces);
			}

			/**
			 * Prevents advice to have inner classes.
			 */
			@Override
			public void visitInnerClass(String name, String outerName, String innerName, int access) {
				if (outerName.equals(adviceReference)) {
					throw new ProxettaException("Proxetta doesn't allow inner classes in/for advice: " + advice.getName());
				}
				super.visitInnerClass(name, outerName, innerName, access);
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
				if (name.equals(CLINIT)) {              // [A6]
					if (!desc.equals(DESC_VOID)) {
						throw new ProxettaException("Invalid static initialization block description for advice: " + advice.getName());
					}
					name = JoddProxetta.defaults().getClinitMethodName() + JoddProxetta.defaults().getMethodDivider() + aspectIndex;
					access |= AsmUtil.ACC_PRIVATE | AsmUtil.ACC_FINAL;
					wd.addAdviceClinitMethod(name);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						@Override
						public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
						}

						@Override
						public void visitLineNumber(int line, Label start) {
						}

						@Override
						public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
							if (opcode == INVOKESTATIC) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							}
							super.visitMethodInsn(opcode, owner, name, desc, isInterface);
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

				if (name.equals(INIT)) { // [A7]
					if (!desc.equals(DESC_VOID)) {
						throw new ProxettaException("Advices can have only default constructors. Invalid advice: " + advice.getName());
					}

					name = JoddProxetta.defaults().getInitMethodName() + JoddProxetta.defaults().getMethodDivider() + aspectIndex;
					access = ProxettaAsmUtil.makePrivateFinalAccess(access);
					wd.addAdviceInitMethod(name);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {
						@Override
						public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
						}

						@Override
						public void visitLineNumber(int line, Label start) {
						}

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
						public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
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
							super.visitMethodInsn(opcode, owner, name, desc, isInterface);
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
				if (!name.equals(JoddProxetta.defaults().getExecuteMethodName())) {
					name = adviceMethodName(name, aspectIndex);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						@Override
						public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
						}

						@Override
						public void visitLineNumber(int line, Label start) {
						}

						@Override
						public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
							if ((opcode == INVOKEVIRTUAL) || (opcode == INVOKEINTERFACE)) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							} else

							if (opcode == INVOKESTATIC || opcode == INVOKESPECIAL) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							}
							super.visitMethodInsn(opcode, owner, name, desc, isInterface);
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
				//return new MethodAdapter(new EmptyMethodVisitor()) {		// toask may we replace this with the following code?
				return new EmptyMethodVisitor() {
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
