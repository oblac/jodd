// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.introspector;

import jodd.util.ArraysUtil;
import jodd.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

	public Methods(final ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.methodsMap = inspectMethods();
	}

	/**
	 * Inspects types methods and return map of {@link MethodDescriptor method descriptors}.
	 */
	protected HashMap<String, MethodDescriptor[]> inspectMethods() {
		boolean scanAccessible = classDescriptor.isScanAccessible();
		Class type = classDescriptor.getType();

		Method[] methods = scanAccessible ? ClassUtil.getAccessibleMethods(type) : ClassUtil.getSupportedMethods(type);

		HashMap<String, MethodDescriptor[]> map = new HashMap<>(methods.length);

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
	protected MethodDescriptor createMethodDescriptor(final Method method) {
		return new MethodDescriptor(classDescriptor, method);
	}


	// ---------------------------------------------------------------- get

	/**
	 * Returns a method that matches given name and parameter types.
	 * Returns <code>null</code> if method is not found.
	 */
	public MethodDescriptor getMethodDescriptor(final String name, final Class[] paramTypes) {
		MethodDescriptor[] methodDescriptors = methodsMap.get(name);
		if (methodDescriptors == null) {
			return null;
		}
		for (MethodDescriptor methodDescriptor : methodDescriptors) {
			Method m = methodDescriptor.getMethod();
			if (ClassUtil.compareParameters(m.getParameterTypes(), paramTypes)) {
				return methodDescriptor;
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
	public MethodDescriptor getMethodDescriptor(final String name) {
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
	public MethodDescriptor[] getAllMethodDescriptors(final String name) {
		return methodsMap.get(name);
	}

	/**
	 * Returns all methods. Cached. Lazy.
	 */
	public MethodDescriptor[] getAllMethodDescriptors() {
		if (allMethods == null) {
			List<MethodDescriptor> allMethodsList = new ArrayList<>();

			for (MethodDescriptor[] methodDescriptors : methodsMap.values()) {
				Collections.addAll(allMethodsList, methodDescriptors);
			}

			MethodDescriptor[] allMethods = allMethodsList.toArray(new MethodDescriptor[0]);

			Arrays.sort(allMethods, new Comparator<MethodDescriptor>() {
				@Override
				public int compare(final MethodDescriptor md1, final MethodDescriptor md2) {
					return md1.getMethod().getName().compareTo(md2.getMethod().getName());
				}
			});

			this.allMethods = allMethods;
		}
		return allMethods;
	}

}