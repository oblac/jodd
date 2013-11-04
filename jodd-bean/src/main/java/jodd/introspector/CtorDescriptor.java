// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Constructor;

/**
 * Constructor descriptor.
 */
public class CtorDescriptor extends Descriptor {

	protected final Constructor constructor;
	protected final Class[] parameters;

	public CtorDescriptor(ClassDescriptor classDescriptor, Constructor constructor) {
		super(classDescriptor, ReflectUtil.isPublic(constructor));
		this.constructor = constructor;
		this.parameters = constructor.getParameterTypes();

		ReflectUtil.forceAccess(constructor);
	}

	/**
	 * Returns constructor.
	 */
	public Constructor getConstructor() {
		return constructor;
	}

	/**
	 * Returns constructors parameters. The returned array
	 * is not cloned.
	 */
	public Class[] getParameters() {
		return parameters;
	}

	/**
	 * Returns <code>true</code> if this is a default constructor
	 * (with no parameters).
	 */
	public boolean isDefault() {
		return parameters.length == 0;
	}

}