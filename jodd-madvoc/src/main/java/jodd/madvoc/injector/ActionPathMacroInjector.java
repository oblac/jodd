// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.macro.PathMacro;

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

		String[] actionPathChunks = actionRequest.getActionPathChunks();

		for (int i = 0; i < set.actionPathMacros.length; i++) {
			PathMacro pathMacro = set.actionPathMacros[i];
			if (pathMacro == null) {
				continue;
			}

			String[] names = pathMacro.getNames();
			String[] values = pathMacro.extract(actionPathChunks[i]);

			for (int j = 0; j < names.length; j++) {
				String name = names[j];
				String value = values[j];

				BeanUtil.setDeclaredPropertyForcedSilent(target, name, value);
			}
		}
	}
}
