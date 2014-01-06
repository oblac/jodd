// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import static jodd.util.ReflectUtil.METHOD_GET_PREFIX;
import static jodd.util.ReflectUtil.METHOD_IS_PREFIX;

/**
 * Bean properties collection. Property in Java is defined as a pair of
 * read and write method. In Jodd, property can be extended with field
 * definition. Moreover, properties will include just single fields.
 * This behavior can be controlled via {@link ClassDescriptor}.
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

	/**
	 * Inspects all properties of target type.
	 */
	protected HashMap<String, PropertyDescriptor> inspectProperties() {
		boolean scanAccessible = classDescriptor.isScanAccessible();
		Class type = classDescriptor.getType();

		HashMap<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();

		Method[] methods = scanAccessible ? ReflectUtil.getAccessibleMethods(type) : ReflectUtil.getSupportedMethods(type);
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

		if (classDescriptor.isIncludeFieldsAsProperties()) {
			FieldDescriptor[] fieldDescriptors = classDescriptor.getAllFieldDescriptors();
			String prefix = classDescriptor.getPropertyFieldPrefix();

			for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
				String name = fieldDescriptor.getField().getName();

				if (prefix != null) {
					if (!name.startsWith(prefix)) {
						continue;
					}
					name = name.substring(prefix.length());
				}

				if (!map.containsKey(name)) {
					// add missing field as a potential property
					map.put(name, createPropertyDescriptor(name, fieldDescriptor));
				}
			}

		}

		return map;
	}


	/**
	 * Adds a setter and/or getter method to the property.
	 * If property is already defined, the new, updated, definition will be created.
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

		PropertyDescriptor propertyDescriptor = createPropertyDescriptor(name, getterMethod, setterMethod);

		map.put(name, propertyDescriptor);
	}

	/**
	 * Creates new {@link PropertyDescriptor}. Note that this method may be called
	 * up to three times (depends on use case) for the same property. Each time when
	 * a property is updated, a new definition is created with updated information.
	 */
	protected PropertyDescriptor createPropertyDescriptor(String name, MethodDescriptor getterMethod, MethodDescriptor setterMethod) {
		return new PropertyDescriptor(classDescriptor, name, getterMethod, setterMethod);
	}

	/**
	 * Creates new field-only {@link PropertyDescriptor}. It will be invoked only once.
	 */
	protected PropertyDescriptor createPropertyDescriptor(String name, FieldDescriptor fieldDescriptor) {
			return new PropertyDescriptor(classDescriptor, name, fieldDescriptor);
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link PropertyDescriptor property descriptor}.
	 */
	public PropertyDescriptor getPropertyDescriptor(String name) {
		return propertyDescriptors.get(name);
	}

	/**
	 * Returns all property descriptors.
	 */
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