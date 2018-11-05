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
import jodd.asm7.ClassReader;
import jodd.asm7.FieldVisitor;
import jodd.asm7.Label;
import jodd.asm7.MethodVisitor;
import jodd.cache.TypeCache;
import jodd.io.StreamUtil;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxettaNames;
import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxyPointcut;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;
import java.io.InputStream;

import static jodd.asm7.Opcodes.ALOAD;
import static jodd.asm7.Opcodes.INVOKEINTERFACE;
import static jodd.asm7.Opcodes.INVOKESPECIAL;
import static jodd.asm7.Opcodes.INVOKESTATIC;
import static jodd.asm7.Opcodes.INVOKEVIRTUAL;
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

	ProxyAspectData(final WorkData wd, final ProxyAspect aspect, final int aspectIndex) {
		this.aspect = aspect;
		this.advice = aspect.advice();
		this.pointcut = aspect.pointcut();
		this.aspectIndex = aspectIndex;
		this.wd = wd;
		adviceClassReader = getCachedAdviceClassReader(advice);
		readAdviceData();
	}

	/**
	 * Delegates to aspects pointcut.
	 */
	boolean apply(final MethodInfo msign) {
		return pointcut.apply(msign);
	}

	// ---------------------------------------------------------------- advice reader cache

	private static TypeCache<ClassReader> adviceClassReaderCache;


	/**
	 * Creates advice's class reader.
	 */
	private ClassReader createAdviceClassReader(final Class<? extends ProxyAdvice> advice) {
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
	private ClassReader getCachedAdviceClassReader(final Class<? extends ProxyAdvice> advice) {
		if (adviceClassReaderCache == null) {
			adviceClassReaderCache = TypeCache.createDefault();
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
			public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
				adviceReference = name;
				super.visit(version, access, name, signature, superName, interfaces);
			}

			/**
			 * Prevents advice to have inner classes.
			 */
			@Override
			public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
				if (outerName.equals(adviceReference)) {
					throw new ProxettaException("Proxetta doesn't allow inner classes in/for advice: " + advice.getName());
				}
				super.visitInnerClass(name, outerName, innerName, access);
			}

			/**
			 * Clones advices fields to destination.
			 */
			@Override
			public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
				wd.dest.visitField(access, adviceFieldName(name, aspectIndex), desc, signature, value);     // [A5]
				return super.visitField(access, name, desc, signature, value);
			}

			/**
			 * Copies advices methods to destination.
			 */
			@Override
			public MethodVisitor visitMethod(int access, String name, final String desc, final String signature, final String[] exceptions) {
				if (name.equals(CLINIT)) {              // [A6]
					if (!desc.equals(DESC_VOID)) {
						throw new ProxettaException("Invalid static initialization block description for advice: " + advice.getName());
					}
					name = ProxettaNames.clinitMethodName + ProxettaNames.methodDivider + aspectIndex;
					access |= AsmUtil.ACC_PRIVATE | AsmUtil.ACC_FINAL;
					wd.addAdviceClinitMethod(name);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						@Override
						public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
						}

						@Override
						public void visitLineNumber(final int line, final Label start) {
						}

						@Override
						public void visitMethodInsn(final int opcode, String owner, String name, final String desc, final boolean isInterface) {
							if (opcode == INVOKESTATIC) {
								if (owner.equals(adviceReference)) {
									owner = wd.thisReference;
									name = adviceMethodName(name, aspectIndex);
								}
							}
							super.visitMethodInsn(opcode, owner, name, desc, isInterface);
						}

						@Override
						public void visitFieldInsn(final int opcode, String owner, String name, final String desc) { // [F6]
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

					name = ProxettaNames.initMethodName + ProxettaNames.methodDivider + aspectIndex;
					access = ProxettaAsmUtil.makePrivateFinalAccess(access);
					wd.addAdviceInitMethod(name);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {
						@Override
						public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
						}

						@Override
						public void visitLineNumber(final int line, final Label start) {
						}

						int state; // used to detect and to ignore the first super call()

						@Override
						public void visitVarInsn(final int opcode, final int var) {                      // [F7]
							if ((state == 0) && (opcode == ALOAD) && (var == 0)) {
								state++;
								return;
							}
							super.visitVarInsn(opcode, var);
						}

						@Override
						public void visitMethodInsn(final int opcode, String owner, String name, final String desc, final boolean isInterface) {
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
						public void visitFieldInsn(final int opcode, String owner, String name, final String desc) { // [F7]
							if (owner.equals(adviceReference)) {
								owner = wd.thisReference;              // [F5]
								name = adviceFieldName(name, aspectIndex);
							}
							super.visitFieldInsn(opcode, owner, name, desc);
						}
					};

				} else

				// other methods
				if (!name.equals(ProxettaNames.executeMethodName)) {
					name = adviceMethodName(name, aspectIndex);
					return new MethodAdapter(wd.dest.visitMethod(access, name, desc, signature, exceptions)) {

						@Override
						public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
						}

						@Override
						public void visitLineNumber(final int line, final Label start) {
						}

						@Override
						public void visitMethodInsn(final int opcode, String owner, String name, final String desc, final boolean isInterface) {
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
						public void visitFieldInsn(final int opcode, String owner, String name, final String desc) {        // replace field references
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
					public void visitVarInsn(final int opcode, final int var) {
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
