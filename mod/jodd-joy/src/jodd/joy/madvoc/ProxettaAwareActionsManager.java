// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc;

import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.ActionConfig;
import jodd.proxetta.Proxetta;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

/**
 * Madvoc actions manager that works with Proxeta.
 */
public class ProxettaAwareActionsManager extends ActionsManager {

	protected final Proxetta proxetta;
	protected final Map<Class, Class> proxyActionClasses;

	public ProxettaAwareActionsManager() {
		this(null);
	}
	public ProxettaAwareActionsManager(Proxetta proxetta) {
		this.proxetta = proxetta;
		this.proxyActionClasses = new HashMap<Class, Class>();
	}

	/**
	 * Registers actions and applies proxeta on actions that are not already registered.
	 */
	@Override
	protected synchronized void registerAction(Class actionClass, Method actionMethod, String actionPath) {
		if (proxetta != null) {
			// create action path from existing class (if not already exist)
			if (actionPath == null) {
				ActionConfig cfg = actionMethodParser.parse(actionClass, actionMethod, actionPath);
				actionPath = cfg.actionPath;
			}
			// create proxy for action class if not already created
			Class existing = proxyActionClasses.get(actionClass);
			if (existing == null) {
				existing = proxetta.defineProxy(actionClass);
				proxyActionClasses.put(actionClass, existing);
			}
			actionClass = existing;
		}
		super.registerAction(actionClass, actionMethod, actionPath);
	}
}
