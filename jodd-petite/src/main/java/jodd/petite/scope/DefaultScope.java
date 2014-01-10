// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanDefinition;
import jodd.petite.PetiteException;

/**
 * Default Petite container scope. It serves only as a <b>marker</b> for
 * marking the default container scope.
 */
public final class DefaultScope implements Scope {

	private DefaultScope() {
		throw new PetiteException("DefaultScope is marker scope and can not be used differently.");
	}

	public Object lookup(String name) {
		return null;
	}

	public void register(BeanDefinition beanDefinition, Object bean) {
	}

	public void remove(String name) {
	}

	public boolean accept(Scope referenceScope) {
		return false;
	}

	public void shutdown() {
	}
}
