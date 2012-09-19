// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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

}
