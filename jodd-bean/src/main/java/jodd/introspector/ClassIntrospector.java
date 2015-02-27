// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;


/**
 * Default class {@link Introspector} simply delegates method calls for
 * more convenient usage.
 */
public class ClassIntrospector {

	/**
	 * Returns class descriptor for specified type.
	 */
	public static ClassDescriptor lookup(Class type) {
		return JoddIntrospector.introspector.lookup(type);
	}

	/**
	 * Registers new type.
	 */
	public static ClassDescriptor register(Class type) {
		return JoddIntrospector.introspector.register(type);
	}

	/**
	 * Clears all cached data.
	 */
	public static void reset() {
		JoddIntrospector.introspector.reset();
	}

}