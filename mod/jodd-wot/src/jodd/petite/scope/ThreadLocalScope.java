// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread local Petite bean scope. Holds beans in thread local scopes.
 */
public class ThreadLocalScope implements Scope {

	protected static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected synchronized Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	public Object lookup(String name) {
		Map<String, Object> threadLocalMap = context.get();
		return threadLocalMap.get(name);
	}

	public void register(String name, Object bean) {
		Map<String, Object> threadLocalMap = context.get();
		threadLocalMap.put(name, bean);
	}

	public void remove(String name) {
		Map<String, Object> threadLocalMap = context.get();
		threadLocalMap.remove(name);
	}
}
