// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Many general utilities that allows writing java as a script, as much as possible :).
 * All methods are safe as possible and operates with as many types as possible.
 */
public class JoddScript {

	/**
	 * Returns string representation of an object, while checking for <code>null</code>.
	 */
	public static String toString(Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}
}
