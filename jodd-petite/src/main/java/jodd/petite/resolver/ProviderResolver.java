// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.BeanDefinition;
import jodd.petite.ProviderDefinition;
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
	public ProviderDefinition[] resolve(BeanDefinition beanDefinition) {
		Class type = beanDefinition.getType();

		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Method[] methods = cd.getAllMethods(true);

		List<ProviderDefinition> list = new ArrayList<ProviderDefinition>();

		for (Method method : methods) {
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
				providerDefinition = new ProviderDefinition(providerName, beanDefinition.getName(), method);
			}

			list.add(providerDefinition);
		}

		ProviderDefinition[] providers;

		if (list.isEmpty()) {
			providers = ProviderDefinition.EMPTY;
		} else {
			providers = list.toArray(new ProviderDefinition[list.size()]);
		}

		return providers;
	}

}