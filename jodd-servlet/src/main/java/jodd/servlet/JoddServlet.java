// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.Jodd;

/**
 * Jodd SERVLET module.
 */
public class JoddServlet {

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddServlet.class);
	}
}