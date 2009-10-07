// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

/**
 * {@link jodd.introspector.Introspector Introspector} caches all class descriptors.
 * All <b>supported</b> methods and fields are examined.
 * 
 * @see AccessibleIntrospector
 */
public class SupportedInterceptor extends AccessibleIntrospector {

	/**
	 * Describes a class by creating a new instance of {@link ClassDescriptor}
	 * that will examine all supported methods and fields.
	 */
	@Override
	protected ClassDescriptor describeClass(Class type) {
		return new ClassDescriptor(type, false);
	}

}
