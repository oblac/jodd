// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

/**
 * Jodd PETITE module.
 */
public class JoddPetite {

	static {
		Jodd.module();

		useProxetta = Jodd.isModuleLoaded(Jodd.PROXETTA);
	}

	/**
	 * Defines if Proxetta should be used.
	 */
	public static boolean useProxetta;

}