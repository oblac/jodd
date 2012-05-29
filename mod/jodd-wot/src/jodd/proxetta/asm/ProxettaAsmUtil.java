// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm.AsmConst;
import jodd.util.StringBand;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;
import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxettaException;
import static jodd.proxetta.asm.ProxettaNaming.*;
import jodd.util.StringPool;
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
		return (access & 0xFFFFFFF0) | AsmConst.ACC_PRIVATE | AsmConst.ACC_FINAL;
	}

	/**
	 * Removes native method access flag.
	 */
	public static int makeNonNative(int access) {
		return (access & ~AsmConst.ACC_NATIVE);
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
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_VOID, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'B':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_BYTE, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'C':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'S':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_SHORT, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'I':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_INTEGER, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'Z':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'J':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_LONG, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'F':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_FLOAT, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
				break;
			case 'D':
				mv.visitFieldInsn(GETSTATIC, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE, "TYPE", AsmConst.L_SIGNATURE_JAVA_LANG_CLASS);
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
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_BYTE);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_BYTE, "<init>", "(B)V");
				break;
			case 'C':
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER, "<init>", "(C)V");
				break;
			case 'S':
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_SHORT);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_SHORT, "<init>", "(S)V");
				break;
			case 'I':
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_INTEGER);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_INTEGER, "<init>", "(I)V");
				break;
			case 'Z':
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESTATIC, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN, "valueOf", "(Z)Ljava/lang/Boolean;");
				break;
			case 'J':
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_LONG);
				mv.visitInsn(DUP);
				mv.visitVarInsn(LLOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_LONG, "<init>", "(J)V");
				break;
			case 'F':
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_FLOAT);
				mv.visitInsn(DUP);
				mv.visitVarInsn(FLOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_FLOAT, "<init>", "(F)V");
				break;
			case 'D':
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE);
				mv.visitInsn(DUP);
				mv.visitVarInsn(DLOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE, "<init>", "(D)V");
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
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_BYTE);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_BYTE, "byteValue", "byteValue()B");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'C':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER, "charValue", "charValue()C");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'S':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_SHORT);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_SHORT, "shortValue", "shortValue()S");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'I':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_INTEGER);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_INTEGER, "intValue", "intValue()I");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'Z':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN, "booleanValue", "booleanValue()Z");
				mv.visitVarInsn(ISTORE, offset);
				break;
			case 'J':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_LONG);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_LONG, "longValue", "longValue()J");
				mv.visitVarInsn(LSTORE, offset);
				break;
			case 'F':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_FLOAT);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_FLOAT, "floatValue", "floatValue()F");
				mv.visitVarInsn(FSTORE, offset);
				break;
			case 'D':
				mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE);
				mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE, "doubleValue", "doubleValue()D");
				mv.visitVarInsn(DSTORE, offset);
				break;
			default:
				mv.visitVarInsn(ASTORE, offset);
		}
	}

	// ---------------------------------------------------------------- return

	public static void visitReturn(MethodVisitor mv, MethodSignatureVisitor msign, boolean isLast) {
		visitReturn(mv, msign, isLast, false);
	}

	public static void visitReturn(MethodVisitor mv, MethodSignatureVisitor msign, boolean isLast, boolean returnDefault) {
		switch (msign.getReturnOpcodeType()) {
			case 'V':
				if (isLast == true) {
					mv.visitInsn(POP);
				}
				mv.visitInsn(RETURN);
				break;
			case 'B':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_BYTE);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_BYTE, "byteValue", "()B");
					}
				}
				mv.visitInsn(IRETURN);
				break;
			case 'C':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER, "charValue", "()C");
					}
				}
				mv.visitInsn(IRETURN);
				break;
			case 'S':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_SHORT);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_SHORT, "shortValue", "()S");
					}
				}
				mv.visitInsn(IRETURN);
				break;
			case 'I':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_INTEGER);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_INTEGER, "intValue", "()I");
					}
				}
				mv.visitInsn(IRETURN);
				break;
			case 'Z':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN, "booleanValue", "()Z");
					}
				}
				mv.visitInsn(IRETURN);
				break;
			case 'J':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(LCONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_LONG);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_LONG, "longValue", "()J");
					}
				}
				mv.visitInsn(LRETURN);
				break;
			case 'F':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(FCONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_FLOAT);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_FLOAT, "floatValue", "()F");
					}
				}
				mv.visitInsn(FRETURN);
				break;
			case 'D':
				if (isLast == true) {
					if (returnDefault) {
						mv.visitInsn(POP);
						mv.visitInsn(DCONST_0);
					} else {
						mv.visitTypeInsn(CHECKCAST, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE);
						mv.visitMethodInsn(INVOKEVIRTUAL, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE, "doubleValue", "()D");
					}
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
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_BYTE);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_BYTE, "<init>", "(B)V");
				break;
			case 'C':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_CHARACTER, "<init>", "(C)V");
				break;
			case 'S':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_SHORT);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_SHORT, "<init>", "(S)V");
				break;
			case 'I':
				mv.visitVarInsn(ISTORE, varOffset);
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_INTEGER);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ILOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_INTEGER, "<init>", "(I)V");
				break;
			case 'Z':
				mv.visitMethodInsn(INVOKESTATIC, AsmConst.SIGNATURE_JAVA_LANG_BOOLEAN, "valueOf", "(Z)Ljava/lang/Boolean;");
				break;
			case 'J':
				mv.visitVarInsn(LSTORE, varOffset);
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_LONG);
				mv.visitInsn(DUP);
				mv.visitVarInsn(LLOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_LONG, "<init>", "(J)V");
				break;
			case 'F':
				mv.visitVarInsn(FSTORE, varOffset);
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_FLOAT);
				mv.visitInsn(DUP);
				mv.visitVarInsn(FLOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_FLOAT, "<init>", "(F)V");
				break;
			case 'D':
				mv.visitVarInsn(DSTORE, varOffset);
				mv.visitTypeInsn(NEW, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE);
				mv.visitInsn(DUP);
				mv.visitVarInsn(DLOAD, varOffset);
				mv.visitMethodInsn(INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_DOUBLE, "<init>", "(D)V");
				break;

		}
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

	public static boolean isPushDefaultResultValueMethod(String name, String desc) {
		if (name.equals("pushDefaultResultValue")) {
			if (desc.equals("()V")) {
				return true;
			}
		}
		return false;
	}

}
