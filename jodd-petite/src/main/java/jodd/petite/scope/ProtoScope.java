// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanDefinition;

/**
 * Prototype scope doesn't pool any beans, so each time bean is requested,
 * a new instance will be created. Prototype scope does not call
 * destroy methods.
 */
public class ProtoScope implements Scope {

	public Object lookup(String name) {
		return null;
	}

	public void register(BeanDefinition beanDefinition, Object bean) {
	}

	public void remove(String name) {
	}

	/**
	 * Allows all scopes to be injected into prototype scoped beans.
	 */
	public boolean accept(Scope referenceScope) {
		return true;
	}

	public void shutdown() {
	}
}
