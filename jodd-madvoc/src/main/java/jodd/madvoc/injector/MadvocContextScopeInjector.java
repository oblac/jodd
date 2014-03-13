// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ScopeType;
import jodd.petite.PetiteContainer;

/**
 * Madvoc context injector. Injects beans from Madvocs internal container,
 * i.e. Madvocs components.
 */
public class MadvocContextScopeInjector extends BaseScopeInjector
		implements ContextInjector<PetiteContainer> {

	public MadvocContextScopeInjector() {
		super(ScopeType.CONTEXT);
	}

	public void injectContext(Object target, PetiteContainer madpc) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}
		for (ScopeData.In in : injectData) {
			Object value = madpc.getBean(in.name);
			if (value != null) {
				String property = in.target != null ? in.target : in.name;
				BeanUtil.setDeclaredProperty(target, property, value);
			}
		}
	}

}