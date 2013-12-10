// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.config.AutomagicMadvocConfigurator;

/**
 * Configurator that loads only actions for integration tests.
 */
public class MadvocTestIntegrationConfig extends AutomagicMadvocConfigurator {

	@Override
	protected void onActionClass(String className) throws ClassNotFoundException {
		if (className.startsWith("jodd.madvoc.action.") == false) {
			return;
		}
		super.onActionClass(className);
	}
}
