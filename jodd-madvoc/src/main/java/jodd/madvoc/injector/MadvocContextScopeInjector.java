// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc.injector;

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
		ScopeData[] injectData = lookupScopeData(actionRequest);
		if (injectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			if (injectData[i] == null) {
				continue;
			}
			ScopeData.In[] scopes = injectData[i].in;

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