// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.util.StringPool;

/**
 * Jodd library global defaults. They are used in more then one place (class, package).
 */
public class JoddDefault {

	/**
	 * Jodd version.
	 */
	public static final String JODD_VERSION;

	/**
	 * Jodd root package.
	 */
	public static final String JODD_PACKAGE_NAME;

	/**
	 * Default temp file prefix.
	 */
	public static String JODD_TMP_FILE_PREFIX = "jodd-";

	/**
	 * Default file encoding (UTF8).
	 */
	public static String encoding = StringPool.UTF_8;

	/**
	 * Default IO buffer size (32 KB).
	 */
	public static int ioBufferSize = 32768;

	/**
	 * Default class loader (of this class) used when class loader is not explicitly specified.
	 * Warning: be careful when using <code>ClassLoader.getSystemClassLoader()</code>,
	 * especially in web applications, since user classes are loaded with containers
	 * classloader.
	 */
	public static ClassLoader classLoader = JoddDefault.class.getClassLoader();

	static {
		Package pkg = Jodd.class.getPackage();
		JODD_VERSION = pkg.getImplementationVersion();
		JODD_PACKAGE_NAME = pkg.getName();
	}

}
