// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.asm.EmptyClassVisitor;
import jodd.util.StringUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;

/**
 * Lookups for specific method in order to start with
 * {@link jodd.paramo.ParamExtractor parameter extraction}.
 */
final class MethodFinder extends EmptyClassVisitor {

	public static final String[] EMPTY_NAMES = new String[0];

	private static final Map<String, String> primitives = new HashMap<String, String>(8);

	private static final String TYPE_INT = "int";
	private static final String TYPE_BOOLEAN = "boolean";
	private static final String TYPE_BYTE = "byte";
	private static final String TYPE_CHAR = "char";
	private static final String TYPE_SHORT = "short";
	private static final String TYPE_FLOAT = "float";
	private static final String TYPE_LONG = "long";
	private static final String TYPE_DOUBLE = "double";
	private static final String ARRAY = "[]";

	static {
		primitives.put(TYPE_INT, "I");
		primitives.put(TYPE_BOOLEAN, "Z");
		primitives.put(TYPE_CHAR, "C");
		primitives.put(TYPE_BYTE, "B");
		primitives.put(TYPE_FLOAT, "F");
		primitives.put(TYPE_LONG, "J");
		primitives.put(TYPE_DOUBLE, "D");
		primitives.put(TYPE_SHORT, "S");
	}

	private final Class declaringClass;
	private final String methodName;
	private final Class[] parameterTypes;
	private ParamExtractor paramExtractor;

	MethodFinder(Class declaringClass, String methodName, Class[] parameterTypes) {
		this.declaringClass = declaringClass;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.paramExtractor = null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (paramExtractor != null) {
			return null;				// method already found, skip all further methods
		}
		if (name.equals(methodName) == false) {
			return null;				// different method
		}

		Type[] argumentTypes = Type.getArgumentTypes(desc);
		int dwordsCount = 0;
		for (Type t : argumentTypes) {
			if (t.getClassName().equals(TYPE_LONG) || t.getClassName().equals(TYPE_DOUBLE)) {
				dwordsCount++;
			}
		}

		int paramCount = argumentTypes.length;
		if (paramCount != this.parameterTypes.length) {
			return null;				// different number of params
		}

		for (int i = 0; i < argumentTypes.length; i++) {
			if (isEqualTypeName(argumentTypes[i], this.parameterTypes[i]) == false) {
				return null;			// wrong param types
			}
		}

		this.paramExtractor = new ParamExtractor((Modifier.isStatic(access) ? 0 : 1), argumentTypes.length + dwordsCount);
		return paramExtractor;
	}

	/**
	 * Returns <code>true</code> if type name equals param type.
	 */
	boolean isEqualTypeName(Type argumentType, Class paramType) {
		String s = argumentType.getClassName();
		if (s.endsWith(ARRAY)) {		// arrays detected
			String prefix = s.substring(0, s.length() - 2);
			String bytecodeSymbol = primitives.get(prefix);
			if (bytecodeSymbol != null) {
				s = '[' + bytecodeSymbol;
			} else {
				s = "[L" + prefix + ';';
			}
		}
		return s.equals(paramType.getName());
	}

	/**
	 * Returns parameter names once when method is parsed.
	 */
	String[] getParameterNames() {
		if (paramExtractor == null) {
			return EMPTY_NAMES;
		}
		if (paramExtractor.debugInfoPresent == false) {
			throw new ParamoException("Parameter names not available for method: '"
					+ declaringClass.getName() + '#' + methodName + "'.");
		}
		return StringUtil.splitc(paramExtractor.getResult(), ',');
	}

}