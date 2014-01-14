// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanData;
import jodd.petite.BeanDefinition;
import jodd.petite.PetiteUtil;

import java.util.Map;
import java.util.HashMap;

/**
 * Singleton scope pools all bean instances so they will be created only once in
 * the container context.
 */
public class SingletonScope implements Scope {

	protected Map<String, BeanData> instances = new HashMap<String, BeanData>();

	public Object lookup(String name) {
		BeanData beanData = instances.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.getBean();
	}

	public void register(BeanDefinition beanDefinition, Object bean) {
		BeanData beanData = new BeanData(beanDefinition, bean);
		instances.put(beanDefinition.getName(), beanData);
	}

	public void remove(String name) {
		instances.remove(name);
	}

	/**
	 * Allows only singleton scoped beans to be injected into the target singleton bean.
	 */
	public boolean accept(Scope referenceScope) {
		return (referenceScope.getClass() == SingletonScope.class);
	}

	/**
	 * Iterate all beans and invokes registered destroy methods.
	 */
	public void shutdown() {
		for (BeanData beanData : instances.values()) {
			PetiteUtil.callDestroyMethods(beanData);
		}
		instances.clear();
	}
}
