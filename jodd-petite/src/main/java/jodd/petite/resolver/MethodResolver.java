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
import jodd.introspector.MethodDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.MethodInjectionPoint;
import jodd.petite.PetiteUtil;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Method reference resolver.
 */
public class MethodResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public MethodResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	public MethodInjectionPoint[] resolve(Class type) {
		// lookup methods
		ClassDescriptor cd = ClassIntrospector.get().lookup(type);
		List<MethodInjectionPoint> list = new ArrayList<>();
		MethodDescriptor[] allMethods = cd.getAllMethodDescriptors();

		for (MethodDescriptor methodDescriptor : allMethods) {
			Method method = methodDescriptor.getMethod();

			if (ClassUtil.isBeanPropertySetter(method)) {
				// ignore setters
				continue;
			}

			if (method.getParameterTypes().length == 0) {
				// ignore methods with no argument
				continue;
			}

			PetiteInject ref = method.getAnnotation(PetiteInject.class);
			if (ref == null) {
				continue;
			}

			String[][] references = PetiteUtil.convertAnnValueToReferences(ref.value());
			list.add(injectionPointFactory.createMethodInjectionPoint(method, references));
		}

		MethodInjectionPoint[] methods;

		if (list.isEmpty()) {
			methods = MethodInjectionPoint.EMPTY;
		} else {
			methods = list.toArray(new MethodInjectionPoint[list.size()]);
		}
		return methods;
	}

}