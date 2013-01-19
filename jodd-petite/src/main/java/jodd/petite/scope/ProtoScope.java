// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

/**
 * Prototype scope doesn't pool any beans, so each time bean is requested,
 * a new instance will be created.
 */
public class ProtoScope implements Scope {

	public Object lookup(String name) {
		return null;
	}

	public void register(String name, Object bean) {
	}

	public void remove(String name) {
	}

	/**
	 * Allow all scopes to be injected into prototype scoped beans.
	 */
	public boolean accept(Scope referenceScope) {
		return true;
	}
}
