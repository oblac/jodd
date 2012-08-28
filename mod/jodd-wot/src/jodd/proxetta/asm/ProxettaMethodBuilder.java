// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm.AsmConst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyTarget;
import static jodd.proxetta.asm.ProxettaAsmUtil.*;
import static jodd.proxetta.asm.ProxettaNaming.EXECUTE_METHOD_NAME;
import jodd.asm.AnnotationVisitorAdapter;
import jodd.asm.EmptyClassVisitor;
import jodd.asm.EmptyMethodVisitor;

import java.util.List;

@SuppressWarnings({"AnonymousClassVariableHidesContainingMethodVariable"})
public class ProxettaMethodBuilder extends EmptyMethodVisitor {

	public static final String TARGET_CLASS_NAME = ProxyTarget.class.getSimpleName();        // extract ProxyTarget name for recognition

	protected final MethodSignatureVisitor msign;
	protected final WorkData wd;
	protected final List<ProxyAspectData> aspectList;

	public ProxettaMethodBuilder(MethodSignatureVisitor msign, WorkData wd, List<ProxyAspectData> aspectList) {
		this.msign = msign;
		this.wd = wd;
		this.aspectList = aspectList;
		createFirstChainDelegate_Start();
	}

	// ---------------------------------------------------------------- visits

	/**
	 * Copies target method annotations
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationVisitor destAnn = mv.visitAnnotation(desc, visible); // [A4]
		return new AnnotationVisitorAdapter(destAnn);
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		AnnotationVisitor destAnn = mv.visitAnnotationDefault();
		return new AnnotationVisitorAdapter(destAnn);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		AnnotationVisitor destAnn = mv.visitParameterAnnotation(parameter, desc, visible);
		return new AnnotationVisitorAdapter(destAnn);
	}


	/**
	 * Finally, builds proxy methods if applied to current method.
	 */
	@Override
	public void visitEnd() {
		createFirstChainDelegate_Continue(tmd);
		for (int p = 0; p < tmd.proxyData.length; p++) {
			tmd.selectCurrentProxy(p);
			createProxyMethod(tmd);
		}
	}


	// ---------------------------------------------------------------- creating

	protected TargetMethodData tmd;
	protected MethodVisitor mv;

	/**
	 * Starts creation of first chain delegate.
	 */
	protected void createFirstChainDelegate_Start() {
		// check invalid access flags
		int access = msign.getAccessFlags();
		if ((access & AsmConst.ACC_FINAL) != 0) {   // detect final
			throw new ProxettaException("Unable to create proxy for final method: " + msign +". Remove final modifier or change the pointcut definition.");
		}

		// create proxy methods
		tmd = new TargetMethodData(msign, aspectList);

		access &= ~ACC_NATIVE;
		access &= ~ACC_ABSTRACT;

		mv = wd.dest.visitMethod(access, tmd.msign.getMethodName(), tmd.msign.getDescription(), tmd.msign.getSignature(), null);
	}

	/**
	 * Continues the creation of the very first method in calling chain that simply delegates invocation to the first proxy method.
	 * This method mirrors the target method.
	 */
	protected void createFirstChainDelegate_Continue(TargetMethodData td) {
		mv.visitCode();
		loadSpecialMethodArguments(mv, td.msign);
		mv.visitMethodInsn(INVOKESPECIAL, wd.thisReference, td.firstMethodName(), td.msign.getDescription());
		visitReturn(mv, td.msign, false);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}



