// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.MadvocException;
import jodd.madvoc.ScopeType;
import jodd.petite.PetiteContainer;

/**
 * Madvoc context injector. Injects beans from Madvocs internal container,
 * i.e. Madvocs components.
 */
public class MadvocContextScopeInjector extends BaseScopeInjector {

	protected final PetiteContainer madpc;

	public MadvocContextScopeInjector(PetiteContainer madpc) {
		super(ScopeType.CONTEXT);
		this.madpc = madpc;
	}

	public void inject(Object target) {
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


	public void outject(Object target) {
		ScopeData.Out[] outjectData = lookupOutData(target.getClass());
		if (outjectData == null) {
			return;
		}
		throw new MadvocException("Madvoc context can't be outjected.");
	}
}