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

package jodd.paramo;

import jodd.asm.EmptyClassVisitor;
import jodd.asm5.MethodVisitor;
import jodd.asm5.Type;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;

/**
 * Lookups for specific method in order to start with
 * {@link jodd.paramo.ParamExtractor parameter extraction}.
 */
final class MethodFinder extends EmptyClassVisitor {

	private static final Map<String, String> primitives = new HashMap<>(8);

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
		if (!name.equals(methodName)) {
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
			if (!isEqualTypeName(argumentTypes[i], this.parameterTypes[i])) {
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
	 * Returns method parameters once when method is parsed.
	 * If method has no parameters, an empty array is returned.
	 */
	MethodParameter[] getResolvedParameters() {
		if (paramExtractor == null) {
			return MethodParameter.EMPTY_ARRAY;
		}
		if (!paramExtractor.debugInfoPresent) {
			throw new ParamoException("Parameter names not available for method: "
					+ declaringClass.getName() + '#' + methodName);
		}
		return paramExtractor.getMethodParameters();
	}

}