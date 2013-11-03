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
public class Methods {

	protected final ClassDescriptor classDescriptor;
	protected final HashMap<String, MethodDescriptor[]> methodsMap;

	// cache
	private MethodDescriptor[] allMethods;

	public Methods(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.methodsMap = inspectMethods();
	}

	protected HashMap<String, MethodDescriptor[]> inspectMethods() {
		boolean accessibleOnly = classDescriptor.isAccessibleOnly();
		Class type = classDescriptor.getType();

		Method[] methods = accessibleOnly ? ReflectUtil.getAccessibleMethods(type) : ReflectUtil.getSupportedMethods(type);

		HashMap<String, MethodDescriptor[]> map = new HashMap<String, MethodDescriptor[]>(methods.length);

		for (Method method : methods) {
			String methodName = method.getName();

			MethodDescriptor[] mds = map.get(methodName);

			if (mds == null) {
				mds = new MethodDescriptor[1];
			} else {
				mds = ArraysUtil.resize(mds, mds.length + 1);
			}

			map.put(methodName, mds);

			mds[mds.length - 1] = createMethodDescriptor(method);
		}

		return map;
	}

	/**
	 * Creates new {@code MethodDescriptor}.
	 */
	protected MethodDescriptor createMethodDescriptor(Method method) {
		return new MethodDescriptor(classDescriptor, method);
	}


	// ---------------------------------------------------------------- get

	/**
	 * Returns a method that matches given name and parameter types.
	 * Returns <code>null</code> if method is not found.
	 */
	public MethodDescriptor getMethodDescriptor(String name, Class[] paramTypes) {
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
	public MethodDescriptor getMethodDescriptor(String name) {
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
	public MethodDescriptor[] getAllMethodDescriptors(String name) {
		return methodsMap.get(name);
	}

	/**
	 * Returns all methods. Cached. Lazy.
	 */
	public MethodDescriptor[] getAllMethods() {
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

}