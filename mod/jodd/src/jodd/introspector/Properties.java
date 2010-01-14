// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Bean properties.
 */
class Properties {

	Methods getters = new Methods();
	String[] getterNames = new String[0];
	Methods setters = new Methods();
	String[] setterNames = new String[0];

	ArrayList<String> getterNameList;
	ArrayList<String> setterNameList;

	void addMethod(String name, Method method) {
		if (name.charAt(0) == '-') {
			name = name.substring(1);
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