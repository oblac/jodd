// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionPathMapper;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ScopeDataManager;
import jodd.madvoc.component.ContextInjector;
import jodd.madvoc.component.ActionPathRewriter;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.petite.PetiteContainer;
import jodd.petite.PetiteUtil;

import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;

/**
 * Web application contains all configurations and holds all managers and controllers of one web application.
 * Custom implementations may override this class to enhance several different functionality.
 */
public class WebApplication {

	public static final String MADVOC_CONTAINER_NAME = "madpc";
	private final PetiteContainer madpc;

	public WebApplication() {
		madpc = createInternalContainer();
	}

	/**
	 * Creates Petite container used internally by Madvoc.
	 */
	protected PetiteContainer createInternalContainer() {
		PetiteContainer madpc = new PetiteContainer();
		madpc.addSelf(MADVOC_CONTAINER_NAME);
		return madpc;
	}

	// ---------------------------------------------------------------- components

	/**
	 * Resolves the name of the last base non-abstract subclass for provided component.
	 */
	private String resolveBaseComponentName(Class component) {
		while(true) {
			Class superClass = component.getSuperclass();
			if (Modifier.isAbstract(superClass.getModifiers())) {
				break;
			}
			if (superClass.equals(Object.class)) {
				break;
			}
			component = superClass;
		}
		return PetiteUtil.resolveBeanName(component);
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
		madpc.removeBean(name);
		madpc.registerBean(name, component);
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
		registerComponent(ActionMethodParser.class);
		registerComponent(ActionPathMapper.class);
		registerComponent(ActionPathRewriter.class);
		registerComponent(ActionsManager.class);

		registerComponent(ApplicationScopeInjector.class);
		registerComponent(MadvocContextScopeInjector.class);
		registerComponent(ContextInjector.class);

		registerComponent(InterceptorsManager.class);
		registerComponent(MadvocConfig.class);
		registerComponent(MadvocController.class);
		registerComponent(ResultsManager.class);
		registerComponent(ResultMapper.class);
		registerComponent(ScopeDataManager.class);
	}


	// ---------------------------------------------------------------- lifecycle

	/**
	 * Initializes web application custom configuration.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void init(MadvocConfig madvocConfig, ServletContext servletContext) {
	}

	/**
	 * Hook for manually registered actions.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void initActions(ActionsManager actionManager) {
	}

	/**
	 * Hook for manually registered results.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void initResults(ResultsManager actionManager) {
	}

	/**
	 * Invoked on web application destroy.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void destroy(MadvocConfig madvocConfig) {
	}


	// ---------------------------------------------------------------- configurator

	/**
	 * Wires configurator in the the madvoc container and invokes configuration.
	 */
	public void configure(MadvocConfigurator configurator) {
		madpc.wire(configurator);
		configurator.configure();
	}
}
