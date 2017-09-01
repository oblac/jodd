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
import jodd.asm5.Label;
import jodd.asm5.MethodVisitor;
import jodd.asm5.Type;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.TypeInfo;
import jodd.util.ClassUtil;
import jodd.util.StringBand;
import jodd.util.StringPool;

import java.lang.reflect.Method;

import static jodd.asm5.Opcodes.*;
import static jodd.proxetta.JoddProxetta.fieldDivider;
import static jodd.proxetta.JoddProxetta.fieldPrefix;
import static jodd.proxetta.JoddProxetta.methodDivider;
import static jodd.proxetta.JoddProxetta.methodPrefix;
import static jodd.util.StringPool.COLON;

/**
 * Various ASM utilities used by {@link jodd.proxetta.Proxetta}.
 * For more generic ASM tools, see {@link jodd.asm.AsmUtil}.
 */
public class ProxettaAsmUtil {

	public static final String INIT = "<init>";
	public static final String CLINIT = "<clinit>";
	public static final String DESC_VOID = "()V";

	// ---------------------------------------------------------------- misc

	/**
	 * Pushes int value in an optimal way.
	 */
	public static void pushInt(MethodVisitor mv, int value) {
		if (value <= 5) {
			mv.visitInsn(ICONST_0 + value);
		} else if (value <= Byte.MAX_VALUE) {
			mv.visitIntInsn(BIPUSH, value);
		}  else {
			mv.visitIntInsn(SIPUSH, value);
		}
	}

	/**
	 * Changes method access to private and final.
	 */
	public static int makePrivateFinalAccess(int access) {
		return (access & 0xFFFFFFF0) | AsmUtil.ACC_PRIVATE | AsmUtil.ACC_FINAL;
	}

	/**
	 * Validates argument index.
	 */
	public static void checkArgumentIndex(MethodInfo methodInfo, int argIndex) {
		if ((argIndex < 1) || (argIndex > methodInfo.getArgumentsCount())) {
			throw new ProxettaException("Invalid argument index: " + argIndex);
		}
	}

	/**
	 * Builds advice field name.
	 */
	public static String adviceFieldName(String name, int index) {
		return fieldPrefix + name + fieldDivider + index;
	}

	/**
	 * Builds advice method name.
	 */
	public static String adviceMethodName(String name, int index) {
		return methodPrefix + name + methodDivider + index;
	}

	// ---------------------------------------------------------------- load

	public static void loadMethodArgumentClass(MethodVisitor mv, MethodInfo methodInfo, int index) {
		TypeInfo argument = methodInfo.getArgument(index);
		loadClass(mv, argument.getOpcode(), argument.getRawName());
	}

