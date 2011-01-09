// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.introspector;

import java.io.FileInputStream;
import java.util.HashMap;

public class FooClassLoader extends ClassLoader {

	private HashMap classBuffer = new HashMap();

	/**
	 * Reads a class file from the special folder on the file system. This location
	 * is not in the CLASSPATH.
	 *
	 * @param className Name of the class to load
	 *
	 * @return loaded class or <code>null</code>
	 */
	private byte[] readClass(String className) {
		System.out.println("> find class: " + className);
		byte result[];
		try {
			FileInputStream fi = new FileInputStream("./build/classes/production/samples/" + className.replace('.', '/') + ".class");
			result = new byte[fi.available()];
			fi.read(result);
			return result;
		} catch (Exception e) {
			/* either class is not found or there is IO error */
			return null;
		}
	}

	/**
	 * Always resolve the class before it is returned.
	 *
	 * @param className name of the class to load
	 *
	 * @return class instance
	 * @exception ClassNotFoundException
	 */
	@Override
	public Class loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, true);
	}

	/**
	 * Custom version of <code>loadClass</code> which is also called
	 * from the internal function <code>FindClassFromClass</code>.
	 *
	 * @return founded class
	 * @exception ClassNotFoundException
	 */
	@Override
	public synchronized Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
		Class result;
		byte classData[];

		System.out.println("> load class: " + className);

		/* [1] examine classBuffer */
		result = (Class) classBuffer.get(className);
		if (result != null) {
			System.out.println("> class is cached");
			return result;
		}

		/* [3] load class from the special location */
		classData = readClass(className);
		if (classData == null) {
			/* [2] check system class loaders */
			result = super.findSystemClass(className);
			System.out.println("> system class");
			return result;
		}

		/* [4] define class, i.e. parse the class file */
		result = defineClass(null, classData, 0, classData.length);
		if (result == null) {
			throw new ClassFormatError();
		}

		/* [5] resolve the class */
		if (resolveIt) {
			resolveClass(result);
		}

		/* [6] store class in the buffer */
		classBuffer.put(className, result);
		System.out.println("> class loaded ok");
		return result;
	}
}
