// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Method descriptor.
 */
public class MethodDescriptor {

	protected final Method method;
	protected final Class[] rawParameterTypes;
	protected final Class rawReturnType;

	public MethodDescriptor(Method method, Class implClass) {
		this.method = method;
		this.rawReturnType = ReflectUtil.getRawType(method.getGenericReturnType(), implClass);

		Type[] params = method.getGenericParameterTypes();
		rawParameterTypes = new Class[params.length];

		for (int i = 0; i < params.length; i++) {
			Type type = params[i];
			rawParameterTypes[i] = ReflectUtil.getRawType(type, implClass);
		}
	}

	/**
	 * Returns method.
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Returns raw return type.
	 */
	public Class getRawReturnType() {
		return rawReturnType;
	}

	/**
	 * Returns raw parameter types.
	 */
	public Class[] getRawParameterTypes() {
		return rawParameterTypes;
	}

}