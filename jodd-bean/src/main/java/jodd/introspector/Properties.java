// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;

import static jodd.util.ReflectUtil.METHOD_GET_PREFIX;
import static jodd.util.ReflectUtil.METHOD_IS_PREFIX;
import static jodd.util.ReflectUtil.NO_PARAMETERS;

/**
 * Bean properties collection.
 */
class Properties {

	private final Methods getters;
	private String[] getterNames;
	private final Methods setters;
	private String[] setterNames;

	Properties(ClassDescriptor classDescriptor) {
		this.getters = new Methods(classDescriptor, 0);
		this.setters = new Methods(classDescriptor, 0);
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
			Method existingMethod = getters.getMethod(name, NO_PARAMETERS);
			if (existingMethod != null) {
				// getter with the same name already exist
				String methodName = method.getName();
				String existingMethodName = existingMethod.getName();
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

}