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

package jodd.util.cl;

import jodd.util.ClassUtil;
import jodd.util.StringUtil;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Default Jodd class loader strategy.
 * Loads a class with a given name dynamically, more reliable then <code>Class.forName</code>.
 * <p>
 * Class will be loaded using class loaders in the following order:
 * <ul>
 * <li>provided class loader (if any)</li>
 * <li><code>Thread.currentThread().getContextClassLoader()}</code></li>
 * <li>caller classloader</li>
 * </ul>
 */
public class DefaultClassLoaderStrategy implements ClassLoaderStrategy {

	/**
	 * List of primitive type names.
	 */
	public static final String[] PRIMITIVE_TYPE_NAMES = new String[] {
			"boolean", "byte", "char", "double", "float", "int", "long", "short",
	};
	/**
	 * List of primitive types that matches names list.
	 */
	public static final Class[] PRIMITIVE_TYPES = new Class[] {
			boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class,
	};
	/**
	 * List of primitive bytecode characters that matches names list.
	 */
	public static final char[] PRIMITIVE_BYTECODE_NAME = new char[] {
			'Z', 'B', 'C', 'D', 'F', 'I', 'J', 'S'
	};

	// ---------------------------------------------------------------- flags

	protected boolean loadArrayClassByComponentTypes = false;

	/**
	 * Returns arrays class loading strategy.
	 */
	public boolean isLoadArrayClassByComponentTypes() {
		return loadArrayClassByComponentTypes;
	}

	/**
	 * Defines arrays class loading strategy.
	 * If <code>false</code> (default), classes will be loaded by <code>Class.forName</code>.
	 * If <code>true</code>, classes will be loaded by reflection and component types.
	 */
	public void setLoadArrayClassByComponentTypes(final boolean loadArrayClassByComponentTypes) {
		this.loadArrayClassByComponentTypes = loadArrayClassByComponentTypes;
	}

	// ---------------------------------------------------------------- names

	/**
	 * Prepares classname for loading, respecting the arrays.
	 * Returns <code>null</code> if class name is not an array.
	 */
	public static String prepareArrayClassnameForLoading(String className) {
		int bracketCount = StringUtil.count(className, '[');

		if (bracketCount == 0) {
			// not an array
			return null;
		}

		String brackets = StringUtil.repeat('[', bracketCount);

		int bracketIndex = className.indexOf('[');
		className = className.substring(0, bracketIndex);

		int primitiveNdx = getPrimitiveClassNameIndex(className);
		if (primitiveNdx >= 0) {
			className = String.valueOf(PRIMITIVE_BYTECODE_NAME[primitiveNdx]);

			return brackets + className;
		} else {
			return brackets + 'L' + className + ';';
		}
	}

	/**
	 * Detects if provided class name is a primitive type.
	 * Returns >= 0 number if so.
	 */
	private static int getPrimitiveClassNameIndex(final String className) {
		int dotIndex = className.indexOf('.');
		if (dotIndex != -1) {
			return -1;
		}
		return Arrays.binarySearch(PRIMITIVE_TYPE_NAMES, className);
	}

	// ---------------------------------------------------------------- load

	/**
	 * Loads class by name.
	 */
	@Override
	public Class loadClass(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
		String arrayClassName = prepareArrayClassnameForLoading(className);

		if ((className.indexOf('.') == -1) && (arrayClassName == null)) {
			// maybe a primitive
			int primitiveNdx = getPrimitiveClassNameIndex(className);
			if (primitiveNdx >= 0) {
				return PRIMITIVE_TYPES[primitiveNdx];
			}
		}

		// try #1 - using provided class loader
		if (classLoader != null) {
			Class klass = loadClass(className, arrayClassName, classLoader);

			if (klass != null) {
				return klass;
			}
		}

		// try #2 - using thread class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();

		if ((currentThreadClassLoader != null) && (currentThreadClassLoader != classLoader)) {
			Class klass = loadClass(className, arrayClassName, currentThreadClassLoader);

			if (klass != null) {
				return klass;
			}
		}

		// try #3 - using caller classloader, similar as Class.forName()
		//Class callerClass = ReflectUtil.getCallerClass(2);
		Class callerClass = ClassUtil.getCallerClass();
		ClassLoader callerClassLoader = callerClass.getClassLoader();

		if ((callerClassLoader != classLoader) && (callerClassLoader != currentThreadClassLoader)) {
			Class klass = loadClass(className, arrayClassName, callerClassLoader);

			if (klass != null) {
				return klass;
			}
		}

		// try #4 - everything failed, try alternative array loader
		if (arrayClassName != null) {
			try {
				return loadArrayClassByComponentType(className, classLoader);
			} catch (ClassNotFoundException ignore) {
			}
		}

		throw new ClassNotFoundException("Class not found: " + className);
	}

	/**
	 * Loads a class using provided class loader.
	 * If class is an array, it will be first loaded using the <code>Class.forName</code>!
	 * We must use this since for JDK {@literal >=} 6 arrays will be not loaded using classloader,
	 * but only with <code>forName</code> method. However, array loading strategy can be
	 * {@link #setLoadArrayClassByComponentTypes(boolean) changed}.
	 */
	protected Class loadClass(final String className, final String arrayClassName, final ClassLoader classLoader) {
		if (arrayClassName != null) {
			try {
				if (loadArrayClassByComponentTypes) {
					return loadArrayClassByComponentType(className, classLoader);
				} else {
					return Class.forName(arrayClassName, true, classLoader);
				}
			} catch (ClassNotFoundException ignore) {
			}
		}

		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException ignore) {
		}

		return null;
	}

	/**
	 * Loads array class using component type.
	 */
	protected Class loadArrayClassByComponentType(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
		int ndx = className.indexOf('[');
		int multi = StringUtil.count(className, '[');

		String componentTypeName = className.substring(0, ndx);

		Class componentType = loadClass(componentTypeName, classLoader);

		if (multi == 1) {
			return Array.newInstance(componentType, 0).getClass();
		}

		int[] multiSizes;

		if (multi == 2) {
			multiSizes = new int[] {0, 0};
		} else if (multi == 3) {
			multiSizes = new int[] {0, 0, 0};
		} else {
			multiSizes = (int[]) Array.newInstance(int.class, multi);
		}

		return Array.newInstance(componentType, multiSizes).getClass();
	}

}