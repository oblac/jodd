// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.cl;

import jodd.util.ReflectUtil;
import jodd.util.StringUtil;

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
 * <li>using <code>Class.forName</code></li>
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

	/**
	 * Prepares classname for loading.
	 */
	public static String prepareClassnameForLoading(String className) {
		int bracketCount = StringUtil.count(className, '[');
		if (bracketCount == 0) {
			return className;
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
	private static int getPrimitiveClassNameIndex(String className) {
		int dotIndex = className.indexOf('.');
		if (dotIndex != -1) {
			return -1;
		}
		return Arrays.binarySearch(PRIMITIVE_TYPE_NAMES, className);
	}




	public Class loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		className = prepareClassnameForLoading(className);

		if ((className.indexOf('.') == -1) || (className.indexOf('[') == -1)) {
			// maybe a primitive
			int primitiveNdx = getPrimitiveClassNameIndex(className);
			if (primitiveNdx >= 0) {
				return PRIMITIVE_TYPES[primitiveNdx];
			}
		}

		// try #1 - using provided class loader
		try {
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
		} catch (ClassNotFoundException ignore) {
		}

		// try #2 - using thread class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		if ((currentThreadClassLoader != null) && (currentThreadClassLoader != classLoader)) {
			try {
				return currentThreadClassLoader.loadClass(className);
			} catch (ClassNotFoundException ignore) {
			}
		}

		// try #3 - using caller classloader, similar as Class.forName()
		//Class callerClass = ReflectUtil.getCallerClass(2);
		Class callerClass = ReflectUtil.getCallerClass();
		ClassLoader callerClassLoader = callerClass.getClassLoader();

		if ((callerClassLoader != classLoader) && (callerClassLoader != currentThreadClassLoader)) {
			try {
				return callerClassLoader.loadClass(className);
			} catch (ClassNotFoundException ignore) {
			}
		}

		// try #4 - using Class.forName(). We must use this since for JDK >= 6
		// arrays will be not loaded using classloader, but only with forName.
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ignore) {
		}

		throw new ClassNotFoundException("Class not found: " + className);
	}

}