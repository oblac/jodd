// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Method descriptor.  Holds additional method data,
 * that might be specific to implementation class.
 */
public class MethodDescriptor extends Descriptor implements Getter, Setter {

	protected final Method method;
	protected final Type returnType;
	protected final Class rawReturnType;
	protected final Class rawReturnComponentType;
	protected final Class rawReturnKeyComponentType;
	protected final Class[] rawParameterTypes;

	public MethodDescriptor(ClassDescriptor classDescriptor, Method method) {
		super(classDescriptor, ReflectUtil.isPublic(method));
		this.method = method;
		this.returnType = method.getGenericReturnType();
		this.rawReturnType = ReflectUtil.getRawType(returnType, classDescriptor.getType());
		this.rawReturnComponentType = ReflectUtil.getComponentType(returnType, classDescriptor.getType());
		this.rawReturnKeyComponentType = ReflectUtil.getComponentType(returnType, classDescriptor.getType(), 0);

		ReflectUtil.forceAccess(method);

		Type[] params = method.getGenericParameterTypes();
		rawParameterTypes = new Class[params.length];

		for (int i = 0; i < params.length; i++) {
			Type type = params[i];
			rawParameterTypes[i] = ReflectUtil.getRawType(type, classDescriptor.getType());
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
	 * Returns raw component type of return type.
	 * May be <code>null</code> if return type does not have
	 * components.
	 */
	public Class getRawReturnKeyComponentType() {
		return rawReturnKeyComponentType;
	}

	/**
	 * Resolves raw return component type for given index.
	 * This value is NOT cached.
	 */
	public Class resolveRawReturnComponentType(int index) {
		return ReflectUtil.getComponentType(returnType, classDescriptor.getType(), index);
	}

	/**
	 * Returns raw parameter types.
	 */
	public Class[] getRawParameterTypes() {
		return rawParameterTypes;
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

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return classDescriptor.getType().getSimpleName() + '#' + method.getName() + "()";
	}

}