// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.WebApplication;

/**
 * {@link jodd.madvoc.WebApplication} that uses {@link jodd.petite.PetiteContainer} for
 * retrieving all instances.
 */
public class PetiteWebApplication extends WebApplication {

	/**
	 * Registers {@link jodd.madvoc.petite.PetiteMadvocController}
	 */
	@Override
	public void registerMadvocComponents() {
		super.registerMadvocComponents();
		registerComponent(PetiteMadvocController.class);
		registerComponent(PetiteInterceptorManager.class);
		registerComponent(PetiteResultsManager.class);
		registerComponent(PetiteMadvocComponent.class);
	}

}
