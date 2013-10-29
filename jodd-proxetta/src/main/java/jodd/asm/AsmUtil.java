// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm4.MethodVisitor;
import jodd.mutable.MutableInteger;

import static jodd.asm4.Opcodes.CHECKCAST;
import static jodd.asm4.Opcodes.INVOKESTATIC;
import static jodd.asm4.Opcodes.INVOKEVIRTUAL;

/**
 * Generic ASM utils.
 */
public class AsmUtil {

	// types

	public static final int TYPE_BYTE           = 'B';
	public static final int TYPE_CHAR           = 'C';
	public static final int TYPE_DOUBLE         = 'D';
	public static final int TYPE_FLOAT          = 'F';
	public static final int TYPE_INT            = 'I';
	public static final int TYPE_LONG           = 'J';
	public static final int TYPE_REFERENCE      = 'L';
	public static final int TYPE_SHORT          = 'S';
	public static final int TYPE_BOOLEAN        = 'Z';
	public static final int TYPE_VOID           = 'V';
	public static final int TYPE_ARRAY          = '[';
//	public static final int TYPE_STRING         = 's';
//	public static final int TYPE_ENUM           = 'e';
//	public static final int TYPE_CLASS          = 'c';
	public static final int TYPE_ANNOTATION     = '@';

	// access flags

	public static final int ACC_PUBLIC = 0x0001;        // class, field, method
	public static final int ACC_PRIVATE = 0x0002;       // class, field, method
	public static final int ACC_PROTECTED = 0x0004;     // class, field, method
	public static final int ACC_STATIC = 0x0008;        // field, method
	public static final int ACC_FINAL = 0x0010;         // class, field, method
	public static final int ACC_SUPER = 0x0020;         // class
	public static final int ACC_SYNCHRONIZED = 0x0020;  // method
	public static final int ACC_VOLATILE = 0x0040;      // field
	public static final int ACC_BRIDGE = 0x0040;        // method
	public static final int ACC_VARARGS = 0x0080;       // method
	public static final int ACC_TRANSIENT = 0x0080;     // field
	public static final int ACC_NATIVE = 0x0100;        // method
	public static final int ACC_INTERFACE = 0x0200;     // class
	public static final int ACC_ABSTRACT = 0x0400;      // class, method
	public static final int ACC_STRICT = 0x0800;        // method
	public static final int ACC_SYNTHETIC = 0x1000;     // class, field, method
	public static final int ACC_ANNOTATION = 0x2000;    // class
	public static final int ACC_ENUM = 0x4000;          // class(?) field inner


	// signatures

	public static final String SIGNATURE_JAVA_LANG_OBJECT 		= "java/lang/Object";
	public static final String SIGNATURE_JAVA_LANG_CLASS 		= "java/lang/Class";
	public static final String SIGNATURE_JAVA_LANG_BYTE 		= "java/lang/Byte";
	public static final String SIGNATURE_JAVA_LANG_CHARACTER 	= "java/lang/Character";
	public static final String SIGNATURE_JAVA_LANG_SHORT 		= "java/lang/Short";
	public static final String SIGNATURE_JAVA_LANG_INTEGER 		= "java/lang/Integer";
	public static final String SIGNATURE_JAVA_LANG_BOOLEAN 		= "java/lang/Boolean";
	public static final String SIGNATURE_JAVA_LANG_LONG 		= "java/lang/Long";
	public static final String SIGNATURE_JAVA_LANG_FLOAT 		= "java/lang/Float";
	public static final String SIGNATURE_JAVA_LANG_DOUBLE 		= "java/lang/Double";
	public static final String SIGNATURE_JAVA_LANG_VOID 		= "java/lang/Void";

	public static final String L_SIGNATURE_JAVA_LANG_OBJECT 	= "Ljava/lang/Object;";
	public static final String L_SIGNATURE_JAVA_LANG_STRING 	= "Ljava/lang/String;";
	public static final String L_SIGNATURE_JAVA_LANG_CLASS 		= "Ljava/lang/Class;";

	private static final String INVALID_BASE_TYPE = "Invalid base type: ";
	private static final String INVALID_TYPE_DESCRIPTION = "Invalid type description: ";

