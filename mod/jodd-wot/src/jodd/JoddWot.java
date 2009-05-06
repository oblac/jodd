// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

/**
 * Jodd-WOT.
 */
public class JoddWot {

	/**
	 * Returns the full version string of the Jodd,
	 */
	public static String getVersion() {
		Package pkg = JoddWot.class.getPackage();
		return (pkg != null ? pkg.getImplementationVersion() : null);
	}

	public static void main(String[] args) {
		System.out.println(
				"\n\n   -= Jodd-WOT =-\n" +
				"        b" + getVersion() +
				"\n\n" +
				"   generic purpose\n" +
				"    java library\n" +
				"   and frameworks\n");
	}
}
