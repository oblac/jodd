// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.Jodd;
import jodd.introspector.Introspector;
import jodd.introspector.JoddIntrospector;

/**
 * Jodd BEAN module.
 */
public class JoddBean {

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddBean.class);

		introspector = JoddIntrospector.introspector;
	}

	/**
	 * Name of 'this' references.
	 */
	public static String thisRef = "*this";

	/**
	 * Default {@link Introspector} implementation.
	 */
	public static Introspector introspector;

}