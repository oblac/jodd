// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.petite.scope.Scope;
import jodd.petite.PetiteException;
import jodd.petite.WiringMode;
import jodd.petite.BeanDefinition;

import java.util.Map;
import java.util.HashMap;

/**
 * Manager for bean definitions and bean scopes.
 * Registeres beans and holds bean definitions.
 */
public class BeanManager {

	/**
	 * Map of all beans definitions.
	 */
	protected final Map<String, BeanDefinition> beans = new HashMap<String, BeanDefinition>();

	/**
	 * Map of all bean scopes.
	 */
	protected final Map<Class<? extends Scope>, Scope> scopes = new HashMap<Class<? extends Scope>, Scope>();

	/**
	 * Resolves scope from scope type.
	 */
	protected Scope resolveScope(Class<? extends Scope> scopeType) {
		Scope scope = scopes.get(scopeType);
		if (scope == null) {
			try {
				scope = scopeType.newInstance();
				scopes.put(scopeType, scope);
			} catch (Exception ex) {
				throw new PetiteException("Unable to create Petite scope: '" + scopeType, ex);
			}
		}
		return scope;
	}


	/**
	 * Returns new bean definition after the registration.
	 */
	public BeanDefinition register(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		// check if type is valid
		if ((type != null) && (type.isInterface() == true)) {
			throw new PetiteException("Unable to register interface '" + type.getName() + "'.");
		}
		// register
		Scope scope = resolveScope(scopeType);
		BeanDefinition beanDefinition = new BeanDefinition(name, type, scope, wiringMode);
		beans.put(name, beanDefinition);
		return beanDefinition;
	}


}
