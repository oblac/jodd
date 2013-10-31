// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.directaccess;

import jodd.asm.AsmUtil;
import jodd.asm4.ClassWriter;
import jodd.asm4.MethodVisitor;
import jodd.asm4.Opcodes;
import jodd.util.ClassLoaderUtil;

import java.lang.reflect.Method;

import static jodd.asm4.Opcodes.CHECKCAST;
import static jodd.asm4.Opcodes.INVOKEVIRTUAL;

/**
 *
 */
public class SetterMethodInvokerClassBuilder {

	protected static int classCounter = 1;

	/**
	 * Creates a new {@link SetterMethodInvoker} class and an instance.
	 * @see #create(java.lang.reflect.Method)
	 */
	public static SetterMethodInvoker createNewInstance(Method method) throws IllegalAccessException, InstantiationException {
		Class<SetterMethodInvoker> methodInvokerClass = create(method);

		return methodInvokerClass.newInstance();
	}

	/**
	 * Creates a {@link SetterMethodInvoker} implementation class for given method.
	 * Classes are <b>not</b> cached, so new one is created every time.
	 */
	@SuppressWarnings("unchecked")
	public static Class<SetterMethodInvoker> create(Method method) {

		String className = MethodInvoker.class.getName() + '$' + method.getName() + '$' + classCounter;

		classCounter++;

		byte[] classBytes = define(method, className);

		return (Class<SetterMethodInvoker>) ClassLoaderUtil.defineClass(className, classBytes);
	}

	/**
	 * Bytecode generation.
	 */
	protected static byte[] define(Method method, String targetClassName) {
		final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
				AsmUtil.typeToSignature(targetClassName),
				null,
				AsmUtil.SIGNATURE_JAVA_LANG_OBJECT,
				new String[]{SetterMethodInvoker.class.getName().replace('.', '/')});

		MethodVisitor mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "<init>", "()V", null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, AsmUtil.SIGNATURE_JAVA_LANG_OBJECT, "<init>", "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);

		// cast to declaring class
		Class declaringClass = method.getDeclaringClass();
		String declaringClassSignature = AsmUtil.typeToSignature(declaringClass);

		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, declaringClassSignature);

		// process parameters
		Class parameterType = method.getParameterTypes()[0];

		mv.visitVarInsn(Opcodes.ALOAD, 2);

		if (parameterType.isPrimitive()) {
			if (parameterType == int.class) {
				AsmUtil.intValue(mv);
			} else if (parameterType == long.class) {
				AsmUtil.longValue(mv);
			} else if (parameterType == float.class) {
				AsmUtil.floatValue(mv);
			} else if (parameterType == double.class) {
				AsmUtil.doubleValue(mv);
			} else if (parameterType == byte.class) {
				AsmUtil.byteValue(mv);
			} else if (parameterType == short.class) {
				AsmUtil.shortValue(mv);
			} else if (parameterType == boolean.class) {
				AsmUtil.booleanValue(mv);
			} else if (parameterType == char.class) {
				AsmUtil.charValue(mv);
			}
		} else {
			mv.visitTypeInsn(CHECKCAST, AsmUtil.typeToSignature(parameterType));
		}

		// return type
		String methodDescription = '(' + AsmUtil.typeToTyperef(parameterType) + ')';

		Class returnType = method.getReturnType();
		methodDescription += AsmUtil.typeToTyperef(returnType);

		// invoke
		mv.visitMethodInsn(INVOKEVIRTUAL, declaringClassSignature, method.getName(), methodDescription);

		// return
		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();

		classWriter.visitEnd();

		return classWriter.toByteArray();
	}

}