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
import jodd.petite.def.ProviderDefinition;
import jodd.petite.meta.PetiteProvider;
import jodd.util.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Provider resolver.
 */
public class ProviderResolver {

	/**
	 * Resolves all providers in the class
	 */
	public ProviderDefinition[] resolve(final Class type, final String name) {
		ClassDescriptor cd = ClassIntrospector.get().lookup(type);
		MethodDescriptor[] methods = cd.getAllMethodDescriptors();

		List<ProviderDefinition> list = new ArrayList<>();

		for (MethodDescriptor methodDescriptor : methods) {
			Method method = methodDescriptor.getMethod();

			PetiteProvider petiteProvider = method.getAnnotation(PetiteProvider.class);
			if (petiteProvider == null) {
				continue;
			}

			String providerName = petiteProvider.value();

			if (StringUtil.isBlank(providerName)) {
				// default provider name
				providerName = method.getName();

				if (providerName.endsWith("Provider")) {
					providerName = StringUtil.substring(providerName, 0, -8);
				}
			}

			ProviderDefinition providerDefinition;

			if (Modifier.isStatic(method.getModifiers())) {
				providerDefinition = new ProviderDefinition(providerName, method);
			} else {
				providerDefinition = new ProviderDefinition(providerName, name, method);
			}

			list.add(providerDefinition);
		}

		ProviderDefinition[] providers;

		if (list.isEmpty()) {
			providers = ProviderDefinition.EMPTY;
		} else {
			providers = list.toArray(new ProviderDefinition[0]);
		}

		return providers;
	}

}