	// ---------------------------------------------------------------- class relates

	/**
	 * Converts bytecode-like description to java class name that can be loaded
	 * with a classloader. Uses less-known feature of class loaders for loading
	 * array classes. For base types returns the one-letter string that can be used
	 * with {@link #loadBaseTypeClass(String)}.
	 *
	 * @see #typedescToSignature(String, jodd.mutable.MutableInteger)
	 */
	public static String typedesc2ClassName(String desc) {
		String className = desc;
		switch (desc.charAt(0)) {
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
			case 'Z':
			case 'V':
				if (desc.length() != 1) {
					throw new IllegalArgumentException(INVALID_BASE_TYPE + desc);
				}
				break;
			case 'L': className = className.substring(1, className.length() - 1);
			case '[':
				// uses less-known feature of class loaders for loading array types
				// using bytecode-like signatures.
				className = className.replace('/', '.');
				break;
			default: throw new IllegalArgumentException(INVALID_TYPE_DESCRIPTION + desc);
		}

		return className;
	}

	/**
	 * Converts type reference to java-name.
	 */
	public static String typeref2Name(String desc) {
		if (desc.charAt(0) != TYPE_REFERENCE) {
			throw new IllegalArgumentException(INVALID_TYPE_DESCRIPTION + desc);
		}
		String name = desc.substring(1, desc.length() - 1);
		return name.replace('/', '.');
	}

	/**
	 * Loads base class type.
	 */
	public static Class loadBaseTypeClass(String desc) throws ClassNotFoundException {
		if (desc.length() != 1) {
			throw new ClassNotFoundException(INVALID_BASE_TYPE + desc);
		}
		switch (desc.charAt(0)) {
			case 'B': return byte.class;
			case 'C': return char.class;
			case 'D': return double.class;
			case 'F': return float.class;
			case 'I': return int.class;
			case 'J': return long.class;
			case 'S': return short.class;
			case 'Z': return boolean.class;
			case 'V': return void.class;
			default: throw new ClassNotFoundException(INVALID_BASE_TYPE + desc);
		}
	}

	// ---------------------------------------------------------------- description

	/**
	 * Returns java-like signature of a bytecode-like description.
	 * @see #typedescToSignature(String, jodd.mutable.MutableInteger)
	 */
	public static String typedescToSignature(String desc) {
		return typedescToSignature(desc, new MutableInteger());
	}

	/**
	 * Returns java-like signature of a bytecode-like description.
	 * Only first description is parsed.
	 *
	 * The field signature represents the value of an argument to a function or
	 * the value of a variable. It is a series of bytes generated by the
	 * following grammar:
	 *
	 * <PRE>
	 * <field_signature> ::= <field_type>
	 * <field_type>      ::= <base_type>|<object_type>|<array_type>
	 * <base_type>       ::= B|C|D|F|I|J|S|Z
	 * <object_type>     ::= L<fullclassname>;
	 * <array_type>      ::= [<field_type>
	 *
	 * The meaning of the base types is as follows:
	 * B byte signed byte
	 * C char character
	 * D double double precision IEEE float
	 * F float single precision IEEE float
	 * I int integer
	 * J long long integer
	 * L<fullclassname>; ... an object of the given class
	 * S short signed short
	 * Z boolean true or false
	 * [<field sig> ... array
	 * </PRE>
	 *
	 * This method converts this string into a Java type declaration such as
	 * <code>String[]</code>.
	 */
	public static String typedescToSignature(String desc, MutableInteger from) {
		int fromIndex = from.getValue();
		from.value++;	// default usage for most cases

		switch (desc.charAt(fromIndex)) {
			case 'B': return "byte";
			case 'C': return "char";
			case 'D': return "double";
			case 'F': return "float";
			case 'I': return "int";
			case 'J': return "long";
			case 'S': return "short";
			case 'Z': return "boolean";
			case 'V': return "void";

			case 'L':
				int index = desc.indexOf(';', fromIndex);
				if (index < 0) {
					throw new IllegalArgumentException(INVALID_TYPE_DESCRIPTION + desc);
				}
				from.setValue(index + 1);
				String str = desc.substring(fromIndex + 1, index);
				return str.replace('/', '.');

			case '[':
				StringBuilder brackets = new StringBuilder();
				int n = fromIndex;
				while (desc.charAt(n) == '[') {	// count opening brackets
					brackets.append("[]");
					n++;
				}
				from.value = n;
				String type = typedescToSignature(desc, from);	// the rest of the string denotes a `<field_type>'
				return type + brackets;

			default: throw new IllegalArgumentException(INVALID_TYPE_DESCRIPTION + desc);
		}
	}

