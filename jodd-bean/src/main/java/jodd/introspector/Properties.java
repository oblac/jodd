// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import static jodd.util.ReflectUtil.METHOD_GET_PREFIX;
import static jodd.util.ReflectUtil.METHOD_IS_PREFIX;

/**
 * Bean properties collection.
 */
public class Properties {

	protected final ClassDescriptor classDescriptor;
	protected final HashMap<String, PropertyDescriptor> propertyDescriptors;

	// cache
	private PropertyDescriptor[] allProperties;

	public Properties(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.propertyDescriptors = inspectProperties();
	}

	protected HashMap<String, PropertyDescriptor> inspectProperties() {
		boolean accessibleOnly = classDescriptor.isAccessibleOnly();
		Class type = classDescriptor.getType();

		HashMap<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();

		Method[] methods = accessibleOnly ? ReflectUtil.getAccessibleMethods(type) : ReflectUtil.getSupportedMethods(type);
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;			// ignore static methods
			}

			boolean add = false;
			boolean issetter = false;

			String propertyName = ReflectUtil.getBeanPropertyGetterName(method);
			if (propertyName != null) {
				add = true;
				issetter = false;
			} else {
				propertyName = ReflectUtil.getBeanPropertySetterName(method);
				if (propertyName != null) {
					add = true;
					issetter = true;
				}
			}

			if (add == true) {
				MethodDescriptor methodDescriptor = classDescriptor.getMethodDescriptor(method.getName(), method.getParameterTypes(), true);
				addProperty(map, propertyName, methodDescriptor, issetter);
			}
		}

		return map;
	}


	/**
	 * Adds a setter and/or getter method to the property.
	 * If property is already defined, it will be updated (by new instance of
	 * {@link PropertyDescriptor}).
	 */
	protected void addProperty(HashMap<String, PropertyDescriptor> map, String name, MethodDescriptor methodDescriptor, boolean isSetter) {
		MethodDescriptor setterMethod = isSetter ? methodDescriptor : null;
		MethodDescriptor getterMethod = isSetter ? null : methodDescriptor;

		PropertyDescriptor existing = map.get(name);

		if (existing == null) {
			// new property, just add it
			PropertyDescriptor propertyDescriptor = createPropertyDescriptor(name, getterMethod, setterMethod);

			map.put(name, propertyDescriptor);
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

		PropertyDescriptor propertyDescriptor =  createPropertyDescriptor(name, getterMethod, setterMethod);

		map.put(name, propertyDescriptor);
	}

	/**
	 * Creates new {@link PropertyDescriptor}.
	 */
	protected PropertyDescriptor createPropertyDescriptor(String name, MethodDescriptor getterMethod, MethodDescriptor setterMethod) {
		return new PropertyDescriptor(classDescriptor, name, getterMethod, setterMethod);
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link PropertyDescriptor property descriptor}.
	 */
	public PropertyDescriptor getPropertyDescriptor(String name) {
		return propertyDescriptors.get(name);
	}

	public PropertyDescriptor[] getAllPropertyDescriptors() {
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