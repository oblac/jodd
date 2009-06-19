// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
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
import jodd.proxetta.AnnotationData;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyTarget;
import static jodd.proxetta.asm.ProxettaAsmUtil.*;
import static jodd.proxetta.asm.ProxettaNaming.EXECUTE_METHOD_NAME;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings({"ParameterNameDiffersFromOverriddenParameter"})
public class ProxyMethodBuilder extends EmptyMethodVisitor  {

	public static final String TARGET_CLASS_NAME = ProxyTarget.class.getSimpleName();        // extract ProxyTarget name for recognition

	protected final MethodSignatureVisitor msign;
	protected final WorkData wd;

	public ProxyMethodBuilder(MethodSignatureVisitor msign, WorkData wd) {
		this.msign = msign;
		this.wd = wd;
	}

	/**
	 * Stores target method annotation data in method signature.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationData ad = new AnnotationData(desc, visible);
		msign.annotations.add(ad);
		return new AnnotationReader(ad);
	}

	/**
	 * Finally, builds proxy methods if applied to current method.
	 */
	@Override
	public void visitEnd() {
		// match method to all proxy aspects

		List<ProxyAspectData> aspectList = null;
		for (ProxyAspectData aspectData : wd.proxyAspects) {
			if (aspectData.apply(msign) == true) {
				if (aspectList == null) {
					aspectList = new ArrayList<ProxyAspectData>(wd.proxyAspects.length);
				}
				aspectList.add(aspectData);
			}
		}
		if (aspectList == null) {
			return; // no pointcut on this method, return
		}

		// check invalid access flags
		int access = msign.getAccessFlags();
		if ((access & MethodInfo.ACC_FINAL) != 0) {   // detect final
			throw new ProxettaException("Unable to create proxy for final method: " + msign +". Remove final modifier or change the pointcut definition.");
		}

		// create proxy methods
		TargetMethodData tmd = new TargetMethodData(msign, aspectList);
		createFirstChainDelegate(tmd);
		for (int p = 0; p < tmd.proxyData.length; p++) {
			tmd.selectCurrentProxy(p);
			createProxyMethod(tmd);
		}
		wd.proxyApplied = true;
	}

	/**
	 * Creates the very first method in calling chain that simply delegates invocation to the first proxy method.
	 * This method mirrors the target method.
	 */
	protected void createFirstChainDelegate(TargetMethodData td) {
		int access = td.msign.getAccessFlags();
		access = ProxettaAsmUtil.makeNonNative(access);
		MethodVisitor mv = wd.dest.visitMethod(access, td.msign.getMethodName(), td.msign.getDescription(), td.msign.getSignature(), null);
		mv.visitCode();
		loadMethodArguments(mv, td.msign);
		mv.visitMethodInsn(INVOKESPECIAL, wd.thisReference, td.firstMethodName(), td.msign.getDescription());
		visitReturn(mv, td.msign, false);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		writeAnnotations(mv, td.msign.getAnnotations());      // [A4]
	}


