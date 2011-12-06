// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;


/**
 * Provides introspection analysis against any java class.
 * Implementations may cache {@link ClassDescriptor} objects to improve performance.
 * @see AccessibleIntrospector
 * @see SupportedIntrospector
 * @see WeakIntrospector
 */
public interface Introspector {
	/**
	 * Returns the {@link jodd.introspector.ClassDescriptor} object for specified class.
	 */
	ClassDescriptor lookup(Class type);

	/**
	 * Registers new class type. If type already registered, it will be
	 * reset and registered again with new class descriptor.
	 */
	ClassDescriptor register(Class type);

	/**
	 * Resets current cache.
	 */
	void reset();

	/**
	 * Returns simple statistics information about all cached descriptors and their usage.
	 */
	String getStatistics();
}
