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

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.SetInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resolves collection fields.
 */
public class SetResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public SetResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all collections for given type.
	 */
	public SetInjectionPoint[] resolve(Class type, boolean autowire) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<SetInjectionPoint> list = new ArrayList<>();

		PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : allProperties) {

			if (propertyDescriptor.isGetterOnly()) {
				continue;
			}

			Class propertyType = propertyDescriptor.getType();
			if (!ClassUtil.isTypeOf(propertyType, Collection.class)) {
				continue;
			}

			MethodDescriptor writeMethodDescriptor = propertyDescriptor.getWriteMethodDescriptor();
			FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

			PetiteInject ref = null;

			if (writeMethodDescriptor != null) {
				ref = writeMethodDescriptor.getMethod().getAnnotation(PetiteInject.class);
			}
			if (ref == null && fieldDescriptor != null) {
				ref = fieldDescriptor.getField().getAnnotation(PetiteInject.class);
			}

			if ((!autowire) && (ref == null)) {
				continue;
			}

			list.add(injectionPointFactory.createSetInjectionPoint(propertyDescriptor));
		}

		SetInjectionPoint[] fields;

		if (list.isEmpty()) {
			fields = SetInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new SetInjectionPoint[list.size()]);
		}
		return fields;
	}

}