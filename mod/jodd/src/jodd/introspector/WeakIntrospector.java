// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ref.ReferenceMap;
import jodd.util.ref.ReferenceType;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Smarter {@link jodd.introspector.Introspector} caches class data
 * together with class loader information. Therefore, if classes are reloaded dynamically,
 * this introspector will maintains its cache.
 */
public class WeakIntrospector implements Introspector {

	protected Map<ClassLoader, Map<String, ClassDescriptor>> cache = new WeakHashMap<ClassLoader, Map<String, ClassDescriptor>>();

	/**
	 * Returns class descriptor map.
	 */
	protected Map<String, ClassDescriptor> getClassDescriptorMap(Class type) {
		ClassLoader loader = type.getClassLoader();
		if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}
		Map<String, ClassDescriptor> map = cache.get(loader);
		if (map == null) {
			map = new ReferenceMap<String, ClassDescriptor>(ReferenceType.WEAK, ReferenceType.WEAK);
			cache.put(loader, map);
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	public ClassDescriptor lookup(Class type) {
		Map<String, ClassDescriptor> map = getClassDescriptorMap(type);
		ClassDescriptor cd = map.get(type.getName());
		if (cd == null) {
			cd = describeClass(type);
			cd.increaseUsageCount();
			map.put(type.getName(), cd);
		}
		return cd;
	}

	/**
	 * {@inheritDoc}
	 */
	public ClassDescriptor register(Class type) {
		Map<String, ClassDescriptor> map = getClassDescriptorMap(type);
		ClassDescriptor cd = describeClass(type);
		map.put(type.getName(), cd);
		return cd;
	}

	/**
	 * Describes a class by creating a new instance of {@link ClassDescriptor}.
	 */
	protected ClassDescriptor describeClass(Class type) {
		return new ClassDescriptor(type, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		cache.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getStatistics() {
		StringBuilder stat = new StringBuilder();
		int total = 0;
		for (Map<String, ClassDescriptor> map : cache.values()) {
			total += map.size();
		}
		stat.append("Total classes: ").append(total).append('\n');
		for (Map<String, ClassDescriptor> map : cache.values()) {
			for (ClassDescriptor bcd : map.values()) {
				if (bcd != null) {
					stat.append('\t').append(bcd.getType().getName()).append(" (");
					stat.append(bcd.getUsageCount()).append(')').append('\n');
				}
			}
		}
		return stat.toString();
	}

}
