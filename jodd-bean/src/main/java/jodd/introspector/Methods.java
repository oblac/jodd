// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ArraysUtil;
import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Methods collection.
 */
class Methods {

	private final ClassDescriptor classDescriptor;
	private final HashMap<String, MethodDescriptor[]> methodsMap;

	// cache
	private MethodDescriptor[] allMethods;

	Methods(ClassDescriptor classDescriptor, int maxMethods) {
		this.classDescriptor = classDescriptor;
		if (maxMethods == 0) {
			maxMethods = 16;
		}
		this.methodsMap = new HashMap<String, MethodDescriptor[]>(maxMethods);
	}

	void addMethod(String name, Method method) {
		MethodDescriptor[] mds = methodsMap.get(name);

		if (mds == null) {
			mds = new MethodDescriptor[1];
		} else {
			mds = ArraysUtil.resize(mds, mds.length + 1);
		}
		methodsMap.put(name, mds);

		mds[mds.length - 1] = classDescriptor.createMethodDescriptor(method);

		// reset cache
		allMethods = null;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns a method that matches given name and parameter types.
	 * Returns <code>null</code> if method is not found.
	 */
	MethodDescriptor getMethodDescriptor(String name, Class[] paramTypes) {
		MethodDescriptor[] methodDescriptors = methodsMap.get(name);
		if (methodDescriptors == null) {
			return null;
		}
		for (int i = 0; i < methodDescriptors.length; i++) {
			Method m = methodDescriptors[i].getMethod();
			if (ReflectUtil.compareParameters(m.getParameterTypes(), paramTypes) == true) {
				return methodDescriptors[i];
			}
		}
		return null;
	}

	/**
	 * Returns method descriptor for given name. If more then one methods with
	 * the same name exists, one method will be returned (not determined which one).
	 * Returns <code>null</code> if no method exist in this collection by given name.
	 * @see #getMethodDescriptor(String, Class[])
	 */
	MethodDescriptor getMethodDescriptor(String name) {
		MethodDescriptor[] methodDescriptors = methodsMap.get(name);
		if (methodDescriptors == null) {
			return null;
		}
		if (methodDescriptors.length != 1) {
			throw new IllegalArgumentException("Method name not unique: " + name);
		}
		return methodDescriptors[0];
	}

	/**
	 * Returns all methods for given name. Returns <code>null</code> if method not found.
	 */
	MethodDescriptor[] getAllMethodDescriptors(String name) {
		return methodsMap.get(name);
	}

	/**
	 * Returns all methods. Cached. Lazy.
	 */
	MethodDescriptor[] getAllMethods() {
		if (allMethods == null) {
			List<MethodDescriptor> allMethodsList = new ArrayList<MethodDescriptor>();

			for (MethodDescriptor[] methodDescriptors : methodsMap.values()) {
				for (MethodDescriptor methodDescriptor : methodDescriptors) {
					allMethodsList.add(methodDescriptor);
				}
			}

			allMethods = allMethodsList.toArray(new MethodDescriptor[allMethodsList.size()]);
		}
		return allMethods;
	}

	/**
	 * Returns number of methods in this collection.
	 */
	int getCount() {
		return getAllMethods().length;
	}

}