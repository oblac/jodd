// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ArraysUtil;

import java.util.Map;
import java.util.Set;

/**
 * Visitor for bean properties.
 */
public abstract class BeanVisitor {

	/**
	 * Source bean.
	 */
	protected Object source;
	/**
	 * List of excluded property names.
	 */
	protected String[] excludeNames;
	/**
	 * List of included property names.
	 */
	protected String[] includeNames;
	/**
	 * Suppress security flag;
	 */
	protected boolean suppressSecurity;
	/**
	 * Defines if null values should be ignored.
	 */
	protected boolean ignoreNullValues;

	// ---------------------------------------------------------------- util

	/**
	 * Returns an array of bean properties. If bean is a <code>Map</code>,
	 * all its keys will be returned.
	 */
	protected String[] resolveProperties(Object bean, boolean suppressSecurity) {
		String[] properties;

		if (bean instanceof Map) {
			Set keys = ((Map) bean).keySet();

			properties = new String[keys.size()];
			int ndx = 0;
			for (Object key : keys) {
				properties[ndx] = key.toString();
				ndx++;
			}
		} else {
			ClassDescriptor classDescriptor = ClassIntrospector.lookup(bean.getClass());

			properties = classDescriptor.getAllBeanGetterNames(suppressSecurity);
		}

		return properties;
	}

	/**
	 * Starts visiting properties.
	 */
	public void visit() {
		String[] properties = resolveProperties(source, false);

		for (String name : properties) {
			if (excludeNames != null) {
				if (ArraysUtil.contains(excludeNames, name) == true) {
					continue;
				}
			}

			if (includeNames != null)  {
				if (ArraysUtil.contains(includeNames, name) == false) {
					continue;
				}
			}

			Object value;

			if (suppressSecurity) {
				value = BeanUtil.getDeclaredProperty(source, name);
			} else {
				value = BeanUtil.getProperty(source, name);
			}

			if (value == null && ignoreNullValues) {
				continue;
			}

			visitProperty(name, value);
		}
	}

	/**
	 * Invoked for each visited property. Returns <code>true</code> if
	 * visiting should continue, otherwise <code>false</code> to stop.
	 */
	protected abstract boolean visitProperty(String name, Object value);
}
