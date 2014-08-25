// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.Jodd;

/**
 * Jodd MADVOC module.
 */
public class JoddMadvoc {

	/**
	 * Defines if Proxetta should be used.
	 */
	public static boolean useProxetta;

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddMadvoc.class);

		useProxetta = Jodd.isModuleLoaded(Jodd.PROXETTA);
	}

}