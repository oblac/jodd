// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import jodd.joy.madvoc.ProxettaAwareActionsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.petite.PetiteWebApplication;
import jodd.petite.PetiteContainer;

/**
 * Default web application.
 */
public abstract class DefaultWebApplication extends PetiteWebApplication {

	protected final DefaultAppCore defaultAppCore;

	protected DefaultWebApplication() {
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
	protected void initWebApplication() {
		defaultAppCore.start();
		super.initWebApplication();
	}

	/**
	 * Registers default and additional {@link ProxettaAwareActionsManager}.
	 * Because the custom action manager is registered using an instance,
	 * all custom Madvoc components should be registered before it!
	 * For that reason, custom madvoc components should be registered
	 * using {@link #registerCustomMadvocComponents()}.
	 */
	@Override
	public final void registerMadvocComponents() {
		super.registerMadvocComponents();

		registerCustomMadvocComponents();

		registerComponent(new ProxettaAwareActionsManager(defaultAppCore.getProxetta()));
	}

	/**
	 * Registers custom madvoc components.
	 * @see #registerMadvocComponents()
	 */
	protected void registerCustomMadvocComponents() {
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
	protected PetiteContainer providePetiteContainer() {
		return defaultAppCore.getPetite();
	}

	/**
	 * Configures <code>AutomagicMadvocConfigurator</code>.
	 */
	@Override
	public void configure(MadvocConfigurator configurator) {
		if (configurator instanceof AutomagicMadvocConfigurator) {
			AutomagicMadvocConfigurator madvocConfigurator = (AutomagicMadvocConfigurator) configurator;
			defaultAppCore.getAppScanner().configure(madvocConfigurator);
		}
		super.configure(configurator);
	}

	/**
	 * Destroys application context and Madvoc.
	 */
	@Override
	protected void destroy(MadvocConfig madvocConfig) {
		defaultAppCore.stop();
		super.destroy(madvocConfig);
	}

}
