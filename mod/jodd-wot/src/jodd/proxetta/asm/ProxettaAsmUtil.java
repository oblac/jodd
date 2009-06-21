// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.AsmConsts;
import static jodd.proxetta.asm.ProxettaNaming.*;
import jodd.util.StringPool;
import static jodd.util.StringPool.COLON;

/**
 * Various ASM utilities used by {@link jodd.proxetta.Proxetta}.
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
		return (access & 0xFFFFFFF0) | MethodInfo.ACC_PRIVATE | MethodInfo.ACC_FINAL;
	}

	/**
	 * Removes native method access flag.
	 */
	public static int makeNonNative(int access) {
		return (access & ~MethodInfo.ACC_NATIVE);
	}

	/**
	 * Validates argument index.
	 */
	public static void checkArgumentIndex(MethodSignatureVisitor msign, int argIndex, Class<? extends ProxyAdvice> advice) {
		if ((argIndex < 1) || (argIndex > msign.getArgumentsCount())) {
			throw new ProxettaException("Invalid argument index: '" + argIndex + "' used in advice: " + advice.getName());
		}
	}

	/**
	 * Utility method that converts type to character.
	 */
	public static String strtype(int type) {
		return Character.toString((char) type);
	}

	/**
	 * Builds advice field name.
	 */
	public static String adviceFieldName(String name, int index) {
		return FIELD_PREFIX + name + FIELD_DIVIDER + index;
	}

	/**
	 * Builds advice method name.
	 */
	public static String adviceMethodName(String name, int index) {
		return METHOD_PREFIX + name + METHOD_DIVIDER + index;
	}



	// ---------------------------------------------------------------- load

	public static void loadMethodArgumentClass(MethodVisitor mv, MethodSignatureVisitor msign, int index) {
		loadClass(mv, msign.getArgumentOpcodeType(index), msign.getArgumentTypeName(index));
	}

	public static void loadMethodReturnClass(MethodVisitor mv, MethodSignatureVisitor msign) {
		loadClass(mv, msign.getReturnOpcodeType(), msign.getReturnTypeName());
	}

	public static void loadClass(MethodVisitor mv, int type, String typeName) {
		switch (type) {
			case 'V':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
				break;
			case 'B':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
				break;
			case 'C':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
				break;
			case 'S':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
				break;
			case 'I':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
				break;
			case 'Z':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
				break;
			case 'J':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
				break;
			case 'F':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
				break;
			case 'D':
				mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
				break;
			default:
				mv.visitLdcInsn(Type.getType(typeName));
				break;
		}

	}

	/**
	 * Loads all method arguments before method call.
	 */
	public static void loadMethodArguments(MethodVisitor mv, MethodSignatureVisitor msign) {
		mv.visitVarInsn(ALOAD, 0);
		for (int i = 1; i <= msign.getArgumentsCount(); i++) {
			loadMethodArgument(mv, msign, i);
		}
	}

	/**
	 * Loads one argument. Index is 1-based. No conversion occurs.
	 */
	public static void loadMethodArgument(MethodVisitor mv, MethodSignatureVisitor msign, int index) {
		int offset = msign.getArgumentOffset(index);
		int type = msign.getArgumentOpcodeType(index);
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


	public static void loadMethodArgumentAsObject(MethodVisitor mv, MethodSignatureVisitor msign, int index) {
		int offset = msign.getArgumentOffset(index);
		int type = msign.getArgumentOpcodeType(index);
		switch (type) {
			case 'V':
				break;
			case 'B':
				mv.visitTypeInsn(NEW, "java/lang/Byte");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Byte", "<init>", "(B)V");
				break;
			case 'C':
				mv.visitTypeInsn(NEW, "java/lang/Character");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Character", "<init>", "(C)V");
				break;
			case 'S':
				mv.visitTypeInsn(NEW, "java/lang/Short");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Short", "<init>", "(S)V");
				break;
			case 'I':
				mv.visitTypeInsn(NEW, "java/lang/Integer");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V");
				break;
			case 'Z':
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
				break;
			case 'J':
				mv.visitTypeInsn(NEW, "java/lang/Long");
				mv.visitInsn(DUP);
				mv.visitVarInsn(LLOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V");
				break;
			case 'F':
				mv.visitTypeInsn(NEW, "java/lang/Float");
				mv.visitInsn(DUP);
				mv.visitVarInsn(FLOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Float", "<init>", "(F)V");
				break;
			case 'D':
				mv.visitTypeInsn(NEW, "java/lang/Double");
				mv.visitInsn(DUP);
				mv.visitVarInsn(DLOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V");
				break;
			default:
				mv.visitVarInsn(ALOAD, offset);
		}
	}

	// ---------------------------------------------------------------- store

	/**
	 * Stores one argument. Index is 1-based. No conversion occurs.
	 */
	public static void storeMethodArgument(MethodVisitor mv, MethodSignatureVisitor msign, int index) {
		int offset = msign.getArgumentOffset(index);
		int type = msign.getArgumentOpcodeType(index);
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


	public static void storeMethodArgumentFromObject(MethodVisitor mv, MethodSignatureVisitor msign, int index) {
		int type = msign.getArgumentOpcodeType(index);
		int offset = msign.getArgumentOffset(index);
		storeValue(mv, offset, type);
	}

	public static void storeValue(MethodVisitor mv, int offset, int type) {
		switch (type) {
			case 'V':
				break;
			case 'B':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "byteValue()B");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'C':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "charValue()C");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'S':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "shortValue()S");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'I':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "intValue()I");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'Z':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "booleanValue()Z");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'J':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "longValue()J");
				mv.visitVarInsn(LSTORE, offset);
				break;
			case 'F':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "floatValue()F");
				mv.visitVarInsn(FSTORE, offset);
				break;
			case 'D':
				mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "doubleValue()D");
				mv.visitVarInsn(DSTORE, offset);
				break;
			default:
				mv.visitVarInsn(ASTORE, offset);
		}
	}

	// ---------------------------------------------------------------- return



	public static void visitReturn(MethodVisitor mv, MethodSignatureVisitor msign, boolean isLast) {
		switch (msign.getReturnOpcodeType()) {
			case 'V':
				if (isLast == true) {
					mv.visitInsn(POP);
				}
				mv.visitInsn(RETURN);
				break;
			case 'B':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
				}
				mv.visitInsn(IRETURN);
				break;
			case 'C':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
				}
				mv.visitInsn(IRETURN);
				break;
			case 'S':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
				}
				mv.visitInsn(IRETURN);
				break;
			case 'I':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
				}
				mv.visitInsn(IRETURN);
				break;
			case 'Z':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
				}
				mv.visitInsn(IRETURN);
				break;
			case 'J':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
				}
				mv.visitInsn(LRETURN);
				break;
			case 'F':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
				}
				mv.visitInsn(FRETURN);
				break;
			case 'D':
				if (isLast == true) {
					mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
				}
				mv.visitInsn(DRETURN);
				break;
			default:
				mv.visitInsn(ARETURN);
				break;
		}
	}


	public static void prepareReturnValue(MethodVisitor mv, MethodSignatureVisitor msign, int varOffset) {
		varOffset += msign.getAllArgumentsSize();
		switch (msign.getReturnOpcodeType()) {
			case 'V':
				mv.visitInsn(ACONST_NULL);
				break;
			case 'B':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Byte");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Byte", "<init>", "(B)V");
				break;
			case 'C':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Character");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Character", "<init>", "(C)V");
				break;
			case 'S':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Short");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Short", "<init>", "(S)V");
				break;
			case 'I':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Integer");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V");
				break;
			case 'Z':
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
				break;
			case 'J':
				mv.visitVarInsn(LSTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Long");
				mv.visitInsn(DUP);
				mv.visitVarInsn(LLOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V");
				break;
			case 'F':
				mv.visitVarInsn(FSTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Float");
				mv.visitInsn(DUP);
				mv.visitVarInsn(FLOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Float", "<init>", "(F)V");
				break;
			case 'D':
				mv.visitVarInsn(DSTORE, varOffset);
				mv.visitTypeInsn(NEW, "java/lang/Double");
				mv.visitInsn(DUP);
				mv.visitVarInsn(DLOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V");
				break;

		}
	}

	// ---------------------------------------------------------------- method signature


	/**
	 * Creates unique key for mathod signatures map.
	 */
	public static String createMethodSignaturesKey(int access, String methodName, String description, String className) {
		return new StringBuilder(100).append(access).append(COLON).append(description).append('_').
				append(className).append(StringPool.HASH).append(methodName).
				toString();
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

	// ---------------------------------------------------------------- converters

	/**
	 * Converts type reference to java-name.
	 */
	public static String typeref2Name(String desc) {
		if (desc.charAt(0) != AsmConsts.TYPE_REFERENCE) {
			throw new ProxettaException("Invalid type description/reference.");
		}
		String name = desc.substring(1, desc.length() - 1);
		return name.replace('/', '.');
	}
}
