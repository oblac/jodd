// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.WebApplication;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;

/**
 * {@link jodd.madvoc.WebApplication WebApplication} that uses {@link jodd.petite.PetiteContainer Petite container}
 * for retrieving all instances.
 */
public class PetiteWebApplication extends WebApplication {

	protected final PetiteContainer pc;

	public PetiteWebApplication() {
		pc = providePetiteContainer();
	}

	/**
	 * Provides {@link PetiteContainer Petite container} instance that will be used as application context.
	 * By default it creates new instance and performs
	 * {@link jodd.petite.config.AutomagicPetiteConfigurator auto-magic configuration}.
	 */
	protected PetiteContainer providePetiteContainer() {
		PetiteContainer pc = new PetiteContainer();
		AutomagicPetiteConfigurator configurator = new AutomagicPetiteConfigurator();
		configurator.configure(pc);
		return pc;
	}


	/**
	 * Registers {@link jodd.madvoc.petite.PetiteMadvocController}
	 */
	@Override
	public void registerMadvocComponents() {
		super.registerMadvocComponents();
		registerComponent("petiteContainer", pc);
		registerComponent(PetiteMadvocController.class);
		registerComponent(PetiteInterceptorManager.class);
		registerComponent(PetiteResultsManager.class);
	}

}
