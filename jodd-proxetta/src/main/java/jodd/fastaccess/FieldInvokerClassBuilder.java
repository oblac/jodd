package jodd.fastaccess;

import jodd.asm.AsmUtil;
import jodd.asm4.ClassWriter;
import jodd.asm4.MethodVisitor;
import jodd.asm4.Opcodes;
import jodd.io.FileUtil;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Bytecode generator of {@link FieldInvoker} implementations.
 */
public class FieldInvokerClassBuilder {

	protected static int classCounter = 1;

	/**
	 * Creates a new {@link FieldInvoker} class and an instance.
	 * @see #create(java.lang.reflect.Field)
	 */
	public static FieldInvoker createNewInstance(Field field) throws IllegalAccessException, InstantiationException {
		Class<FieldInvoker> fieldInvokerClass = create(field);

		return fieldInvokerClass.newInstance();
	}

	/**
	 * Creates a {@link FieldInvoker} implementation class for given method.
	 * Classes are <b>not</b> cached, so new one is created every time.
	 */
	@SuppressWarnings("unchecked")
	public static Class<FieldInvoker> create(Field field) {

		String className = FieldInvokerClassBuilder.class.getPackage().getName() + ".FieldInvoker$" + field.getName() + '$' + classCounter;

		classCounter++;

		byte[] classBytes = define(field, className);

		try {
			FileUtil.writeBytes("/Users/igor/A.class", classBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (Class<FieldInvoker>) ClassLoaderUtil.defineClass(className, classBytes);
	}


	/**
	 * Bytecode generation.
	 */
	protected static byte[] define(Field field, String targetClassName) {
		final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
				AsmUtil.typeToSignature(targetClassName),
				null,
				AsmUtil.SIGNATURE_JAVA_LANG_OBJECT,
				new String[]{FieldInvoker.class.getName().replace('.', '/')});

		MethodVisitor mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "<init>", "()V", null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, AsmUtil.SIGNATURE_JAVA_LANG_OBJECT, "<init>", "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// SET method
		mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);

		// cast to declaring class
		Class declaringClass = field.getDeclaringClass();
		String declaringClassSignature = AsmUtil.typeToSignature(declaringClass);

		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClassSignature);

		// process field
		Class fieldType = field.getType();
		mv.visitVarInsn(Opcodes.ALOAD, 2);

		if (fieldType.isPrimitive()) {
			if (fieldType == int.class) {
				AsmUtil.intValue(mv);
			} else if (fieldType == long.class) {
				AsmUtil.longValue(mv);
			} else if (fieldType == float.class) {
				AsmUtil.floatValue(mv);
			} else if (fieldType == double.class) {
				AsmUtil.doubleValue(mv);
			} else if (fieldType == byte.class) {
				AsmUtil.byteValue(mv);
			} else if (fieldType == short.class) {
				AsmUtil.shortValue(mv);
			} else if (fieldType == boolean.class) {
				AsmUtil.booleanValue(mv);
			} else if (fieldType == char.class) {
				AsmUtil.charValue(mv);
			}
		} else {
			mv.visitTypeInsn(Opcodes.CHECKCAST, AsmUtil.typeToSignature(fieldType));
		}

		mv.visitFieldInsn(Opcodes.PUTFIELD, declaringClassSignature, field.getName(), AsmUtil.typeToTyperef(fieldType));

		mv.visitInsn(Opcodes.RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();


		// GET method
		mv = classWriter.visitMethod(AsmUtil.ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);

		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClassSignature);

		mv.visitFieldInsn(Opcodes.GETFIELD, declaringClassSignature, field.getName(), AsmUtil.typeToTyperef(fieldType));

		if (fieldType.isPrimitive()) {
			if (fieldType == int.class) {
				AsmUtil.valueOfInteger(mv);
			} else if (fieldType == long.class) {
				AsmUtil.valueOfLong(mv);
			} else if (fieldType == float.class) {
				AsmUtil.valueOfFloat(mv);
			} else if (fieldType == double.class) {
				AsmUtil.valueOfDouble(mv);
			} else if (fieldType == byte.class) {
				AsmUtil.valueOfByte(mv);
			} else if (fieldType == short.class) {
				AsmUtil.valueOfShort(mv);
			} else if (fieldType == boolean.class) {
				AsmUtil.valueOfBoolean(mv);
			} else if (fieldType == char.class) {
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