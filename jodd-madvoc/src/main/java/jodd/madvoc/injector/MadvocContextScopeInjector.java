// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.petite.PetiteContainer;

/**
 * Madvoc context injector. Injects beans from Madvocs internal container,
 * i.e. Madvocs components.
 */
public class MadvocContextScopeInjector extends BaseScopeInjector
		implements Injector, ContextInjector<PetiteContainer> {

	protected final PetiteContainer madpc;

	public MadvocContextScopeInjector(MadvocConfig madvocConfig, ScopeDataResolver scopeDataResolver, PetiteContainer madpc) {
		super(ScopeType.CONTEXT, madvocConfig, scopeDataResolver);
		this.madpc = madpc;
	}

	public void injectContext(Target target, ScopeData[] scopeData, PetiteContainer madpc) {
		ScopeData.In[] injectData = lookupInData(scopeData);
		if (injectData == null) {
			return;
		}
		for (ScopeData.In in : injectData) {
			Object value = madpc.getBean(in.name);
			if (value != null) {
				String property = in.target != null ? in.target : in.name;
				setTargetProperty(target, property, value);
			}
		}
	}

	public void inject(ActionRequest actionRequest) {
		ScopeData.In[][] injectData = lookupInData(actionRequest);
		if (injectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			ScopeData.In[] scopes = injectData[i];
			if (scopes == null) {
				continue;
			}

			for (ScopeData.In in : scopes) {
				Object value = madpc.getBean(in.name);
				if (value != null) {
					String property = in.target != null ? in.target : in.name;
					setTargetProperty(target, property, value);
				}
			}

		}
	}

}