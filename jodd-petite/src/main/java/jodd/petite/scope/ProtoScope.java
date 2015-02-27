// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanDefinition;

/**
 * Prototype scope doesn't pool any beans, so each time bean is requested,
 * a new instance will be created. Prototype scope does not call
 * destroy methods.
 */
public class ProtoScope implements Scope {

	/**
	 * Returns <code>null</code> as no bean instance is stored.
	 */
	public Object lookup(String name) {
		return null;
	}

	/**
	 * Does nothing, as bean instances are not stored.
	 */
	public void register(BeanDefinition beanDefinition, Object bean) {
	}

	/**
	 * Does nothing.
	 */
	public void remove(String name) {
	}

	/**
	 * Allows all scopes to be injected into prototype scoped beans.
	 */
	public boolean accept(Scope referenceScope) {
		return true;
	}

	/**
	 * Does nothing.
	 */
	public void shutdown() {
	}

}