	public static void loadClass(MethodVisitor mv, int type, String typeName) {
		switch (type) {
			case 'V':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_VOID, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'B':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_BYTE, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'C':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_CHARACTER, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'S':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_SHORT, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'I':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_INTEGER, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'Z':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_BOOLEAN, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'J':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_LONG, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'F':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_FLOAT, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'D':
				mv.visitFieldInsn(GETSTATIC, AsmUtil.SIGNATURE_JAVA_LANG_DOUBLE, "TYPE", AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			default:
				mv.visitLdcInsn(Type.getType(typeName));
				break;
		}

	}

	/**
	 * Loads all method arguments before INVOKESPECIAL call.
	 */
	public static void loadSpecialMethodArguments(MethodVisitor mv, MethodInfo methodInfo) {
		mv.visitVarInsn(ALOAD, 0);
		for (int i = 1; i <= methodInfo.getArgumentsCount(); i++) {
			loadMethodArgument(mv, methodInfo, i);
		}
	}

	/**
	 * Loads all method arguments before INVOKESTATIC call.
	 */
	public static void loadStaticMethodArguments(MethodVisitor mv, MethodInfo methodInfo) {
		for (int i = 0; i < methodInfo.getArgumentsCount(); i++) {
			loadMethodArgument(mv, methodInfo, i);
		}
	}

	/**
	 * Loads all method arguments before INVOKEVIRTUAL call.
	 */
	public static void loadVirtualMethodArguments(MethodVisitor mv, MethodInfo methodInfo) {
		for (int i = 1; i <= methodInfo.getArgumentsCount(); i++) {
			loadMethodArgument(mv, methodInfo, i);
		}
	}

	/**
	 * Loads one argument. Index is 1-based. No conversion occurs.
	 */
	public static void loadMethodArgument(MethodVisitor mv, MethodInfo methodInfo, int index) {
		int offset = methodInfo.getArgumentOffset(index);
		int type = methodInfo.getArgument(index).getOpcode();
		switch (type) {
			case 'V':
				break;
			case 'B':
			case 'C':
			case 'S':
			case 'I':
			case 'Z':
				mv.visitVarInsn(ILOAD, offset);
				break;
			case 'J':
				mv.visitVarInsn(LLOAD, offset);
				break;
			case 'F':
				mv.visitVarInsn(FLOAD, offset);
				break;
			case 'D':
				mv.visitVarInsn(DLOAD, offset);
				break;
			default:
				mv.visitVarInsn(ALOAD, offset);
		}
	}


	public static void loadMethodArgumentAsObject(MethodVisitor mv, MethodInfo methodInfo, int index) {
		int offset = methodInfo.getArgumentOffset(index);
		int type = methodInfo.getArgument(index).getOpcode();
		switch (type) {
			case 'V':
				break;
			case 'B':
				mv.visitVarInsn(ILOAD, offset);
				AsmUtil.valueOfByte(mv);
				break;
			case 'C':
				mv.visitVarInsn(ILOAD, offset);
				AsmUtil.valueOfCharacter(mv);
				break;
			case 'S':
				mv.visitVarInsn(ILOAD, offset);
				AsmUtil.valueOfShort(mv);
				break;
			case 'I':
				mv.visitVarInsn(ILOAD, offset);
				AsmUtil.valueOfInteger(mv);
				break;
			case 'Z':
				mv.visitVarInsn(ILOAD, offset);
				AsmUtil.valueOfBoolean(mv);
				break;
			case 'J':
				mv.visitVarInsn(LLOAD, offset);
				AsmUtil.valueOfLong(mv);
				break;
			case 'F':
				mv.visitVarInsn(FLOAD, offset);
				AsmUtil.valueOfFloat(mv);
				break;
			case 'D':
				mv.visitVarInsn(DLOAD, offset);
				AsmUtil.valueOfDouble(mv);
				break;
			default:
				mv.visitVarInsn(ALOAD, offset);
		}
	}

	// ---------------------------------------------------------------- store

	/**
	 * Stores one argument. Index is 1-based. No conversion occurs.
	 */
	public static void storeMethodArgument(MethodVisitor mv, MethodInfo methodInfo, int index) {
		int offset = methodInfo.getArgumentOffset(index);
		int type = methodInfo.getArgument(index).getOpcode();
		switch (type) {
			case 'V':
				break;
			case 'B':
			case 'C':
			case 'S':
			case 'I':
			case 'Z':
				mv.visitVarInsn(ISTORE, offset); break;
			case 'J':
				mv.visitVarInsn(LSTORE, offset); break;
			case 'F':
				mv.visitVarInsn(FSTORE, offset); break;
			case 'D':
				mv.visitVarInsn(DSTORE, offset); break;
			default:
				mv.visitVarInsn(ASTORE, offset);
		}
	}

	/**
	 * Returns <code>true</code> if opcode is xSTORE.
	 */
	public static boolean isStoreOpcode(int opcode) {
		return (opcode == ISTORE)
				|| (opcode == LSTORE)
				|| (opcode == FSTORE)
				|| (opcode == DSTORE)
				|| (opcode == ASTORE);
	}


	public static void storeMethodArgumentFromObject(MethodVisitor mv, MethodInfo methodInfo, int index) {
		int type = methodInfo.getArgument(index).getOpcode();
		int offset = methodInfo.getArgumentOffset(index);
		storeValue(mv, offset, type);
	}

	public static void storeValue(MethodVisitor mv, int offset, int type) {
		switch (type) {
			case 'V':
				break;
			case 'B':
				AsmUtil.byteValue(mv);
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'C':
				AsmUtil.charValue(mv);
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'S':
				AsmUtil.shortValue(mv);
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'I':
				AsmUtil.intValue(mv);
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'Z':
				AsmUtil.booleanValue(mv);
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'J':
				AsmUtil.longValue(mv);
				mv.visitVarInsn(LSTORE, offset);
				break;
			case 'F':
				AsmUtil.floatValue(mv);
				mv.visitVarInsn(FSTORE, offset);
				break;
			case 'D':
				AsmUtil.doubleValue(mv);
				mv.visitVarInsn(DSTORE, offset);
				break;
			default:
				mv.visitVarInsn(ASTORE, offset);
		}
	}

	// ---------------------------------------------------------------- return

	/**
	 * Visits return opcodes.
	 */
	public static void visitReturn(MethodVisitor mv, MethodInfo methodInfo, boolean isLast) {
		switch (methodInfo.getReturnType().getOpcode()) {
			case 'V':
				if (isLast) {
					mv.visitInsn(POP);
				}
				mv.visitInsn(RETURN);
				break;

			case 'B':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
						mv.visitInsn(IRETURN);
					mv.visitLabel(label);

					AsmUtil.byteValue(mv);
				}
				mv.visitInsn(IRETURN);
				break;

			case 'C':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
						mv.visitInsn(IRETURN);
					mv.visitLabel(label);

					AsmUtil.charValue(mv);
				}
				mv.visitInsn(IRETURN);
				break;

			case 'S':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
						mv.visitInsn(IRETURN);
					mv.visitLabel(label);

					AsmUtil.shortValue(mv);
				}
				mv.visitInsn(IRETURN);
				break;

