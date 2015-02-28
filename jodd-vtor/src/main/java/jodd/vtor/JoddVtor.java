// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.Jodd;

/**
 * Jodd VTOR module.
 */
public class JoddVtor {

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddVtor.class);
	}

}