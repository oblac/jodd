// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.Jodd;

/**
 * Jodd LAGARTO module.
 */
public class JoddLagarto {

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddLagarto.class);
	}

}