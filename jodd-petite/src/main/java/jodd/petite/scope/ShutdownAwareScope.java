// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanData;
import jodd.petite.DestroyMethodPoint;
import jodd.petite.PetiteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base {@link Scope} class for scopes that collect
 * destroyable beans and implement shutdown routine.
 */
public abstract class ShutdownAwareScope implements Scope {

	protected List<BeanData> destroyableBeans;

	/**
	 * Returns <code>true</code> if bean is destroyable.
	 */
	protected boolean isBeanDestroyable(BeanData beanData) {
		DestroyMethodPoint[] dmp = beanData.getBeanDefinition().getDestroyMethodPoints();
		return dmp != null && dmp.length != 0;
	}

	/**
	 * Checks if bean data is destroyable (has destroy methods) and
	 * registers it for later {@link #shutdown()}.
	 */
	protected void registerDestroyableBeans(BeanData beanData) {
		if (isBeanDestroyable(beanData) == false) {
			return;
		}
		if (destroyableBeans == null) {
			destroyableBeans = new ArrayList<BeanData>();
		}
		destroyableBeans.add(beanData);
	}

	/**
	 * Returns number of destroyable beans that have been registered.
	 */
	protected int totalRegisteredDestroyableBeans() {
		if (destroyableBeans == null) {
			return 0;
		}
		return destroyableBeans.size();
	}

	/**
	 * Removes destroyable bean from the list and calls it destroy methods.
	 * If bean is not destroyable, does nothing. Bean gets destroyed only once.
	 */
	protected void destroyBean(BeanData beanData) {
		if (destroyableBeans == null) {
			return;
		}
		if (isBeanDestroyable(beanData) == false) {
			return;
		}
		if (destroyableBeans.remove(beanData)) {
			PetiteUtil.callDestroyMethods(beanData);
		}
	}

	/**
	 * Shutdowns the scope and calls all collected destroyable beans.
	 */
	public void shutdown() {
		if (destroyableBeans == null) {
			return;
		}

		for (BeanData destroyableBean : destroyableBeans) {
			PetiteUtil.callDestroyMethods(destroyableBean);
		}

		destroyableBeans.clear();
	}

}