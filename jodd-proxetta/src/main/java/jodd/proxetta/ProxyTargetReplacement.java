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

package jodd.proxetta;

import jodd.asm.AsmUtil;
import jodd.asm5.MethodVisitor;
import jodd.asm5.Opcodes;
import jodd.asm5.Type;
import jodd.proxetta.asm.ProxettaAsmUtil;
import jodd.util.ClassLoaderUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static jodd.asm5.Opcodes.AASTORE;
import static jodd.asm5.Opcodes.ANEWARRAY;
import static jodd.asm5.Opcodes.DUP;
import static jodd.asm5.Opcodes.POP;
import static jodd.proxetta.asm.ProxettaAsmUtil.checkArgumentIndex;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadMethodArgumentAsObject;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadMethodArgumentClass;
import static jodd.proxetta.asm.ProxettaAsmUtil.pushInt;

/**
 * Replacements methods for {@link jodd.proxetta.ProxyTarget} methods.
 */
public class ProxyTargetReplacement {

	public static final String PROXY_TARGET_INFO = "jodd/proxetta/ProxyTargetInfo";

	/**
	 * Visits replacement code for {@link ProxyTarget#argumentsCount()}.
	 */
	public static void argumentsCount(MethodVisitor mv, MethodInfo methodInfo) {
		int argsCount = methodInfo.getArgumentsCount();
		pushInt(mv, argsCount);
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#argumentType(int)}.
	 */
	public static void argumentType(MethodVisitor mv, MethodInfo methodInfo, int argIndex) {
		checkArgumentIndex(methodInfo, argIndex);
		mv.visitInsn(POP);
		loadMethodArgumentClass(mv, methodInfo, argIndex);
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#argument(int)}.
	 */
	public static void argument(MethodVisitor mv, MethodInfo methodInfo, int argIndex) {
		checkArgumentIndex(methodInfo, argIndex);
		mv.visitInsn(POP);
		loadMethodArgumentAsObject(mv, methodInfo, argIndex);
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
		ProxettaAsmUtil.loadClass(mv, methodInfo.getReturnType().getOpcode(), methodInfo.getReturnType().getName());
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
	 * Visits replacement code for {@link ProxyTarget#targetClass()}.
	 */
	public static void targetClass(MethodVisitor mv, MethodInfo methodInfo) {
		ClassInfo classInfo = methodInfo.getClassInfo();
		mv.visitLdcInsn(Type.getType('L' + classInfo.getReference() + ';'));
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#info()}.
	 */
	public static void info(MethodVisitor mv, MethodInfo methodInfo, int argsOff) {
		mv.visitTypeInsn(Opcodes.NEW, PROXY_TARGET_INFO);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, PROXY_TARGET_INFO, "<init>", "()V", false);

//		int argsOff = methodInfo.getAllArgumentsSize();
//		argsOff++;

		mv.visitVarInsn(Opcodes.ASTORE, argsOff);

		// argument count
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		argumentsCount(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "argumentCount", "I");

		// arguments class
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		createArgumentsClassArray(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "argumentsClasses", "[Ljava/lang/Class;");

		// arguments
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		createArgumentsArray(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "arguments", "[Ljava/lang/Object;");

		// return type
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		returnType(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "returnType", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);

		// target method name
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		targetMethodName(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "targetMethodName", AsmUtil.L_SIGNATURE_JAVA_LANG_STRING);

		// target method name
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		targetMethodDescription(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "targetMethodDescription", AsmUtil.L_SIGNATURE_JAVA_LANG_STRING);

		// target method name
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		targetMethodSignature(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "targetMethodSignature", AsmUtil.L_SIGNATURE_JAVA_LANG_STRING);

		// target class
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
		targetClass(mv, methodInfo);
		mv.visitFieldInsn(Opcodes.PUTFIELD, PROXY_TARGET_INFO, "targetClass", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);

		// the end
		mv.visitVarInsn(Opcodes.ALOAD, argsOff);
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#targetMethodAnnotation(String, String)}.
	 */
	public static void targetMethodAnnotation(MethodVisitor mv, MethodInfo methodInfo, String[] args) {
		AnnotationInfo[] anns = methodInfo.getAnnotations();

		if (anns != null) {
			targetAnnotation(mv, anns, args);
		}
	}

	/**
	 * Visits replacement code for {@link ProxyTarget#targetClassAnnotation(String, String)}.
	 */
	public static void targetClassAnnotation(MethodVisitor mv, ClassInfo classInfo, String[] args) {
		AnnotationInfo[] anns = classInfo.getAnnotations();

		if (anns != null) {
			targetAnnotation(mv, anns, args);
		} else {
			mv.visitInsn(Opcodes.ACONST_NULL);
		}
	}

	private static void targetAnnotation(MethodVisitor mv, AnnotationInfo[] anns, String[] args) {
		for (AnnotationInfo ann : anns) {
			String annotationSignature = ann.getAnnotationSignature();
			Method annotationMethod = null;

			if (annotationSignature.equals(args[0])) {
				String elementName = args[1];
				Object elementValue = ann.getElement(elementName);

				if (elementValue == null) {
					// read default annotation
					String annotationClass = ann.getAnnotationClassname();

					try {
						Class annotation = ClassLoaderUtil.loadClass(annotationClass);

						annotationMethod = annotation.getMethod(elementName);

						elementValue = annotationMethod.getDefaultValue();
					}
					catch (Exception ignore) {
						elementValue = null;
					}

					if (elementValue == null) {
						mv.visitInsn(Opcodes.ACONST_NULL);
						return;
					}
				}

				Class elementValueClass = elementValue.getClass();

				if (!elementValueClass.isArray()) {
					// non-arrays
					ProxettaAsmUtil.visitElementValue(mv, elementValue, true);
					return;
				}
				else {
					// arrays
					Class componentType = elementValueClass.getComponentType();

					String annotationClass = ann.getAnnotationClassname();

					try {
						if (annotationMethod == null) {
							Class annotation = ClassLoaderUtil.loadClass(annotationClass);

							annotationMethod = annotation.getMethod(elementName);
						}

						componentType = annotationMethod.getReturnType().getComponentType();
					}
					catch (Exception ignore) {
					}

					int size = Array.getLength(elementValue);

					ProxettaAsmUtil.pushInt(mv, size);

					ProxettaAsmUtil.newArray(mv, componentType);

					for (int i = 0; i < size; i++) {
						mv.visitInsn(DUP);

						ProxettaAsmUtil.pushInt(mv, i);

						Object value = Array.get(elementValue, i);
						ProxettaAsmUtil.visitElementValue(mv, value, false);

						ProxettaAsmUtil.storeIntoArray(mv, componentType);
					}

					return;
				}
			}
		}
	}
}