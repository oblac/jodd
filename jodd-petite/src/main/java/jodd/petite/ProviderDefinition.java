// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Bean provider definition.
 */
public class ProviderDefinition {

	public static final ProviderDefinition[] EMPTY = new ProviderDefinition[0];

	protected String name;
	protected String beanName;
	protected Method method;

	public ProviderDefinition(String name, String beanName, Method method) {
		this.name = name;
		this.beanName = beanName;
		this.method = method;
	}

	public ProviderDefinition(String name, Method staticMethod) {
		this.name = name;
		if (!Modifier.isStatic(staticMethod.getModifiers())) {
			throw new PetiteException("Provider method is not static: " + staticMethod);
		}

		this.method = staticMethod;
	}

}