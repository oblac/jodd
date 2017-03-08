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

package jodd.madvoc.proxetta;

import jodd.madvoc.ActionDef;
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
		this.proxyActionClasses = new HashMap<>();
	}

	/**
	 * Registers actions and applies proxetta on actions that are not already registered.
	 * We need to define {@link jodd.madvoc.ActionDef} before we apply the proxy, using
	 * target action class.
	 */
	@Override
	protected synchronized ActionConfig registerAction(Class actionClass, Method actionMethod, ActionDef actionDef) {

		if (proxetta != null) {
			if (actionDef == null) {
				actionDef = actionMethodParser.parseActionDef(actionClass, actionMethod);
			}

			// create proxy for action class if not already created

			Class existing = proxyActionClasses.get(actionClass);

			if (existing == null) {
				existing = proxetta.builder(actionClass).define();

				proxyActionClasses.put(actionClass, existing);
			}

			actionClass = existing;
		}

		return super.registerAction(actionClass, actionMethod, actionDef);
	}
}