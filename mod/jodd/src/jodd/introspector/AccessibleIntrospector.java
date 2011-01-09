// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link jodd.introspector.Introspector introspector} caches all class descriptors.
 * Only <b>accessible</b> methods and fields are examined.
 * <p>
 * It does not provide any more subtle logic behind, therefore, it should not be used
 * in environments with dynamic class re-loading.
 *
 * todo: add optional max value for total number of class descriptors stored in cache
 * @see SupportedIntrospector
 */
public class AccessibleIntrospector implements Introspector {

	protected final Map<Class, ClassDescriptor> cache = new HashMap<Class, ClassDescriptor>();

	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	public ClassDescriptor register(Class type) {
		ClassDescriptor cd = describeClass(type);
		cache.put(type, cd);
		return cd;
	}

	/**
	 * Describes a class by creating a new instance of {@link ClassDescriptor}
	 * that examines all accessible methods and fields.
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
		stat.append("Total classes: ").append(cache.size()).append('\n');
		for (Map.Entry<Class, ClassDescriptor> entry : cache.entrySet()) {
			ClassDescriptor bcd = entry.getValue();
			stat.append('\t').append(entry.getKey().getName()).append(" (");
			stat.append(bcd.getUsageCount()).append(" uses)").append('\n');
		}
		return stat.toString();
	}

}
