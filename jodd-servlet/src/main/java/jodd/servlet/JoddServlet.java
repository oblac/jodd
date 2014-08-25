// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.Jodd;

/**
 * Jodd SERVLET module.
 */
public class JoddServlet {

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddServlet.class);
	}
}