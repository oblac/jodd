// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Methods
 */
class Methods {

	HashMap<String, List<MethodDescriptor>> mMapTemp = new HashMap<String, List<MethodDescriptor>>();
	HashMap<String, MethodEntry> mMap;

	Method[] allMethods;
	int count;

	boolean locked;

	void checkLocked() {
		if (locked == true) {
			throw new IllegalStateException();	// introspection finished
		}
	}

	void addMethod(String name, Method method) {
		checkLocked();
		count++;
		List<MethodDescriptor> paramList = mMapTemp.get(name);
		if (paramList == null) {
			paramList = new ArrayList<MethodDescriptor>();
			mMapTemp.put(name, paramList);
		}
		paramList.add(new MethodDescriptor(method));
	}

	void lock() {
		HashMap<String, MethodEntry> newMap = new HashMap<String, MethodEntry>(mMapTemp.size());
		locked = true;
		allMethods = new Method[count];
		int k = 0;
		for (String name : mMapTemp.keySet()) {
			List<MethodDescriptor> list = mMapTemp.get(name);
			if (list.isEmpty()) {
				continue;
			}
			MethodEntry entry = new MethodEntry();
			entry.size = list.size();
			entry.methodsList = new Method[entry.size];
			entry.paramterTypes = new Class[entry.size][];
			for (int i = 0; i < entry.size; i++) {
				MethodDescriptor md = list.get(i);
				allMethods[k] = md.method;
				k++;
				entry.methodsList[i] = md.method;
				entry.paramterTypes[i] = md.parameterTypes;
			}
			newMap.put(name, entry);
		}
		mMap = newMap;
		mMapTemp = null;
	}

	// ---------------------------------------------------------------- get

	Method getMethod(String name, Class[] paramTypes) {
		MethodEntry entry = mMap.get(name);
		if (entry == null) {
			return null;
		}
		for (int i = 0; i < entry.size; i++) {
			if (ReflectUtil.compareParameters(entry.paramterTypes[i], paramTypes) == true) {
				return entry.methodsList[i];
			}
		}
		return null;
	}

	Method getMethod(String name) {
		MethodEntry entry = mMap.get(name);
		if (entry == null) {
			return null;
		}
		if (entry.size != 1) {
			throw new IllegalArgumentException("Method '" + name + "' is not unique");
		}
		return entry.methodsList[0];
	}

	Method[] getAllMethods(String name) {
		MethodEntry entry = mMap.get(name);
		if (entry == null) {
			return null;
		}
		return entry.methodsList;
	}

	Method[] getAllMethods() {
		return allMethods;
	}

	// ---------------------------------------------------------------- introspection time

	Method lookupMethod(String name, Class[] paramTypes) {
		checkLocked();
		List<MethodDescriptor> list = mMapTemp.get(name);
		if (list == null) {
			return null;
		}
		for (MethodDescriptor md : list) {
			if (ReflectUtil.compareParameters(md.parameterTypes, paramTypes) == true) {
				return md.method;
			}
		}
		return null;
	}

	public void removeAllMethodsForName(String name) {
		checkLocked();
		List<MethodDescriptor> paramList = mMapTemp.remove(name);
		if (paramList != null) {
			count -= paramList.size();
		}
	}
}