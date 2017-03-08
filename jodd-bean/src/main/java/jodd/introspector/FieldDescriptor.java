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

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Field descriptor. Holds additional field data,
 * that might be specific to implementation class.
 */
public class FieldDescriptor extends Descriptor implements Getter, Setter {

	protected final Field field;
	protected final Type type;
	protected final Class rawType;
	protected final Class rawComponentType;
	protected final Class rawKeyComponentType;

	/**
	 * Creates new field descriptor and resolve all additional field data.
	 * Also, forces access to a field.
	 */
	public FieldDescriptor(ClassDescriptor classDescriptor, Field field) {
		super(classDescriptor, ReflectUtil.isPublic(field));
		this.field = field;
		this.type = field.getGenericType();
		this.rawType = ReflectUtil.getRawType(type, classDescriptor.getType());

		Class[] componentTypes = ReflectUtil.getComponentTypes(type, classDescriptor.getType());
		if (componentTypes != null) {
			this.rawComponentType = componentTypes[componentTypes.length - 1];
			this.rawKeyComponentType = componentTypes[0];
		} else {
			this.rawComponentType = null;
			this.rawKeyComponentType = null;
		}

		ReflectUtil.forceAccess(field);
	}

	/**
	 * Returns field name.
	 */
	@Override
	public String getName() {
		return field.getName();
	}

	/**
	 * Returns field.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Returns fields raw type.
	 */
	public Class getRawType() {
		return rawType;
	}

	/**
	 * Returns fields raw component type. Returns <code>null</code>
	 * if field has no component type.
	 */
	public Class getRawComponentType() {
		return rawComponentType;
	}

	/**
	 * Returns fields raw component type. Returns <code>null</code>
	 * if field has no component type.
	 */
	public Class getRawKeyComponentType() {
		return rawKeyComponentType;
	}

	/**
	 * Resolves raw component type for given index. This value is NOT cached.
	 */
	public Class[] resolveRawComponentTypes() {
		return ReflectUtil.getComponentTypes(type, classDescriptor.getType());
	}

	// ---------------------------------------------------------------- getter/setter

	public Object invokeGetter(Object target) throws InvocationTargetException, IllegalAccessException {
		return field.get(target);
	}

	public Class getGetterRawType() {
		return getRawType();
	}

	public Class getGetterRawComponentType() {
		return getRawComponentType();
	}

	public Class getGetterRawKeyComponentType() {
		return getRawKeyComponentType();
	}

	public void invokeSetter(Object target, Object argument) throws IllegalAccessException {
		field.set(target, argument);
	}

	public Class getSetterRawType() {
		return getRawType();
	}

	public Class getSetterRawComponentType() {
		return getRawComponentType();
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return classDescriptor.getType().getSimpleName() + '#' + field.getName();
	}

}