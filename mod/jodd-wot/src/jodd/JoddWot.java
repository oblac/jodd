// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

/**
 * Jodd-WOT.
 */
public class JoddWot {

	/**
	 * Hello from Jodd-WOT.
	 */
	public static void main(String[] args) {
		Package pkg = JoddWot.class.getPackage();
		String version = pkg.getImplementationVersion();
		System.out.println(
				"\n\n   -= Jodd-WOT =-\n" +
				"        " + version +
				"\n\n" +
				"   generic purpose\n" +
				"    java library\n" +
				"   and frameworks\n");
	}
}
