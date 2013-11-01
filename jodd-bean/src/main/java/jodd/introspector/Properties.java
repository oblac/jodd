// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static jodd.util.ReflectUtil.METHOD_GET_PREFIX;
import static jodd.util.ReflectUtil.METHOD_IS_PREFIX;
import static jodd.util.ReflectUtil.NO_PARAMETERS;

/**
 * Bean properties collection.
 */
class Properties {

	private final ClassDescriptor classDescriptor;
	private final Map<String, PropertyDescriptor> propertyDescriptors;

	private final Methods getters;
	private String[] getterNames;
	private final Methods setters;
	private String[] setterNames;

	Properties(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;

		this.getters = new Methods(classDescriptor, 0);
		this.setters = new Methods(classDescriptor, 0);

		this.propertyDescriptors = new HashMap<String, PropertyDescriptor>();
	}

	/**
	 * Adds a setter and/or getter method to the property.
	 * If property is already defined, it will be updated (by new instance of
	 * {@link PropertyDescriptor}).
	 */
	void addProperty(String name, MethodDescriptor methodDescriptor, boolean isSetter) {
		// todo remove
		getterNames = null;
		setterNames = null;

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

	/**
	 * Adds getter or setter to collection.
	 */
	void addMethod(String name, Method method) {

		getterNames = null;
		setterNames = null;

		if (name.charAt(0) == '-') {
			name = name.substring(1);

			// check for special case of double get/is
			Method existingGetterMethod = getters.getMethod(name, NO_PARAMETERS);
			if (existingGetterMethod != null) {
				// getter with the same name already exist
				String methodName = method.getName();
				String existingMethodName = existingGetterMethod.getName();
				if (
						existingMethodName.startsWith(METHOD_GET_PREFIX) &&
						methodName.startsWith(METHOD_IS_PREFIX)) {
					getters.removeMethods(name);	// remove getter to use ister instead
				} else if (
						existingMethodName.startsWith(METHOD_IS_PREFIX) &&
						methodName.startsWith(METHOD_GET_PREFIX)) {
					return;		// ignore getter when ister exist
				}
			}

			getters.addMethod(name, method);
		} else if (name.charAt(0) == '+') {
			name = name.substring(1);
			setters.addMethod(name, method);
		} else {
			throw new IllegalArgumentException();
		}
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns getters collection.
	 */
	Methods getGetters() {
		return getters;
	}

	/**
	 * Returns getter names. Cached. Lazy.
	 */
	String[] getGetterNames() {
		if (getterNames == null) {
			Method[] getterMethods = getters.getAllMethods();
			String[] names = new String[getterMethods.length];

			for (int i = 0; i < getterMethods.length; i++) {
				names[i] = ReflectUtil.getBeanPropertyGetterName(getterMethods[i]);
			}

			getterNames = names;
		}
		return getterNames;
	}

	/**
	 * Returns setters collection.
	 */
	Methods getSetters() {
		return setters;
	}

	/**
	 * Returns setter names. Cached. Lazy.
	 */
	String[] getSetterNames() {
		if (setterNames == null) {
			Method[] setterMethods = setters.getAllMethods();
			String[] names = new String[setterMethods.length];

			for (int i = 0; i < setterMethods.length; i++) {
				names[i] = ReflectUtil.getBeanPropertySetterName(setterMethods[i]);
			}

			setterNames = names;
		}
		return setterNames;
	}

	/**
	 * Returns {@link PropertyDescriptor property descriptor}.
	 */
	PropertyDescriptor getProperty(String name) {
		return propertyDescriptors.get(name);
	}

}