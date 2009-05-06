// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

/**
 * Default class {@link Introspector} simply delegates method calls for
 * more convenient usage.
 */
public class ClassIntrospector {

	public static final Introspector SIMPLE_INTROSPECTOR = new SimpleIntrospector();
	public static final Introspector WEAK_INTROSPECTOR = new WeakIntrospector();

	public static Introspector DEFAULT = SIMPLE_INTROSPECTOR;

	/**
	 * Returns class descriptor for specified type.
	 */
	public static ClassDescriptor lookup(Class type) {
		return DEFAULT.lookup(type);
	}

	/**
	 * Registers new type.
	 */
	public static ClassDescriptor register(Class type) {
		return DEFAULT.register(type);
	}

	/**
	 * Clears cache.
	 */
	public static void resetCache() {
		DEFAULT.reset();
	}

	/**
	 * Returns a string with some basic statistics information.
	 */
	public static String getStatistics() {
		return DEFAULT.getStatistics();
	}

	/**
	 * Specifies custom global introspector.
	 */
	public static void setDefaultIntrospector(Introspector i) {
		if (i != null) {
			DEFAULT = i;
		}
	}
}