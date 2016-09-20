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
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resolves properties.
 */
public class PropertyResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public PropertyResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all properties for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type, boolean autowire) {
		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<PropertyInjectionPoint> list = new ArrayList<>();
		PropertyDescriptor[] allPropertyDescriptors = cd.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : allPropertyDescriptors) {

			if (propertyDescriptor.isGetterOnly()) {
				continue;
			}

			Class propertyType = propertyDescriptor.getType();
			if (ReflectUtil.isTypeOf(propertyType, Collection.class)) {
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

			String[] refName = null;

			if (ref != null) {
				String name = ref.value().trim();
				if (name.length() != 0) {
					refName = new String[] {name};
				}
			}

			list.add(injectionPointFactory.createPropertyInjectionPoint(propertyDescriptor, refName));
		}

		PropertyInjectionPoint[] fields;

		if (list.isEmpty()) {
			fields = PropertyInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new PropertyInjectionPoint[list.size()]);
		}

		return fields;
	}

}