// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.util.HashMap;
import java.util.Map;

import static jodd.util.ReflectUtil.METHOD_GET_PREFIX;
import static jodd.util.ReflectUtil.METHOD_IS_PREFIX;

/**
 * Bean properties collection.
 */
class Properties {

	private final ClassDescriptor classDescriptor;
	private final Map<String, PropertyDescriptor> propertyDescriptors;

	// cache
	protected PropertyDescriptor[] allProperties;

	Properties(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.propertyDescriptors = new HashMap<String, PropertyDescriptor>();
	}

	/**
	 * Adds a setter and/or getter method to the property.
	 * If property is already defined, it will be updated (by new instance of
	 * {@link PropertyDescriptor}).
	 */
	void addProperty(String name, MethodDescriptor methodDescriptor, boolean isSetter) {
		allProperties = null;

		MethodDescriptor setterMethod = isSetter ? methodDescriptor : null;
		MethodDescriptor getterMethod = isSetter ? null : methodDescriptor;

		PropertyDescriptor existing = propertyDescriptors.get(name);

		if (existing == null) {
			// new property, just add it
			PropertyDescriptor propertyDescriptor =
					new PropertyDescriptor(classDescriptor, name, getterMethod, setterMethod);

			propertyDescriptors.put(name, propertyDescriptor);
			return;
		}

		if (!isSetter) {
			// use existing setter
			setterMethod = existing.getWriteMethodDescriptor();

			// check existing
			MethodDescriptor existingMethodDescriptor = existing.getReadMethodDescriptor();
			if (existingMethodDescriptor != null) {
				// check for special case of double get/is

				// getter with the same name already exist
				String methodName = methodDescriptor.getMethod().getName();
				String existingMethodName = existingMethodDescriptor.getMethod().getName();

				if (
						existingMethodName.startsWith(METHOD_IS_PREFIX) &&
						methodName.startsWith(METHOD_GET_PREFIX)) {

					// ignore getter when ister exist
					return;
				}
			}
		} else {
			// setter
			// use existing getter
			getterMethod = existing.getReadMethodDescriptor();
		}

		PropertyDescriptor propertyDescriptor =
				new PropertyDescriptor(classDescriptor, name, getterMethod, setterMethod);

		propertyDescriptors.put(name, propertyDescriptor);
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link PropertyDescriptor property descriptor}.
	 */
	PropertyDescriptor getProperty(String name) {
		return propertyDescriptors.get(name);
	}

	PropertyDescriptor[] getAllProperties() {
		if (allProperties == null) {
			PropertyDescriptor[] allProperties = new PropertyDescriptor[propertyDescriptors.size()];

			int index = 0;
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors.values()) {
				allProperties[index] = propertyDescriptor;
				index++;
			}

			this.allProperties = allProperties;
		}
		return allProperties;
	}

}