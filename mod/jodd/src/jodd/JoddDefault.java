// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.util.StringPool;

/**
 * List of Jodd library defaults. They are used in more then one place (class, package).
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
	 * Default file encoding.
	 */
	public static String encoding = StringPool.UTF_8;

	/**
	 * Default IO buffer size (32 KB).
	 */
	public static int ioBufferSize = 32768;

	static {
		Package pkg = Jodd.class.getPackage();
		JODD_VERSION = pkg.getImplementationVersion();
		JODD_PACKAGE_NAME = pkg.getName();
	}

}
