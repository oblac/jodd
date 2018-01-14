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
import jodd.madvoc.ScopeType;
import jodd.petite.PetiteContainer;

/**
 * Madvoc context injector. Injects beans from Madvocs internal container,
 * i.e. Madvocs components.
 */
public class MadvocContextScopeInjector implements Injector, ContextInjector<PetiteContainer> {
	private final static ScopeType SCOPE_TYPE = ScopeType.CONTEXT;
	protected final PetiteContainer madpc;

	public MadvocContextScopeInjector(final PetiteContainer madpc) {
		this.madpc = madpc;
	}

	@Override
	public void injectContext(final Targets targets, final PetiteContainer madpc) {
		targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
			Object value = madpc.getBean(in.name);
			if (value != null) {
				target.writeValue(in.propertyName(), value, false);
			}
		});
	}

	@Override
	public void inject(final ActionRequest actionRequest) {
		Targets targets = actionRequest.getTargets();

		targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
			Object value = madpc.getBean(in.name);
			if (value != null) {
				target.writeValue(in.propertyName(), value, false);
			}
		});
	}

}