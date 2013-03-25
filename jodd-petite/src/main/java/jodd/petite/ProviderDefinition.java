// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Bean provider definition.
 */
public class ProviderDefinition {

	protected String beanName;
	protected Method method;

	public ProviderDefinition(String beanName, Method method) {
		this.beanName = beanName;
		this.method = method;
	}

	public ProviderDefinition(Method staticMethod) {
		if (!Modifier.isStatic(staticMethod.getModifiers())) {
			throw new PetiteException("Provider method is not static: " + staticMethod);
		}

		this.method = staticMethod;
	}

}