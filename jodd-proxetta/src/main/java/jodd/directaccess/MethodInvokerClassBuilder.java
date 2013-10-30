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
 * Bytecode generator of {@link MethodInvoker} implementations.
 */
public class MethodInvokerClassBuilder {

	protected static int classCounter = 1;

	/**
	 * Creates a new {@link MethodInvoker} class and an instance.
	 * @see #create(java.lang.reflect.Method)
	 */
	public static MethodInvoker createNewInstance(Method method) throws IllegalAccessException, InstantiationException {
		Class<MethodInvoker> methodInvokerClass = create(method);

		return methodInvokerClass.newInstance();
	}

	/**
	 * Creates a {@link MethodInvoker} implementation class for given method.
	 * Classes are <b>not</b> cached, so new one is created every time.
	 */
	@SuppressWarnings("unchecked")
	public static Class<MethodInvoker> create(Method method) {

		String className = MethodInvokerClassBuilder.class.getPackage().getName() + ".MethodInvoker$" + method.getName() + '$' + classCounter;

		classCounter++;

		byte[] classBytes = define(method, className);

		return (Class<MethodInvoker>) ClassLoaderUtil.defineClass(className, classBytes);
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
				new String[]{MethodInvoker.class.getName().replace('.', '/')});

		MethodVisitor mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "<init>", "()V", null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, AsmUtil.SIGNATURE_JAVA_LANG_OBJECT, "<init>", "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);

		// cast to declaring class
		Class declaringClass = method.getDeclaringClass();
		String declaringClassSignature = AsmUtil.typeToSignature(declaringClass);

		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, declaringClassSignature);

		// process parameters
		Class[] parameterTypes = method.getParameterTypes();
		String methodDescription = "(";

		for (int i = 0; i < parameterTypes.length; i++) {
			Class parameterType = parameterTypes[i];

			methodDescription += AsmUtil.typeToTyperef(parameterType);

			mv.visitVarInsn(Opcodes.ALOAD, 2);
			if (i <= 5) {
				mv.visitInsn(Opcodes.ICONST_0 + i);
			} else {
				mv.visitIntInsn(Opcodes.BIPUSH, i);
			}
			mv.visitInsn(Opcodes.AALOAD);

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
		}

		// return type
		methodDescription += ")";

		Class returnType = method.getReturnType();
		methodDescription += AsmUtil.typeToTyperef(returnType);

		// invoke
		mv.visitMethodInsn(INVOKEVIRTUAL, declaringClassSignature, method.getName(), methodDescription);

		// return
		if (returnType == void.class) {
			mv.visitInsn(Opcodes.ACONST_NULL);
		} else if (returnType.isPrimitive()) {
			if (returnType == int.class) {
				AsmUtil.valueOfInteger(mv);
			} else if (returnType == long.class) {
				AsmUtil.valueOfLong(mv);
			} else if (returnType == float.class) {
				AsmUtil.valueOfFloat(mv);
			} else if (returnType == double.class) {
				AsmUtil.valueOfDouble(mv);
			} else if (returnType == byte.class) {
				AsmUtil.valueOfByte(mv);
			} else if (returnType == short.class) {
				AsmUtil.valueOfShort(mv);
			} else if (returnType == boolean.class) {
				AsmUtil.valueOfBoolean(mv);
			} else if (returnType == char.class) {
				AsmUtil.valueOfCharacter(mv);
			}
		}
		mv.visitInsn(Opcodes.ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();

		classWriter.visitEnd();

		return classWriter.toByteArray();
	}

}