// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanData;
import jodd.petite.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread local Petite bean scope. Holds beans in thread local scopes.
 * Be careful with this scope, if you do not have control on threads!
 * For example, app servers may have a thread pools, so threads may not
 * finish when expected. ThreadLocalScope can not invoke destroy methods.
 */
public class ThreadLocalScope implements Scope {

	protected static ThreadLocal<Map<String, BeanData>> context = new ThreadLocal<Map<String, BeanData>>() {
		@Override
		protected synchronized Map<String, BeanData> initialValue() {
			return new HashMap<String, BeanData>();
		}
	};

	public Object lookup(String name) {
		Map<String, BeanData> threadLocalMap = context.get();
		BeanData beanData = threadLocalMap.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.getBean();
	}

	public void register(BeanDefinition beanDefinition, Object bean) {
		BeanData beanData = new BeanData(beanDefinition, bean);
		Map<String, BeanData> threadLocalMap = context.get();
		threadLocalMap.put(beanDefinition.getName(), beanData);
	}

	public void remove(String name) {
		Map<String, BeanData> threadLocalMap = context.get();
		threadLocalMap.remove(name);
	}

	/**
	 * Defines allowed referenced scopes that can be injected into the
	 * thread-local scoped bean.
	 */
	public boolean accept(Scope referenceScope) {
		Class<? extends Scope> refScopeType = referenceScope.getClass();

		if (refScopeType == SingletonScope.class) {
			return true;
		}

		if (refScopeType == ThreadLocalScope.class) {
			return true;
		}

		return false;
	}

	public void shutdown() {
	}

}