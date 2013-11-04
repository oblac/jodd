// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Constructor;

/**
 * Constructor descriptor.
 */
public class CtorDescriptor {

	protected final ClassDescriptor classDescriptor;
	protected final Constructor constructor;
	protected final Class[] parameters;
	protected final boolean isPublic;

	public CtorDescriptor(ClassDescriptor classDescriptor, Constructor constructor) {
		this.classDescriptor = classDescriptor;
		this.constructor = constructor;
		this.parameters = constructor.getParameterTypes();
		this.isPublic = ReflectUtil.isPublic(constructor);

		ReflectUtil.forceAccess(constructor);
	}

	public ClassDescriptor getClassDescriptor() {
		return classDescriptor;
	}

	public Constructor getConstructor() {
		return constructor;
	}

	public Class[] getParameters() {
		return parameters;
	}

	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * Returns <code>true</code> if descriptor matches required declared flag.
	 */
	public boolean matchDeclared(boolean declared) {
		if (!declared) {
			return isPublic;
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if this is a default constructor
	 * (with no parameters).
	 */
	public boolean isDefault() {
		return parameters.length == 0;
	}

}