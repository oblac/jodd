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

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.util.StringUtil;

/**
 * Injects macro values from action path into the action bean.
 * Path macros are considered to be in {@link jodd.madvoc.ScopeType#REQUEST request scope}.
 */
public class ActionPathMacroInjector extends BaseScopeInjector implements Injector {

	public ActionPathMacroInjector(ScopeDataResolver scopeDataResolver) {
		super(ScopeType.REQUEST, scopeDataResolver);
		silent = true;
	}

	public void inject(ActionRequest actionRequest) {
		ActionConfig config = actionRequest.getActionConfig();
		ActionConfigSet set = config.getActionConfigSet();

		if (set.actionPathMacros == null) {
			// no action path macros at all, just exit
			return;
		}

		ScopeData[] injectData = lookupScopeData(actionRequest);
		if (injectData == null) {
			return;
		}

		// inject

		Target[] targets = actionRequest.getTargets();

		String[] names = set.actionPathMacros.getNames();
		String[] values = set.actionPathMacros.extract(actionRequest.getActionPath());

		for (int ndx = 0; ndx < values.length; ndx++) {
			String value = values[ndx];

			if (StringUtil.isEmpty(value)) {
				continue;
			}

			String macroName = names[ndx];

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
					String name = getMatchedPropertyName(in, macroName);

					if (name != null) {
						setTargetProperty(target, name, value);
					}
				}
			}
		}
	}
}
