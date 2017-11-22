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

package jodd.joy.core;

import jodd.madvoc.WebApp;
import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.petite.PetiteWebApp;
import jodd.madvoc.proxetta.ProxettaAwareActionsManager;
import jodd.madvoc.proxetta.ProxettaProvider;
import jodd.petite.PetiteContainer;

/**
 * Default web application.
 */
public abstract class DefaultWebApp extends PetiteWebApp {

	protected final DefaultAppCore defaultAppCore;

	protected DefaultWebApp() {
		defaultAppCore = createAppCore();
	}

	/**
	 * Creates {@link DefaultAppCore}.
	 */
	protected abstract DefaultAppCore createAppCore();


	/**
	 * Starts {@link DefaultAppCore application core} before web application is initialized.
	 */
	@Override
	public WebApp start() {
		defaultAppCore.start();
		return super.start();
	}

	/**
	 * Registers default and additional {@link ProxettaAwareActionsManager}.
	 */
	@Override
	protected final void registerMadvocComponents() {
		super.registerMadvocComponents();
		madvocContainer.registerComponentInstance((ProxettaProvider) defaultAppCore::getProxetta);
	}

	/**
	 * Defines application container for Madvoc usage. We share applications
	 * Petite container from the appCore, so Madvoc can use it when creating
	 * Madvoc actions. By sharing the application container with the Madvoc,
	 * Petite beans can be injected in the actions.
	 * <p>
	 * If container is not shared, PetiteWebApplication would create
	 * new Petite container; that is fine when e.g. there are no layers.
	 */
	@Override
	protected PetiteContainer createAndInitializePetiteContainer() {
		return defaultAppCore.getPetite();
	}

	/**
	 * Configures <code>AutomagicMadvocConfigurator</code>.
	 * todo remove this by adding special class for configuration that takes the appCore and its AppScanner.
	 */
	@Deprecated
	public void configure(Object configurator) {
		if (configurator instanceof AutomagicMadvocConfigurator) {
			AutomagicMadvocConfigurator madvocConfigurator = (AutomagicMadvocConfigurator) configurator;

			defaultAppCore.getAppScanner().configure(madvocConfigurator);
		}
	}

	/**
	 * Destroys application context and Madvoc.
	 */
	@Override
	public void shutdown() {
		defaultAppCore.stop();
		super.shutdown();
	}

}
