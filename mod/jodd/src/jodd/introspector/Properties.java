// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.StringPool;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static jodd.util.ReflectUtil.METHOD_GET_PREFIX;
import static jodd.util.ReflectUtil.METHOD_IS_PREFIX;
import static jodd.util.ReflectUtil.NO_PARAMETERS;

/**
 * Bean properties.
 */
class Properties {

	Methods getters = new Methods();
	String[] getterNames = StringPool.EMPTY_ARRAY;
	Methods setters = new Methods();
	String[] setterNames = StringPool.EMPTY_ARRAY;

	ArrayList<String> getterNameList;
	ArrayList<String> setterNameList;

	void addMethod(String name, Method method) {
		if (name.charAt(0) == '-') {
			name = name.substring(1);

			// check for special case of double get/is
			Method existingMethod = getters.lookupMethod(name, NO_PARAMETERS);
			if (existingMethod != null) {
				// getter with the same name already exist
				String methodName = method.getName();
				String existingMethodName = existingMethod.getName();
				if (
						existingMethodName.startsWith(METHOD_GET_PREFIX) &&
						methodName.startsWith(METHOD_IS_PREFIX)) {
					getters.removeAllMethodsForName(name);	// remove getter to use ister instead of it
					getterNameList.remove(name);
				} else if (
						existingMethodName.startsWith(METHOD_IS_PREFIX) &&
						methodName.startsWith(METHOD_GET_PREFIX)) {
					return;		// ignore getter when ister exist
				}
			}

			getters.addMethod(name, method);
			if (getterNameList == null) {
				getterNameList = new ArrayList<String>();
			}
			getterNameList.add(name);
		} else if (name.charAt(0) == '+') {
			name = name.substring(1);
			setters.addMethod(name, method);
			if (setterNameList == null) {
				setterNameList = new ArrayList<String>();
			}
			setterNameList.add(name);
		}
	}

	void lock() {
		if (getterNameList != null) {
			getterNames = new String[getterNameList.size()];
			for (int i = 0; i < getterNameList.size(); i++) {
				getterNames[i] = getterNameList.get(i);
			}
			getterNameList = null;
		}
		if (setterNameList != null) {
			setterNames = new String[setterNameList.size()];
			for (int i = 0; i < setterNameList.size(); i++) {
				setterNames[i] = setterNameList.get(i);
			}
			setterNameList = null;
		}
		getters.lock();
		setters.lock();
	}

	public String[] getAllBeanGetterNames() {
		return getterNames;
	}
	public String[] getAllBeanSetterNames() {
		return setterNames;
	}
}