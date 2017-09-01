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

package jodd.asm;

import jodd.asm5.MethodVisitor;
import jodd.mutable.MutableInteger;

import static jodd.asm5.Opcodes.CHECKCAST;
import static jodd.asm5.Opcodes.INVOKESTATIC;
import static jodd.asm5.Opcodes.INVOKEVIRTUAL;

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
	 * array classes.
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
			case 'L':
				className = className.substring(1, className.length() - 1); break;
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

	// ---------------------------------------------------------------- description

	/**
	 * Returns type-name to type char.
	 * Arrays are not supported.
	 */
	public static char typeNameToOpcode(String typeName) {
		switch (typeName) {
			case "byte" : return 'B';
			case "char": return 'C';
			case "double": return 'D';
			case "float": return 'F';
			case "int": return 'I';
			case "long": return 'J';
			case "short": return 'S';
			case "boolean": return 'Z';
			case "void": return 'V';
			default: return 'L';
		}
	}

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
	 * <PRE>{@code
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
	 * }</PRE>
	 *
	 * This method converts this string into a Java type declaration such as
	 * <code>String[]</code>.
	 */
	public static String typedescToSignature(String desc, MutableInteger from) {
		int fromIndex = from.get();
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
				from.set(index + 1);
				String str = desc.substring(fromIndex + 1, index);
				return str.replace('/', '.');

			case 'T':
				return desc.substring(from.value);

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

			default:
				if (from.value == 0) {
					throw new IllegalArgumentException(INVALID_TYPE_DESCRIPTION + desc);
				}
				// generics!
				return desc.substring(from.value);
		}
	}

	// ---------------------------------------------------------------- type

	/**
	 * Converts java-class name ("foo.Bar") to bytecode-signature ("foo/bar").
	 */
	public static String typeToSignature(String className) {
		return className.replace('.', '/');
	}

	/**
	 * Converts java-class name ("foo.Bar") to bytecode-name ("foo/bar").
	 */
	public static String typeToSignature(Class type) {
		return type.getName().replace('.', '/');
	}

	/**
	 * Converts type to byteccode type ref.
	 */
	public static String typeToTyperef(Class type) {
		if (!type.isArray()) {
			if (!type.isPrimitive()) {
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
			if (type == byte.class) {
				return "B";
			}
			if (type == char.class) {
				return "C";
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
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_INTEGER, "intValue", "()I", false);
	}

	/**
	 * Converts <code>Long</code> object to a <code>long</code>.
	 */
	public static void longValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_LONG);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_LONG, "longValue", "()J", false);
	}

	/**
	 * Converts <code>Float</code> object to a <code>float</code>.
	 */
	public static void floatValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_FLOAT);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_FLOAT, "floatValue", "()F", false);
	}

	/**
	 * Converts <code>Double</code> object to a <code>double</code>.
	 */
	public static void doubleValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_DOUBLE);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_DOUBLE, "doubleValue", "()D", false);
	}

	/**
	 * Converts <code>Byte</code> object to a <code>byte</code>.
	 */
	public static void byteValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_BYTE);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_BYTE, "byteValue", "()B", false);
	}

	/**
	 * Converts <code>Short</code> object to a <code>short</code>.
	 */
	public static void shortValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_SHORT);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_SHORT, "shortValue", "()S", false);
	}

	/**
	 * Converts <code>Boolean</code> object to a <code>boolean</code>.
	 */
	public static void booleanValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_BOOLEAN);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_BOOLEAN, "booleanValue", "()Z", false);
	}

	/**
	 * Converts <code>Character</code> object to a <code>char</code>.
	 */
	public static void charValue(MethodVisitor mv) {
		mv.visitTypeInsn(CHECKCAST, SIGNATURE_JAVA_LANG_CHARACTER);
		mv.visitMethodInsn(INVOKEVIRTUAL, SIGNATURE_JAVA_LANG_CHARACTER, "charValue", "()C", false);
	}

	public static void valueOfInteger(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_INTEGER, "valueOf", "(I)Ljava/lang/Integer;", false);
	}

	public static void valueOfLong(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_LONG, "valueOf", "(J)Ljava/lang/Long;", false);
	}

	public static void valueOfFloat(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_FLOAT, "valueOf", "(F)Ljava/lang/Float;", false);
	}

	public static void valueOfDouble(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_DOUBLE, "valueOf", "(D)Ljava/lang/Double;", false);
	}

	public static void valueOfByte(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_BYTE, "valueOf", "(B)Ljava/lang/Byte;", false);
	}

	public static void valueOfShort(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_SHORT, "valueOf", "(S)Ljava/lang/Short;", false);
	}

	public static void valueOfBoolean(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_BOOLEAN, "valueOf", "(Z)Ljava/lang/Boolean;", false);
	}

	public static void valueOfCharacter(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKESTATIC, SIGNATURE_JAVA_LANG_CHARACTER, "valueOf", "(C)Ljava/lang/Character;", false);
	}

}