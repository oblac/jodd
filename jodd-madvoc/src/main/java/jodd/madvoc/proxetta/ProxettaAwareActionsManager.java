// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.proxetta;

import jodd.madvoc.ActionId;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.ActionConfig;
import jodd.proxetta.impl.ProxyProxetta;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

/**
 * Madvoc {@link jodd.madvoc.component.ActionsManager actions manager} that works with Proxetta.
 */
public class ProxettaAwareActionsManager extends ActionsManager {

	protected final ProxyProxetta proxetta;
	protected final Map<Class, Class> proxyActionClasses;

	public ProxettaAwareActionsManager(ProxyProxetta proxetta) {
		this.proxetta = proxetta;
		this.proxyActionClasses = new HashMap<Class, Class>();
	}

	/**
	 * Registers actions and applies proxetta on actions that are not already registered.
	 * We need to define {@link jodd.madvoc.ActionId} before we apply the proxy, using
	 * target action class.
	 */
	@Override
	protected synchronized ActionConfig registerAction(Class actionClass, Method actionMethod, ActionId actionId) {

		if (proxetta != null) {
			if (actionId == null) {
				actionId = actionMethodParser.parseActionId(actionClass, actionMethod);
			}

			// create proxy for action class if not already created

			Class existing = proxyActionClasses.get(actionClass);

			if (existing == null) {
				existing = proxetta.builder(actionClass).define();

				proxyActionClasses.put(actionClass, existing);
			}

			actionClass = existing;
		}

		return super.registerAction(actionClass, actionMethod, actionId);
	}
}