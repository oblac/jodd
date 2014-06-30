// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.ArraysUtil;

import java.util.ArrayList;
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
	 * Flag for enabling declared properties, or just public ones.
	 */
	protected boolean declared;
	/**
	 * Defines if null values should be ignored.
	 */
	protected boolean ignoreNullValues;
	/**
	 * Defines if fields should be included.
	 */
	protected boolean includeFields;

	// ---------------------------------------------------------------- util

	/**
	 * Returns all bean property names.
	 */
	protected String[] getAllBeanPropertyNames(Class type, boolean declared) {
		ClassDescriptor classDescriptor = ClassIntrospector.lookup(type);

		PropertyDescriptor[] propertyDescriptors = classDescriptor.getAllPropertyDescriptors();

		ArrayList<String> names = new ArrayList<String>(propertyDescriptors.length);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			MethodDescriptor getter = propertyDescriptor.getReadMethodDescriptor();
			if (getter != null) {
				if (getter.matchDeclared(declared)) {
					names.add(propertyDescriptor.getName());
				}
			}
			else if (includeFields) {
				FieldDescriptor field = propertyDescriptor.getFieldDescriptor();
				if (field != null) {
					if (field.matchDeclared(declared)) {
						names.add(field.getName());
					}
				}
			}
		}

		return names.toArray(new String[names.size()]);
	}

	/**
	 * Returns an array of bean properties. If bean is a <code>Map</code>,
	 * all its keys will be returned.
	 */
	protected String[] resolveProperties(Object bean, boolean declared) {
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
			properties = getAllBeanPropertyNames(bean.getClass(), declared);
		}

		return properties;
	}

	/**
	 * Starts visiting properties.
	 */
	public void visit() {
		String[] properties = resolveProperties(source, declared);

		for (String name : properties) {
			if (name == null) {
				continue;
			}

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

			if (declared) {
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
