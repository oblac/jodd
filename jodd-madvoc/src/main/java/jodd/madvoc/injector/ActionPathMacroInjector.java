// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.ActionRequest;
import jodd.util.StringUtil;

/**
 * Injects macro values from action path into the action bean.
 */
public class ActionPathMacroInjector {

	public void inject(Object target, ActionRequest actionRequest) {
		ActionConfig config = actionRequest.getActionConfig();
		ActionConfigSet set = config.getActionConfigSet();

		if (set.actionPathMacros == null) {
			return;
		}

		String[] names = set.actionPathMacros.getNames();
		String[] values = set.actionPathMacros.extract(actionRequest.getActionPath());

		for (int i = 0; i < values.length; i++) {
			String value = values[i];

			if (StringUtil.isEmpty(value)) {
				continue;
			}

			String name = names[i];

			BeanUtil.setDeclaredPropertyForcedSilent(target, name, value);
		}
	}
}
