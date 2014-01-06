// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.WebApplication;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;

/**
 * {@link jodd.madvoc.WebApplication WebApplication} that uses {@link jodd.petite.PetiteContainer Petite container}
 * for retrieving all instances.
 */
public class PetiteWebApplication extends WebApplication {

	/**
	 * Creates default Petite container that will be
	 * {@link #providePetiteContainer() provided} to Madvoc.
	 * This method does not have to be fired off if Petite
	 * container is created on some other place!
	 */
	protected PetiteContainer createPetiteContainer() {
		return new PetiteContainer();
	}

	/**
	 * Provides {@link PetiteContainer Petite container} instance that will be used as application context.
	 * By default it {@link #createPetiteContainer() creates new container instance} and performs
	 * {@link jodd.petite.config.AutomagicPetiteConfigurator auto-magic configuration}.
	 */
	protected PetiteContainer providePetiteContainer() {
		PetiteContainer pc = createPetiteContainer();

		AutomagicPetiteConfigurator configurator = new AutomagicPetiteConfigurator();

		configurator.configure(pc);

		return pc;
	}


	/**
	 * Registers {@link #providePetiteContainer() provided Petite container}
	 * and Petite-aware Madvoc components.
	 */
	@Override
	public void registerMadvocComponents() {
		super.registerMadvocComponents();

		PetiteContainer petiteContainer = providePetiteContainer();
		registerComponent("petiteContainer", petiteContainer);

		registerComponent(PetiteMadvocController.class);
		registerComponent(PetiteFilterManager.class);
		registerComponent(PetiteInterceptorManager.class);
		registerComponent(PetiteResultsManager.class);
	}

}