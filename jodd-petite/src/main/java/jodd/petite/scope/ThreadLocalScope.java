// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.util.ArraysUtil;

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

	/**
	 * Defines allowed referenced scopes that can be injected into the
	 * thread-local scoped bean.
	 */
	public boolean accept(Scope referenceScope) {
		Class<? extends Scope> refScopeType = referenceScope.getClass();

		for (int i = 0; i < acceptedScopes.length; i++) {
			if (refScopeType == acceptedScopes[i]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Registers scope that will be {@link #accept(Scope) accepted}.
	 */
	protected void acceptScope(Class<? extends Scope> scope) {
		acceptedScopes = ArraysUtil.append(acceptedScopes, scope);
	}

	// array of accepted scopes that can be injected here
	protected Class[] acceptedScopes = new Class[] {
			ThreadLocalScope.class,
			SingletonScope.class,
			//SessionScope.class,
	};

}