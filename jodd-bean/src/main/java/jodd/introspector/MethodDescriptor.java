// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Method descriptor. Holds additional method data,
 * that might be specific to implementation class.
 */
public class MethodDescriptor extends Descriptor implements Getter, Setter {

	protected final Method method;
	protected final Type returnType;
	protected final Class rawReturnType;
	protected final Class rawReturnComponentType;
	protected final Class rawReturnKeyComponentType;
	protected final Class[] rawParameterTypes;
	protected final Class[] rawParameterComponentTypes;

	public MethodDescriptor(ClassDescriptor classDescriptor, Method method) {
		super(classDescriptor, ReflectUtil.isPublic(method));
		this.method = method;
		this.returnType = method.getGenericReturnType();
		this.rawReturnType = ReflectUtil.getRawType(returnType, classDescriptor.getType());

		Class[] componentTypes = ReflectUtil.getComponentTypes(returnType, classDescriptor.getType());
		if (componentTypes != null) {
			this.rawReturnComponentType = componentTypes[componentTypes.length - 1];
			this.rawReturnKeyComponentType = componentTypes[0];
		} else {
			this.rawReturnComponentType = null;
			this.rawReturnKeyComponentType = null;
		}

		ReflectUtil.forceAccess(method);

		Type[] params = method.getGenericParameterTypes();
		Type[] genericParams = method.getGenericParameterTypes();

		rawParameterTypes = new Class[params.length];
		rawParameterComponentTypes = genericParams.length == 0 ? null : new Class[params.length];

		for (int i = 0; i < params.length; i++) {
			Type type = params[i];
			rawParameterTypes[i] = ReflectUtil.getRawType(type, classDescriptor.getType());
			if (rawParameterComponentTypes != null) {
				rawParameterComponentTypes[i] = ReflectUtil.getComponentType(genericParams[i], classDescriptor.getType(), -1);
			}
		}
	}

	/**
	 * Returns method name.
	 */
	public String getName() {
		return method.getName();
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
	 * Returns raw component type of return type.
	 * May be <code>null</code> if return type does not have
	 * components.
	 */
	public Class getRawReturnKeyComponentType() {
		return rawReturnKeyComponentType;
	}

	/**
	 * Resolves raw return component types
	 * This value is NOT cached.
	 */
	public Class[] resolveRawReturnComponentTypes() {
		return ReflectUtil.getComponentTypes(returnType, classDescriptor.getType());
	}

	/**
	 * Returns raw parameter types.
	 */
	public Class[] getRawParameterTypes() {
		return rawParameterTypes;
	}

	/**
	 * Returns raw parameter component types. Returns <code>null</code>
	 * if data does not exist.
	 */
	public Class[] getRawParameterComponentTypes() {
		return rawParameterComponentTypes;
	}

	// ---------------------------------------------------------------- getter/setter

	public Object invokeGetter(Object target) throws InvocationTargetException, IllegalAccessException {
		return method.invoke(target, null);
	}

	public Class getGetterRawType() {
		return getRawReturnType();
	}

	public Class getGetterRawComponentType() {
		return getRawReturnComponentType();
	}

	public Class getGetterRawKeyComponentType() {
		return getRawReturnKeyComponentType();
	}

	public void invokeSetter(Object target, Object argument) throws IllegalAccessException, InvocationTargetException {
		method.invoke(target, argument);
	}

	public Class getSetterRawType() {
		return getRawParameterTypes()[0];
	}

	public Class getSetterRawComponentType() {
		Class[] ts = getRawParameterComponentTypes();
		if (ts == null) {
			return null;
		}
		return ts[0];
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return classDescriptor.getType().getSimpleName() + '#' + method.getName() + "()";
	}

}