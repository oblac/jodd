// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import jodd.Jodd;

/**
 * Jodd JTX module.
 */
public class JoddJtx {

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddJtx.class);
	}

}