	/**
	 * Creates proxy methods over target method, For each matched proxy, new proxy method is created
	 * by taking advice bytecode and replaces usages of {@link jodd.proxetta.ProxyTarget}.
	 * <p>
	 * Invocation chain example: name -> name$p0 -> name$p1 -> name$p4 -> super
	 */
	public void createProxyMethod(final TargetMethodData td) {
		final ProxyAspectData aspectData = td.getProxyData();

		int access = td.msign.getAccessFlags();

		access &= ~ACC_NATIVE;
		access &= ~ACC_ABSTRACT;
		access = ProxettaAsmUtil.makePrivateFinalAccess(access);

		final MethodVisitor mv = wd.dest.visitMethod(access, td.methodName(), td.msign.getDescription(), null, null);
		mv.visitCode();

		//*** VISIT ADVICE - called for each aspect and each method
		aspectData.getAdviceClassReader().accept(new EmptyClassVisitor() {

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

				if (name.equals(EXECUTE_METHOD_NAME) == false) {
					return null;
				}

				return new IntArgHistoryMethodAdapter(mv) {

					@Override
					public void visitFieldInsn(int opcode, String string, String string1, String string2) {
						if (string.equals(aspectData.adviceReference)) {
							string = wd.thisReference;              // [F5]
							string1 = adviceFieldName(string1, aspectData.aspectIndex);
						}
						super.visitFieldInsn(opcode, string, string1, string2);
					}


					@Override
					public void visitVarInsn(int opcode, int i1) {
						i1 += (i1 == 0 ? 0 : td.msign.getAllArgumentsSize());
						super.visitVarInsn(opcode, i1);   // [F1]
					}

					@Override
					public void visitIincInsn(int var, int i1) {
						var += (var == 0 ? 0 : td.msign.getAllArgumentsSize());
						super.visitIincInsn(var, i1);  // [F1]
					}

					boolean returnDefault;

					@Override
					public void visitInsn(int opcode) {
						if (opcode == ARETURN) {
							visitReturn(mv, td.msign, true, returnDefault);
							return;
						}
						if (traceNext == true) {
							if ((opcode == POP) || (opcode == POP2)) {      // [F3] - invoke invoked without assignment
								return;
							}
						}
						super.visitInsn(opcode);
					}

					@SuppressWarnings({"ParameterNameDiffersFromOverriddenParameter"})
					@Override
					public void visitMethodInsn(int opcode, String string, String mname, String mdesc) {
						if ((opcode == INVOKEVIRTUAL) || (opcode == INVOKEINTERFACE) || (opcode == INVOKESPECIAL)) {
							if (string.equals(aspectData.adviceReference)) {
								string = wd.thisReference;
								mname = adviceMethodName(mname, aspectData.aspectIndex);
							}
						} else

						if (opcode == INVOKESTATIC) {
							if (string.equals(aspectData.adviceReference)) {
								string = wd.thisReference;
								mname = adviceMethodName(mname, aspectData.aspectIndex);
							} else

							if (string.endsWith('/' + TARGET_CLASS_NAME) == true) {

								if (isInvokeMethod(mname, mdesc)) {           // [R7]
									if (td.isLastMethodInChain()) {                            // last proxy method just calls super target method

										if (wd.isWrapper() == false) {
											// PROXY
											loadSpecialMethodArguments(mv, td.msign);
											mv.visitMethodInsn(INVOKESPECIAL, wd.superReference, td.msign.getMethodName(), td.msign.getDescription());
										} else {
											// WRAPPER
											mv.visitVarInsn(ALOAD, 0);
											mv.visitFieldInsn(GETFIELD, wd.thisReference, wd.wrapperRef, wd.wrapperType);
											loadVirtualMethodArguments(mv, td.msign);
											if (wd.wrapInterface) {
												mv.visitMethodInsn(INVOKEINTERFACE, wd.wrapperType.substring(1, wd.wrapperType.length() - 1), td.msign.getMethodName(), td.msign.getDescription());
											} else {
												mv.visitMethodInsn(INVOKEVIRTUAL, wd.wrapperType.substring(1, wd.wrapperType.length() - 1), td.msign.getMethodName(), td.msign.getDescription());
											}
										}

										prepareReturnValue(mv, td.msign, aspectData.maxLocalVarOffset);     // [F4]
										traceNext = true;
									} else {                                                    // calls next proxy method
										loadSpecialMethodArguments(mv, td.msign);
										mv.visitMethodInsn(INVOKESPECIAL, wd.thisReference, td.nextMethodName(), td.msign.getDescription());
										visitReturn(mv, td.msign, false);
									}
									return;
								} else

								if (isArgumentsCountMethod(mname, mdesc)) {        // [R2]
									int argsCount = td.msign.getArgumentsCount();
									pushInt(mv, argsCount);
									return;
								} else

								if (isArgumentTypeMethod(mname, mdesc)) {      // [R3]
									int argIndex = this.getArgumentIndex();
									checkArgumentIndex(td.msign, argIndex, aspectData.advice);
									mv.visitInsn(POP);
									loadMethodArgumentClass(mv, td.msign, argIndex);
									return;
								} else

								if (isArgumentMethod(mname, mdesc)) {           // [R4]
									int argIndex = this.getArgumentIndex();
									checkArgumentIndex(td.msign, argIndex, aspectData.advice);
									mv.visitInsn(POP);
									loadMethodArgumentAsObject(mv, td.msign, argIndex);
									return;
								} else

								if (isSetArgumentMethod(mname, mdesc)) {           // [R5]
									int argIndex = this.getArgumentIndex();
									checkArgumentIndex(td.msign, argIndex, aspectData.advice);
									mv.visitInsn(POP);
									storeMethodArgumentFromObject(mv, td.msign, argIndex);
									return;
								} else

								if (isCreateArgumentsArrayMethod(mname, mdesc)) {  // [R6]
									int argsCount = td.msign.getArgumentsCount();
									pushInt(mv, argsCount);
									mv.visitTypeInsn(ANEWARRAY, AsmConst.SIGNATURE_JAVA_LANG_OBJECT);
									for (int i = 0; i < argsCount; i++) {
										mv.visitInsn(DUP);
										pushInt(mv, i);
										loadMethodArgumentAsObject(mv, td.msign, i + 1);
										mv.visitInsn(AASTORE);
									}
									return;
								} else

								if (isCreateArgumentsClassArrayMethod(mname, mdesc)) {     // [R11]
									int argsCount = td.msign.getArgumentsCount();
									pushInt(mv, argsCount);
									mv.visitTypeInsn(ANEWARRAY, AsmConst.SIGNATURE_JAVA_LANG_CLASS);
									for (int i = 0; i < argsCount; i++) {
										mv.visitInsn(DUP);
										pushInt(mv, i);
										loadMethodArgumentClass(mv, td.msign, i + 1);
										mv.visitInsn(AASTORE);
									}
									return;
								} else

								if (isTargetMethod(mname, mdesc)) {       // [R9.1]
									mv.visitVarInsn(ALOAD, 0);
									return;
								} else

								if (isTargetClassMethod(mname, mdesc)) {       // [R9]
									mv.visitLdcInsn(Type.getType('L' + wd.superReference + ';'));
									return;
								} else

								if (isTargetMethodNameMethod(mname, mdesc)) {  // [R10]
									mv.visitLdcInsn(td.msign.getMethodName());
									return;
								} else

								if (isTargetMethodSignatureMethod(mname, mdesc)) {
									mv.visitLdcInsn(td.msign.getSignature());
									return;
								} else

								if (isTargetMethodDescriptionMethod(mname, mdesc)) {
									mv.visitLdcInsn(td.msign.getDescription());
									return;
								} else

								if (isReturnTypeMethod(mname, mdesc)) {        // [R11]
									loadMethodReturnClass(mv, td.msign);
									return;
								} else

								if (isPushDefaultResultValueMethod(mname, mdesc)) {
									returnDefault = true;
									return;
								}
							}
						}
						super.visitMethodInsn(opcode, string, mname, mdesc);
					}

				};
			}

		}, 0);
	}
}