// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Method descriptor.  Holds additional method data,
 * that might be specific to implementation class.
 */
public class MethodDescriptor {

	protected final Method method;
	protected final Class[] rawParameterTypes;
	protected final Class rawReturnType;
	protected final Class rawReturnComponentType;

	public MethodDescriptor(Method method, Class implClass) {
		this.method = method;
		Type type = method.getGenericReturnType();
		this.rawReturnType = ReflectUtil.getRawType(type, implClass);
		this.rawReturnComponentType = ReflectUtil.getComponentType(type, implClass);

		Type[] params = method.getGenericParameterTypes();
		rawParameterTypes = new Class[params.length];

		for (int i = 0; i < params.length; i++) {
			type = params[i];
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
	 * Returns raw component type of return type.
	 * May be <code>null</code> if return type does not have
	 * components.
	 */
	public Class getRawReturnComponentType() {
		return rawReturnComponentType;
	}

	/**
	 * Returns raw parameter types.
	 */
	public Class[] getRawParameterTypes() {
		return rawParameterTypes;
	}

}