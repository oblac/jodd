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

package jodd.petite;

import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.ClassUtil;

import java.util.Collection;
import java.util.HashSet;

/**
 * Set injection point.
 */
public class SetInjectionPoint<T> {

	public static final SetInjectionPoint[] EMPTY = new SetInjectionPoint[0];

	public final PropertyDescriptor propertyDescriptor;

	public final Class<T> type;

	public final Class targetClass;

	public SetInjectionPoint(PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
		this.type = resolveSetType(propertyDescriptor);

		// resolve component type
		Class targetClass = null;

		MethodDescriptor writeMethodDescriptor = propertyDescriptor.getWriteMethodDescriptor();
		FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

		if (writeMethodDescriptor != null) {
			targetClass = writeMethodDescriptor.getSetterRawComponentType();
		}
		if (targetClass == null && fieldDescriptor != null) {
			targetClass = fieldDescriptor.getRawComponentType();
		}

		this.targetClass = targetClass;

		if (targetClass == null) {
			throw new PetiteException("Unknown Petite set component type " +
					type.getSimpleName() + '.' + propertyDescriptor.getName());
		}
	}

	@SuppressWarnings({"unchecked"})
	protected Class<T> resolveSetType(PropertyDescriptor propertyDescriptor) {
		Class<T> type = (Class<T>) propertyDescriptor.getType();

		if (ClassUtil.isTypeOf(type, Collection.class)) {
			return type;
		}
		throw new PetiteException("Unsupported Petite set type: " + type.getName());
	}

	/**
	 * Creates target set for injection. For now it creates <code>HashSet</code>,
	 * but custom implementation can change this setting.
	 */
	public Collection<T> createSet(int length) {
		return new HashSet<>(length);
	}
}
