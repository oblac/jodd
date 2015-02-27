// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.Jodd;

/**
 * Jodd DB module.
 */
public class JoddDb {

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddDb.class);
	}

}