// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.ActionRequest;

/**
 * Injects macro values from action path.
 */
public class ActionPathMacroInjector {

	public void inject(Object target, ActionRequest actionRequest) {
		ActionConfig config = actionRequest.getActionConfig();
		ActionConfigSet set = config.getActionConfigSet();
		if (set.actionPathMacros== null) {
			return;
		}

		String[] actionPathChunks = actionRequest.getActionPathChunks();
		for (int i = 0; i < set.actionPathMacros.length; i++) {
			ActionConfigSet.PathMacro macro = set.actionPathMacros[i];
			int ndx = macro.ndx;
			String name = macro.name;
			String value = actionPathChunks[ndx];

			int leftLen = macro.left.length();
			int rightLen = macro.right.length();

			if (leftLen + rightLen > 0) {
				// there is additional prefix and/or suffix
				value = value.substring(leftLen, value.length() - rightLen);
			}
			BeanUtil.setDeclaredPropertyForcedSilent(target, name, value);
		}
	}
}
