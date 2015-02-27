// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.cl;

/**
 * Class loader strategy defines how classes should be loaded.
 */
public interface ClassLoaderStrategy {

	/**
	 * Loads class with given name and optionally provided class loader.
	 */
	public Class loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException;

}