// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.Jodd;

/**
 * Jodd PETITE module.
 */
public class JoddPetite {

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddPetite.class);

		useProxetta = Jodd.isModuleLoaded(Jodd.PROXETTA);
	}

	/**
	 * Defines if Proxetta should be used.
	 */
	public static boolean useProxetta;

}