			case 'I':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
						mv.visitInsn(IRETURN);
					mv.visitLabel(label);

					AsmUtil.intValue(mv);
				}
				mv.visitInsn(IRETURN);
				break;

			case 'Z':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
						mv.visitInsn(IRETURN);
					mv.visitLabel(label);

					AsmUtil.booleanValue(mv);
				}
				mv.visitInsn(IRETURN);
				break;

			case 'J':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(LCONST_0);
						mv.visitInsn(LRETURN);
					mv.visitLabel(label);

					AsmUtil.longValue(mv);
				}
				mv.visitInsn(LRETURN);
				break;

			case 'F':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(FCONST_0);
						mv.visitInsn(FRETURN);
					mv.visitLabel(label);

					AsmUtil.floatValue(mv);
				}
				mv.visitInsn(FRETURN);
				break;

			case 'D':
				if (isLast) {
					mv.visitInsn(DUP);
					Label label = new Label();
					mv.visitJumpInsn(IFNONNULL, label);
						mv.visitInsn(POP);
						mv.visitInsn(DCONST_0);
						mv.visitInsn(DRETURN);
					mv.visitLabel(label);

					AsmUtil.doubleValue(mv);
				}
				mv.visitInsn(DRETURN);
				break;

			default:
				mv.visitInsn(ARETURN);
				break;
		}
	}

	/**
	 * Prepares return value.
	 */
	public static void prepareReturnValue(MethodVisitor mv, MethodInfo methodInfo, int varOffset) {
		varOffset += methodInfo.getAllArgumentsSize();
		switch (methodInfo.getReturnType().getOpcode()) {
			case 'V':
				mv.visitInsn(ACONST_NULL);
				break;
			case 'B':
				AsmUtil.valueOfByte(mv);
				break;
			case 'C':
				AsmUtil.valueOfCharacter(mv);
				break;
			case 'S':
				AsmUtil.valueOfShort(mv);
				break;
			case 'I':
				AsmUtil.valueOfInteger(mv);
				break;
			case 'Z':
				AsmUtil.valueOfBoolean(mv);
				break;
			case 'J':
				AsmUtil.valueOfLong(mv);
				break;
			case 'F':
				AsmUtil.valueOfFloat(mv);
				break;
			case 'D':
				AsmUtil.valueOfDouble(mv);
				break;

		}
	}

	public static void castToReturnType(MethodVisitor mv, MethodInfo methodInfo) {
		final String returnType;

		char returnOpcodeType = methodInfo.getReturnType().getOpcode();

		switch (returnOpcodeType) {
			case 'I':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_INTEGER;
				break;
			case 'J':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_LONG;
				break;
			case 'S':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_SHORT;
				break;
			case 'B':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_BYTE;
				break;
			case 'Z':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_BOOLEAN;
				break;
			case 'F':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_FLOAT;
				break;
			case 'D':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_DOUBLE;
				break;
			case 'C':
				returnType = AsmUtil.SIGNATURE_JAVA_LANG_CHARACTER;
				break;
			case '[':
				returnType = methodInfo.getReturnType().getRawName();
				break;
			default:
				String rtname = methodInfo.getReturnType().getRawName();
				returnType = rtname.length() == 0 ?
					AsmUtil.typeToSignature(methodInfo.getReturnType().getType()) :
					AsmUtil.typedesc2ClassName(rtname);
				break;
		}

		mv.visitTypeInsn(CHECKCAST, returnType);
	}

	// ---------------------------------------------------------------- method signature


	/**
	 * Creates unique key for method signatures map.
	 */
	public static String createMethodSignaturesKey(int access, String methodName, String description, String className) {
		return new StringBand(7)
			.append(access)
			.append(COLON)
			.append(description)
			.append(StringPool.UNDERSCORE)
			.append(className)
			.append(StringPool.HASH)
			.append(methodName)
			.toString();
	}

	// ---------------------------------------------------------------- annotation work

	/**
	 * Visits non-array element value for annotation. Returns <code>true</code>
	 * if value is successfully processed.
	 */
	public static void visitElementValue(MethodVisitor mv, Object elementValue, boolean boxPrimitives) {
		if (elementValue instanceof String) {	// string
			mv.visitLdcInsn(elementValue);
			return;
		}
		if (elementValue instanceof Type) {		// class
			mv.visitLdcInsn(elementValue);
			return;
		}
		if (elementValue instanceof Class) {
			mv.visitLdcInsn(Type.getType((Class) elementValue));
			return;
		}

		// primitives

		if (elementValue instanceof Integer) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfInteger(mv);
			}
			return;
		}
		if (elementValue instanceof Long) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfLong(mv);
			}
			return;
		}
		if (elementValue instanceof Short) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfShort(mv);
			}
			return;
		}
		if (elementValue instanceof Byte) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfByte(mv);
			}
			return;
		}
		if (elementValue instanceof Float) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfFloat(mv);
			}
			return;
		}
		if (elementValue instanceof Double) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfDouble(mv);
			}
			return;
		}
		if (elementValue instanceof Character) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfCharacter(mv);
			}
			return;
		}
		if (elementValue instanceof Boolean) {
			mv.visitLdcInsn(elementValue);
			if (boxPrimitives) {
				AsmUtil.valueOfBoolean(mv);
			}
			return;
		}

		// enum

		Class elementValueClass = elementValue.getClass();
		Class enumClass = ClassUtil.findEnum(elementValueClass);

		if (enumClass != null) {
			try {
				String typeRef = AsmUtil.typeToTyperef(enumClass);

				// invoke
				Method nameMethod = elementValue.getClass().getMethod("name");
				String name = (String) nameMethod.invoke(elementValue);

				mv.visitFieldInsn(GETSTATIC, typeRef, name, typeRef);

				return;
			} catch (Exception ignore) {
			}
		}

		throw new ProxettaException("Unsupported annotation type: " + elementValue.getClass());
	}

	// ---------------------------------------------------------------- array

	/**
	 * Creates new array.
	 */
	public static void newArray(MethodVisitor mv, Class componentType) {
		if (componentType == int.class) {
			mv.visitIntInsn(NEWARRAY, T_INT);
			return;
		}
		if (componentType == long.class) {
			mv.visitIntInsn(NEWARRAY, T_LONG);
			return;
		}
		if (componentType == float.class) {
			mv.visitIntInsn(NEWARRAY, T_FLOAT);
			return;
		}
		if (componentType == double.class) {
			mv.visitIntInsn(NEWARRAY, T_DOUBLE);
			return;
		}
		if (componentType == byte.class) {
			mv.visitIntInsn(NEWARRAY, T_BYTE);
			return;
		}
		if (componentType == short.class) {
			mv.visitIntInsn(NEWARRAY, T_SHORT);
			return;
		}
		if (componentType == boolean.class) {
			mv.visitIntInsn(NEWARRAY, T_BOOLEAN);
			return;
		}
		if (componentType == char.class) {
			mv.visitIntInsn(NEWARRAY, T_CHAR);
			return;
		}

		mv.visitTypeInsn(ANEWARRAY, AsmUtil.typeToSignature(componentType));
	}

	/**
	 * Stores element on stack into an array.
	 */
	public static void storeIntoArray(MethodVisitor mv, Class componentType) {
		if (componentType == int.class) {
			mv.visitInsn(IASTORE);
			return;
		}
		if (componentType == long.class) {
			mv.visitInsn(LASTORE);
			return;
		}
		if (componentType == float.class) {
			mv.visitInsn(FASTORE);
			return;
		}
		if (componentType == double.class) {
			mv.visitInsn(DASTORE);
			return;
		}
		if (componentType == byte.class) {
			mv.visitInsn(BASTORE);
			return;
		}
		if (componentType == short.class) {
			mv.visitInsn(SASTORE);
			return;
		}
		if (componentType == boolean.class) {
			mv.visitInsn(BASTORE);
			return;
		}
		if (componentType == char.class) {
			mv.visitInsn(CASTORE);
			return;
		}

		mv.visitInsn(AASTORE);
	}

	// ---------------------------------------------------------------- detect advice macros

	public static boolean isInvokeMethod(String name, String desc) {
		if (name.equals("invoke")) {
			if (desc.equals("()Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArgumentsCountMethod(String name, String desc) {
		if (name.equals("argumentsCount")) {
			if (desc.equals("()I")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArgumentTypeMethod(String name, String desc) {
		if (name.equals("argumentType")) {
			if (desc.equals("(I)Ljava/lang/Class;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArgumentMethod(String name, String desc) {
		if (name.equals("argument")) {
			if (desc.equals("(I)Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSetArgumentMethod(String name, String desc) {
		if (name.equals("setArgument")) {
			if (desc.equals("(Ljava/lang/Object;I)V")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCreateArgumentsArrayMethod(String name, String desc) {
		if (name.equals("createArgumentsArray")) {
			if (desc.equals("()[Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCreateArgumentsClassArrayMethod(String name, String desc) {
		if (name.equals("createArgumentsClassArray")) {
			if (desc.equals("()[Ljava/lang/Class;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isReturnTypeMethod(String name, String desc) {
		if (name.equals("returnType")) {
			if (desc.equals("()Ljava/lang/Class;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTargetMethod(String name, String desc) {
		if (name.equals("target")) {
			if (desc.equals("()Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}


	public static boolean isTargetClassMethod(String name, String desc) {
		if (name.equals("targetClass")) {
			if (desc.equals("()Ljava/lang/Class;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTargetMethodNameMethod(String name, String desc) {
		if (name.equals("targetMethodName")) {
			if (desc.equals("()Ljava/lang/String;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTargetMethodSignatureMethod(String name, String desc) {
		if (name.equals("targetMethodSignature")) {
			if (desc.equals("()Ljava/lang/String;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTargetMethodDescriptionMethod(String name, String desc) {
		if (name.equals("targetMethodDescription")) {
			if (desc.equals("()Ljava/lang/String;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isReturnValueMethod(String name, String desc) {
		if (name.equals("returnValue")) {
			if (desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInfoMethod(String name, String desc) {
		if (name.equals("info")) {
			if (desc.equals("()Ljodd/proxetta/ProxyTargetInfo;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTargetMethodAnnotationMethod(String name, String desc) {
		if (name.equals("targetMethodAnnotation")) {
			if (desc.equals("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTargetClassAnnotationMethod(String name, String desc) {
		if (name.equals("targetClassAnnotation")) {
			if (desc.equals("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;")) {
				return true;
			}
		}
		return false;
	}

}
