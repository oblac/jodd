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
import jodd.introspector.PropertyDescriptor;
import jodd.petite.def.BeanReferences;
import jodd.petite.def.PropertyInjectionPoint;
import jodd.util.ClassUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolves properties.
 */
public class PropertyResolver {

	protected final ReferencesResolver referencesResolver;

	public PropertyResolver(final ReferencesResolver referencesResolver) {
		this.referencesResolver = referencesResolver;
	}

	/**
	 * Resolves all properties for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type, final boolean autowire) {
		final List<PropertyInjectionPoint> list = new ArrayList<>();
		final Set<String> usedPropertyNames = new HashSet<>();

		// lookup fields
		while (type != Object.class) {

			final ClassDescriptor cd = ClassIntrospector.get().lookup(type);
			final PropertyDescriptor[] allPropertyDescriptors = cd.getAllPropertyDescriptors();

			for (PropertyDescriptor propertyDescriptor : allPropertyDescriptors) {

				if (propertyDescriptor.isGetterOnly()) {
					continue;
				}

				if (usedPropertyNames.contains(propertyDescriptor.getName())) {
					continue;
				}

				Class propertyType = propertyDescriptor.getType();
				if (ClassUtil.isTypeOf(propertyType, Collection.class)) {
					continue;
				}

				BeanReferences reference = referencesResolver.readReferenceFromAnnotation(propertyDescriptor);

				if (reference == null) {
					if (!autowire) {
						continue;
					} else {
						reference = referencesResolver.buildDefaultReference(propertyDescriptor);
					}
				}

				list.add(new PropertyInjectionPoint(propertyDescriptor, reference));

				usedPropertyNames.add(propertyDescriptor.getName());
			}

			// go to the supertype
			type = type.getSuperclass();
		}

		final PropertyInjectionPoint[] fields;

		if (list.isEmpty()) {
			fields = PropertyInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new PropertyInjectionPoint[0]);
		}

		return fields;
	}

}