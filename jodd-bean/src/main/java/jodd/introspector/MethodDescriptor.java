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

import jodd.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Method descriptor. Holds additional method data,
 * that might be specific to implementation class.
 */
public class MethodDescriptor extends Descriptor implements Getter, Setter {

	private static final MethodParamDescriptor[] NO_PARAMS = new MethodParamDescriptor[0];

	protected final Method method;
	protected final Type returnType;
	protected final Class rawReturnType;
	protected final Class rawReturnComponentType;
	protected final Class rawReturnKeyComponentType;
	protected final MethodParamDescriptor[] parameters;
	protected final MapperFunction mapperFunction;

	public MethodDescriptor(final ClassDescriptor classDescriptor, final Method method) {
		super(classDescriptor, ClassUtil.isPublic(method));
		this.method = method;
		this.returnType = method.getGenericReturnType();
		this.rawReturnType = ClassUtil.getRawType(returnType, classDescriptor.getType());

		Class[] componentTypes = ClassUtil.getComponentTypes(returnType, classDescriptor.getType());
		if (componentTypes != null) {
			this.rawReturnComponentType = componentTypes[componentTypes.length - 1];
			this.rawReturnKeyComponentType = componentTypes[0];
		} else {
			this.rawReturnComponentType = null;
			this.rawReturnKeyComponentType = null;
		}

		// force access

		ClassUtil.forceAccess(method);

		// mapper

		final Mapper mapper = method.getAnnotation(Mapper.class);

		if (mapper != null) {
			mapperFunction = MapperFunctionInstances.get().lookup(mapper.value());
		} else {
			mapperFunction = null;
		}

		// parameters

		if (method.getParameterCount() == 0) {
			parameters = NO_PARAMS;
		}
		else {
			parameters = new MethodParamDescriptor[method.getParameterCount()];

			Class[] params = method.getParameterTypes();
			Type[] genericParams = method.getGenericParameterTypes();

			for (int i = 0; i < params.length; i++) {
				final Class parameterType = params[i];
				final Class rawParameterType = genericParams.length == 0 ?
					parameterType :
					ClassUtil.getRawType(genericParams[i], classDescriptor.getType());
				final Class rawParameterComponentType = genericParams.length == 0 ?
					null :
					ClassUtil.getComponentType(genericParams[i], classDescriptor.getType(), -1);

				parameters[i] = new MethodParamDescriptor(parameterType, rawParameterType, rawParameterComponentType);
			}
		}
	}

	/**
	 * Returns method name.
	 */
	@Override
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
		return ClassUtil.getComponentTypes(returnType, classDescriptor.getType());
	}

	/**
	 * Returns {@link MethodParamDescriptor method parameteres}.
	 */
	public MethodParamDescriptor[] getParameters() {
		return parameters;
	}

	/**
	 * Returns number of parameters.
	 */
	public int getParameterCount() {
		return parameters.length;
	}

	// ---------------------------------------------------------------- getter/setter

	@Override
	public Object invokeGetter(final Object target) throws InvocationTargetException, IllegalAccessException {
		return method.invoke(target, null);
	}

	@Override
	public Class getGetterRawType() {
		return getRawReturnType();
	}

	@Override
	public Class getGetterRawComponentType() {
		return getRawReturnComponentType();
	}

	@Override
	public Class getGetterRawKeyComponentType() {
		return getRawReturnKeyComponentType();
	}

	@Override
	public void invokeSetter(final Object target, final Object argument) throws IllegalAccessException, InvocationTargetException {
		method.invoke(target, argument);
	}

	@Override
	public Class getSetterRawType() {
		return getParameters()[0].getRawType();
	}

	@Override
	public Class getSetterRawComponentType() {
		return getParameters()[0].getRawComponentType();
	}

	/**
	 * Returns {@link MapperFunction} if defined, or {@code null} otherwise.
	 */
	@Override
	public MapperFunction getMapperFunction() {
		return mapperFunction;
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return classDescriptor.getType().getSimpleName() + '#' + method.getName() + "()";
	}

}