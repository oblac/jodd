// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.introspector.CachingIntrospector;
import jodd.introspector.Introspector;

/**
 * Jodd BEAN module.
 */
public class JoddBean {

	/**
	 * Name of 'this' references.
	 */
	public static String thisRef = "*this";

	/**
	 * Field name prefix.
	 */
	public static String fieldPrefix;

	/**
	 * Default {@link Introspector} implementation.
	 */
	public static Introspector introspector = new CachingIntrospector(true);

}
