// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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