	/**
	 * Write all proxy method annotations.
	 */
	void writeAnnotations(MethodVisitor dest, List<AnnotationData> annotations) {
		for (AnnotationData ann : annotations) {
			AnnotationVisitor av = dest.visitAnnotation(ann.signature, ann.isVisible);
			for (Map.Entry<String, Object> entry : ann.values.entrySet()) {
				av.visit(entry.getKey(), entry.getValue());
			}
		}
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
		access = ProxettaAsmUtil.makeNonNative(access);
		access = ProxettaAsmUtil.makePrivateFinalAccess(access);
		final MethodVisitor mv = wd.dest.visitMethod(access, td.methodName(), td.msign.getDescription(), null, null);
		mv.visitCode();

		//*** VISIT ADVICE - called for each aspect and each method
		aspectData.getAdviceClassReader().accept(new EmptyVisitor() {

			@Override
			public MethodVisitor visitMethod(int methodAccess, String name, String desc, String signature, String[] exceptions) {

				if (name.equals(EXECUTE_METHOD_NAME) == false) {
					return null;
				}

				return new IntArgHistoryMethodAdapter(mv) {

					@Override
					public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
						if (owner.equals(aspectData.adviceReference)) {
							owner = wd.thisReference;              // [F5]
							fieldName = adviceFieldName(fieldName, aspectData.aspectIndex);
						}
						super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
					}


					@Override
					public void visitVarInsn(int opcode, int var) {
						var += (var == 0 ? 0 : td.msign.getAllArgumentsSize());
						super.visitVarInsn(opcode, var);   // [F1]
					}

					@Override
					public void visitIincInsn(int var, int increment) {
						var += (var == 0 ? 0 : td.msign.getAllArgumentsSize());
						super.visitIincInsn(var, increment);  // [F1]
					}

					@Override
					public void visitInsn(int opcode) {
						if (opcode == ARETURN) {
							visitReturn(mv, td.msign, true);
							return;
						}
						if (traceNext == true) {
							if ((opcode == POP) || (opcode == POP2)) {      // [F3] - invoke invoked without assignment
								return;
							}
						}
						super.visitInsn(opcode);
					}

					@Override
					public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc) {
						if ((opcode == INVOKEVIRTUAL) || (opcode == INVOKEINTERFACE)) {
							if (owner.equals(aspectData.adviceReference)) {
								owner = wd.thisReference;
								methodName = adviceMethodName(methodName, aspectData.aspectIndex);
							}
						} else

						if (opcode == INVOKESTATIC) {
							if (owner.equals(aspectData.adviceReference)) {
								owner = wd.thisReference;
								methodName = adviceMethodName(methodName, aspectData.aspectIndex);
							} else

							if (owner.endsWith('/' + TARGET_CLASS_NAME) == true) {

								if (isInvokeMethod(methodName, methodDesc)) {           // [R7]
									if (td.isLastMethodInChain()) {                            // last proxy method just calls super target method
										loadMethodArguments(mv, td.msign);
										mv.visitMethodInsn(INVOKESPECIAL, wd.superReference, td.msign.getMethodName(), td.msign.getDescription());

										prepareReturnValue(mv, td.msign, aspectData.maxLocalVarOffset);     // [F4]
										traceNext = true;
									} else {                                                    // calls next proxy method
										loadMethodArguments(mv, td.msign);
										mv.visitMethodInsn(INVOKESPECIAL, wd.thisReference, td.nextMethodName(), td.msign.getDescription());
										visitReturn(mv, td.msign, false);
									}
									return;
								} else

								if (isArgumentsCountMethod(methodName, methodDesc)) {        // [R2]
									int argsCount = td.msign.getArgumentsCount();
									pushInt(mv, argsCount);
									return;
								} else

								if (isArgumentTypeMethod(methodName, methodDesc)) {      // [R3]
									int argIndex = this.getArgumentIndex();
									checkArgumentIndex(td.msign, argIndex, aspectData.advice);
									mv.visitInsn(POP);
									loadMethodArgumentClass(mv, td.msign, argIndex);
									return;
								} else

								if (isArgumentMethod(methodName, methodDesc)) {           // [R4]
									int argIndex = this.getArgumentIndex();
									checkArgumentIndex(td.msign, argIndex, aspectData.advice);
									mv.visitInsn(POP);
									loadMethodArgumentAsObject(mv, td.msign, argIndex);
									return;
								} else

								if (isSetArgumentMethod(methodName, methodDesc)) {           // [R5]
									int argIndex = this.getArgumentIndex();
									checkArgumentIndex(td.msign, argIndex, aspectData.advice);
									mv.visitInsn(POP);
									storeMethodArgumentFromObject(mv, td.msign, argIndex);
									return;
								} else

								if (isCreateArgumentsArrayMethod(methodName, methodDesc)) {  // [R6]
									int argsCount = td.msign.getArgumentsCount();
									pushInt(mv, argsCount);
									mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
									for (int i = 0; i < argsCount; i++) {
										mv.visitInsn(DUP);
										pushInt(mv, i);
										loadMethodArgumentAsObject(mv, td.msign, i + 1);
										mv.visitInsn(AASTORE);
									}
									return;
								} else

								if (isCreateArgumentsClassArrayMethod(methodName, methodDesc)) {     // [R11]
									int argsCount = td.msign.getArgumentsCount();
									pushInt(mv, argsCount);
									mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
									for (int i = 0; i < argsCount; i++) {
										mv.visitInsn(DUP);
										pushInt(mv, i);
										loadMethodArgumentClass(mv, td.msign, i + 1);
										mv.visitInsn(AASTORE);
									}
									return;
								} else

								if (isTargetMethod(methodName, methodDesc)) {       // [R9.1]
									mv.visitVarInsn(ALOAD, 0);
									return;
								} else

								if (isTargetClassMethod(methodName, methodDesc)) {       // [R9]
									mv.visitLdcInsn(Type.getType('L' + wd.superReference + ';'));
									return;
								} else

								if (isTargetMethodNameMethod(methodName, methodDesc)) {  // [R10]
									mv.visitLdcInsn(td.msign.getMethodName());
									return;
								}

								if (isReturnTypeMethod(methodName, methodDesc)) {        // [R11]
									loadMethodReturnClass(mv, td.msign);
									return;
								}
							}
						}
						super.visitMethodInsn(opcode, owner, methodName, methodDesc);
					}

				};
			}

		}, 0);
	}
}