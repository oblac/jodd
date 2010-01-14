// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import java.util.Map;
import java.util.HashMap;

/**
 * Singleton scope pools all bean instances so they will be created only once in
 * the container context.
 */
public class SingletonScope implements Scope {

	protected Map<String, Object> instances = new HashMap<String, Object>();

	public Object lookup(String name) {
		return instances.get(name);
	}

	public void register(String name, Object bean) {
		instances.put(name, bean);
	}

	public void remove(String name) {
		instances.remove(name);
	}

}
