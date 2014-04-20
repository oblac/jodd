// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.asm.AsmUtil;
import jodd.asm5.MethodVisitor;
import jodd.proxetta.asm.ProxettaAsmUtil;

import static jodd.asm5.Opcodes.AASTORE;
import static jodd.asm5.Opcodes.ANEWARRAY;
import static jodd.asm5.Opcodes.DUP;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadMethodArgumentAsObject;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadMethodArgumentClass;
import static jodd.proxetta.asm.ProxettaAsmUtil.pushInt;

/**
 * Replacements methods for {@link jodd.proxetta.ProxyTarget} methods.
 */
public class ProxyTargetReplacement {

	/**
	 * Visits replacement code for {@link ProxyTarget#argumentsCount()}.
	 */
	public static void argumentsCount(MethodVisitor mv, MethodInfo methodInfo) {
		int argsCount = methodInfo.getArgumentsCount();
		pushInt(mv, argsCount);
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#targetMethodName()}.
	 */
	public static void targetMethodName(MethodVisitor mv, MethodInfo methodInfo) {
		mv.visitLdcInsn(methodInfo.getMethodName());
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#targetMethodSignature()}.
	 */
	public static void targetMethodSignature(MethodVisitor mv, MethodInfo methodInfo) {
		mv.visitLdcInsn(methodInfo.getSignature());
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#targetMethodDescription()}.
	 */
	public static void targetMethodDescription(MethodVisitor mv, MethodInfo methodInfo) {
		mv.visitLdcInsn(methodInfo.getDescription());
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#createArgumentsArray()}.
	 */
	public static void createArgumentsArray(MethodVisitor mv, MethodInfo methodInfo) {
		int argsCount = methodInfo.getArgumentsCount();
		pushInt(mv, argsCount);
		mv.visitTypeInsn(ANEWARRAY, AsmUtil.SIGNATURE_JAVA_LANG_OBJECT);
		for (int i = 0; i < argsCount; i++) {
			mv.visitInsn(DUP);
			pushInt(mv, i);
			loadMethodArgumentAsObject(mv, methodInfo, i + 1);
			mv.visitInsn(AASTORE);
		}
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#createArgumentsClassArray()}.
	 */
	public static void createArgumentsClassArray(MethodVisitor mv, MethodInfo methodInfo) {
		int argsCount = methodInfo.getArgumentsCount();
		pushInt(mv, argsCount);
		mv.visitTypeInsn(ANEWARRAY, AsmUtil.SIGNATURE_JAVA_LANG_CLASS);
		for (int i = 0; i < argsCount; i++) {
			mv.visitInsn(DUP);
			pushInt(mv, i);
			loadMethodArgumentClass(mv, methodInfo, i + 1);
			mv.visitInsn(AASTORE);
		}
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#returnType()}.
	 */
	public static void returnType(MethodVisitor mv, MethodInfo methodInfo) {
		ProxettaAsmUtil.loadClass(mv, methodInfo.getReturnOpcodeType(), methodInfo.getReturnTypeName());
	}

}