	// ---------------------------------------------------------------- type

	/**
	 * Converts class name ("foo.Bar") to signature ("foo/bar").
	 */
	public static String typeToSignature(String className) {
		return className.replace('.', '/');
	}

	/**
	 * Converts class name ("foo.Bar") to asm name ("foo/bar").
	 */
	public static String typeToSignature(Class type) {
		return type.getName().replace('.', '/');
	}

	/**
	 * Converts type to type ref.
	 */
	public static String typeToTyperef(Class type) {
		if (type.isArray() == false) {
			if (type.isPrimitive() == false) {
				return 'L' + typeToSignature(type) + ';';
			}
			if (type == int.class) {
				return "I";
			}
			if (type == long.class) {
				return "J";
			}
			if (type == boolean.class) {
				return "Z";
			}
			if (type == double.class) {
				return "D";
			}
			if (type == float.class) {
				return "F";
			}
			if (type == short.class) {
				return "S";
			}
			if (type == void.class) {
				return "V";
			}
			if (type == char.class) {
				return "B";
			}
		}

		return type.getName();
	}

	// ---------------------------------------------------------------- boxing

	/**
	 * Converts <code>Integer</code> object to an <code>int</code>.
	 */
	public static void intValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_INTEGER);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_INTEGER, "intValue", "()I");
	}

	/**
	 * Converts <code>Long</code> object to a <code>long</code>.
	 */
	public static void longValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_LONG);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_LONG, "longValue", "()J");
	}

	/**
	 * Converts <code>Float</code> object to a <code>float</code>.
	 */
	public static void floatValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_FLOAT);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_FLOAT, "floatValue", "()F");
	}

	/**
	 * Converts <code>Double</code> object to a <code>double</code>.
	 */
	public static void doubleValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_DOUBLE);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_DOUBLE, "doubleValue", "()D");
	}

	/**
	 * Converts <code>Byte</code> object to a <code>byte</code>.
	 */
	public static void byteValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_BYTE);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_BYTE, "byteValue", "()B");
	}

	/**
	 * Converts <code>Short</code> object to a <code>short</code>.
	 */
	public static void shortValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_SHORT);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_SHORT, "shortValue", "()S");
	}

	/**
	 * Converts <code>Boolean</code> object to a <code>boolean</code>.
	 */
	public static void booleanValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_BOOLEAN);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_BOOLEAN, "booleanValue", "()Z");
	}

	/**
	 * Converts <code>Character</code> object to a <code>char</code>.
	 */
	public static void charValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_CHARACTER);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_CHARACTER, "charValue", "()C");
	}

	public static void valueOfInteger(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_INTEGER, "valueOf", "(I)Ljava/lang/Integer;");
	}

	public static void valueOfLong(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_LONG, "valueOf", "(J)Ljava/lang/Long;");
	}

	public static void valueOfFloat(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_FLOAT, "valueOf", "(F)Ljava/lang/Float;");
	}

	public static void valueOfDouble(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_DOUBLE, "valueOf", "(D)Ljava/lang/Double;");
	}

	public static void valueOfByte(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_BYTE, "valueOf", "(B)Ljava/lang/Byte;");
	}

	public static void valueOfShort(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_SHORT, "valueOf", "(S)Ljava/lang/Short;");
	}

	public static void valueOfBoolean(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_BOOLEAN, "valueOf", "(Z)Ljava/lang/Boolean;");
	}

	public static void valueOfCharacter(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_CHARACTER, "valueOf", "(C)Ljava/lang/Character;");
	}
}