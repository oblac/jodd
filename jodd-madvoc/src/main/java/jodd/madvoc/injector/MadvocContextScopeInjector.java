// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.petite.PetiteContainer;

/**
 * Madvoc context injector. Injects beans from Madvocs internal container,
 * i.e. Madvocs components.
 */
public class MadvocContextScopeInjector extends BaseScopeInjector
		implements Injector, ContextInjector<PetiteContainer> {

	protected final PetiteContainer madpc;

	public MadvocContextScopeInjector(ScopeDataResolver scopeDataResolver, PetiteContainer madpc) {
		super(ScopeType.CONTEXT, scopeDataResolver);
		this.madpc = madpc;
	}

	public void injectContext(Object target, PetiteContainer madpc) {
		ScopeData.In[] injectData = resolveInData(target.getClass());
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

	public void inject(ActionRequest actionRequest) {
		Object[] targets = actionRequest.getTargets();

		ScopeData.In[][] injectData = lookupInData(actionRequest);
		if (injectData == null) {
			return;
		}

		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			ScopeData.In[] scopes = injectData[i];
			if (scopes == null) {
				continue;
			}

			for (ScopeData.In in : scopes) {
				Object value = madpc.getBean(in.name);
				if (value != null) {
					String property = in.target != null ? in.target : in.name;
					BeanUtil.setDeclaredProperty(target, property, value);
				}
			}

		}
	}

}