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