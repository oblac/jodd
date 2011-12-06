// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

/**
 * Bean Loaders know how to populate a bean from provided source.
 * Loaders are manager by {@link jodd.bean.BeanLoaderManager}.
 * However, they can be used directly like functors.
 */
public interface BeanLoader {

	/**
	 * Loads values from given source into the destination bean.
	 * All properties from source object will be iterated
	 * to be set into the destination.
	 *
	 * @param destination	instance that will be populated
	 * @param source	object to populate from
	 */
	void load(Object destination, Object source);

}
