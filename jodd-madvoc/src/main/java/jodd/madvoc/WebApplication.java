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

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParamNameResolver;
import jodd.madvoc.component.ActionPathMacroManager;
import jodd.madvoc.component.ContextInjectorComponent;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InjectorsManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ActionPathRewriter;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.petite.PetiteContainer;

import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.Properties;

import jodd.props.Props;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * Web application contains all configurations and holds all managers and controllers of one web application.
 * Custom implementations may override this class to enhance several different functionality.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class WebApplication {

	private static Logger log;

	public static final String MADVOC_CONTAINER_NAME = "madpc";

	protected PetiteContainer madpc;

	/**
	 * Creates web application. Application is not initialized.
	 * @see #WebApplication(boolean)
	 */
	public WebApplication() {
	}

	/**
	 * Creates web application and optionally {@link #initWebApplication() initializes} it.
	 */
	public WebApplication(boolean init) {
		if (init) {
			initWebApplication();
		}
	}

	/**
	 * Initializes web application. Invoked very first.
	 * By default, it creates a Logger and creates internal Petite container.
	 * Also adds itself into it.
	 */
	protected void initWebApplication() {
		log = LoggerFactory.getLogger(WebApplication.class);
		log.debug("Initializing Madvoc web application");

		madpc = new PetiteContainer();
		madpc.addSelf(MADVOC_CONTAINER_NAME);
	}

	// ---------------------------------------------------------------- components

	/**
	 * Resolves the name of the last base non-abstract subclass for provided component.
	 * It iterates all subclasses up to the <code>Object</cde> and declares the last
	 * non-abstract class as base component. Component name will be resolved from the
	 * founded base component.
	 */
	private String resolveBaseComponentName(Class component) {
		Class lastComponent = component;
		while (true) {
			Class superClass = component.getSuperclass();
			if (superClass.equals(Object.class)) {
				break;
			}
			component = superClass;
			if (Modifier.isAbstract(component.getModifiers()) == false) {
				lastComponent = component;
			}
		}
		return madpc.resolveBeanName(lastComponent);
	}

	/**
	 * Registers component using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponent(Object) 
	 */
	public final void registerComponent(Class component) {
		String name = resolveBaseComponentName(component);
		registerComponent(name, component);
	}

	public final void registerComponent(String name, Class component) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component '" + name + "' of type " + component.getName());
		}
		madpc.removeBean(name);
		madpc.registerPetiteBean(component, name, null, null, false);
	}

	/**
	 * Registers component instance using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponent(Class)
	 */
	public final void registerComponent(Object componentInstance) {
		Class component = componentInstance.getClass();
		String name = resolveBaseComponentName(component);
		registerComponent(name, componentInstance);
	}

	/**
	 * Registers component instance and wires it with internal container.
	 */
	public final void registerComponent(String name, Object componentInstance) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component '" + name + "' instance of " + componentInstance.getClass().getName());
		}
		madpc.removeBean(name);
		madpc.addBean(name, componentInstance);
	}

	/**
	 * Returns registered component. Should be used only in special cases.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T getComponent(Class<T> component) {
		String name = resolveBaseComponentName(component);
		return (T) madpc.getBean(name);
	}

	/**
	 * Returns registered component.
	 */
	public Object getComponent(String componentName) {
		return madpc.getBean(componentName);
	}

	/**
	 * Registers default Madvoc components.
	 * Invoked before {@link #init(MadvocConfig , ServletContext) madvoc initialization}.
	 */
	public void registerMadvocComponents() {
		log.debug("Registering Madvoc components");

		registerComponent(ActionMethodParser.class);
		registerComponent(ActionMethodParamNameResolver.class);
		registerComponent(ActionPathRewriter.class);
		registerComponent(ActionPathMacroManager.class);
		registerComponent(ActionsManager.class);
		registerComponent(ContextInjectorComponent.class);
		registerComponent(InjectorsManager.class);
		registerComponent(InterceptorsManager.class);
		registerComponent(FiltersManager.class);
		registerComponent(MadvocConfig.class);
		registerComponent(MadvocController.class);
		registerComponent(ResultsManager.class);
		registerComponent(ResultMapper.class);
		registerComponent(ScopeDataResolver.class);
	}


	// ---------------------------------------------------------------- lifecycle

	/**
	 * Initialized web application parameters. Provided properties object is always non-<code>null</code>.
	 * Simple defines parameters for internal container.
	 */
	protected void defineParams(Properties properties) {
		log.debug("Defining Madvoc parameters");

		madpc.defineParameters(properties);
	}

	protected void defineParams(Props props) {
		log.debug("Defining Madvoc parameters");

		madpc.defineParameters(props);
	}

	/**
	 * Initializes web application custom configuration.
	 * When running web application out from container,
	 * <code>servletContext</code> may be <code>null</code>.
	 */
	protected void init(MadvocConfig madvocConfig, ServletContext servletContext) {
		log.debug("Initializing Madvoc");
	}

	/**
	 * Hook for filters.
	 */
	protected void initFilters(FiltersManager filtersManager) {
		log.debug("Initializing Madvoc filters");
	}

	/**
	 * Hook for interceptors.
	 */
	protected void initInterceptors(InterceptorsManager interceptorsManager) {
		log.debug("Initializing Madvoc interceptors");
	}


	/**
	 * Hook for actions manager. Allows manual registration of the actions.
	 */
	protected void initActions(ActionsManager actionManager) {
		log.debug("Initializing Madvoc actions");
	}

	/**
	 * Hook for manually registered results.
	 */
	protected void initResults(ResultsManager actionManager) {
		log.debug("Initializing Madvoc results");
	}

	/**
	 * Called when Madvoc is up and ready.
	 */
	protected void ready() {
		log.info("Madvoc is ready");
	}

	/**
	 * Invoked on web application destroy.
	 */
	protected void destroy(MadvocConfig madvocConfig) {
		log.debug("Destroying Madvoc");
	}

	// ---------------------------------------------------------------- configurator

	/**
	 * Adds configurator to Madvoc container and invokes configuration.
	 */
	public void configure(MadvocConfigurator configurator) {
		log.debug("Configuring Madvoc");

		registerComponent(configurator);

		configurator.configure();
	}
}
