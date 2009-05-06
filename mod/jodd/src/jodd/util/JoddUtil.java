// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.Jodd;

/**
 * Some internal Jodd utilities.
 */
public class JoddUtil {

	private static final String version;
	private static final String rootPackage;
	private static final String rootPackageDot;

	static {
		Package pkg = Jodd.class.getPackage();
		version = (pkg != null ? pkg.getImplementationVersion() : "");
		rootPackage = "jodd";
		rootPackageDot = "jodd.";
	}

	/**
	 * Returns the full version string of the Jodd,
	 */
	public static String getVersion() {
		return version;
	}

	/**
	 * Returns the root package name of the Jodd ('jodd').
	 */
	public static String getRootPackageName() {
		return rootPackage;
	}

	/**
	 * Returns <code>true</code> if class is from Jodd library.
	 */
	public static boolean isJoddClass(Class clazz) {
		return clazz.getPackage().getName().startsWith(rootPackageDot);
	}

}
