// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;

/**
 * Madvoc component that holds petite instance. 
 */
public class PetiteMadvocComponent {

	protected final PetiteContainer pc;

	public PetiteMadvocComponent() {
		pc = createPetiteContainer();
	}

	/**
	 * Creates new {@link PetiteContainer Petite container} instance and performs
	 * {@link jodd.petite.config.AutomagicPetiteConfigurator auto-magic configuration}.
	 */
	protected PetiteContainer createPetiteContainer() {
		PetiteContainer pc = new PetiteContainer();
		AutomagicPetiteConfigurator configurator = new AutomagicPetiteConfigurator();
		configurator.configure(pc);
		return pc;
	}

	/**
	 * Returns Petite container instance.
	 */
	public PetiteContainer getPetiteContainer() {
		return pc;
	}
}
