// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Simple {@link jodd.introspector.Introspector} caches all class descriptors.
 * It does not provide any more subtle logic behind, therefore, it should not be used
 * in environments with dynamic class re-loading.
 *
 * todo: add optional max value for total number of class descriptors stored in cache 
 */
public class SimpleIntrospector implements Introspector {

	protected Map<Class, ClassDescriptor> cache = new HashMap<Class, ClassDescriptor>();

	/**
	 * Returns the {@link ClassDescriptor} object for specified class.
	 */
	public ClassDescriptor lookup(Class type) {
		ClassDescriptor cd = cache.get(type);
		if (cd != null) {
			cd.increaseUsageCount();
			return cd;
		}
		cd = describeClass(type);
		cache.put(type, cd);
		return cd;
	}

	/**
	 * Registers new class type. If type already registered, it will be
	 * reseted and registered again with new class descriptor.
	 */
	public ClassDescriptor register(Class type) {
		ClassDescriptor cd = describeClass(type);
		cache.put(type, cd);
		return cd;
	}

	/**
	 * Describes a class by creating a new instance of {@link ClassDescriptor}.
	 */
	protected ClassDescriptor describeClass(Class type) {
		return new ClassDescriptor(type);
	}

	/**
	 * Resets current cache.
	 */
	public void reset() {
		cache = new WeakHashMap<Class, ClassDescriptor>();
	}

	/**
	 * Returns simple statistics information about all cached descriptors and their usage.
	 */
	public String getStatistics() {
		StringBuilder stat = new StringBuilder();
		stat.append("Total classes: ").append(cache.size()).append('\n');
		for (Class clazz : cache.keySet()) {
			ClassDescriptor bcd = cache.get(clazz);
			stat.append('\t').append(clazz.getName()).append(" (");
			stat.append(bcd.getUsageCount()).append(" uses)").append('\n');
		}
		return stat.toString();
	}

}
