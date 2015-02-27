// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.Jodd;

/**
 * Jodd PETITE module.
 */
public class JoddPetite {

	/**
	 * Defines if Proxetta should be used.
	 */
	public static boolean useProxetta;

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddPetite.class);

		useProxetta = Jodd.isModuleLoaded(Jodd.PROXETTA);
	}

}