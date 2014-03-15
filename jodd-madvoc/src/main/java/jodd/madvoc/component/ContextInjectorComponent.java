// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;

import javax.servlet.ServletContext;

/**
 * Context injector for all singleton Madvoc elements, like results and interceptors.
 */
public class ContextInjectorComponent {

	@PetiteInject
	protected PetiteContainer madpc;

	@PetiteInject
	InjectorsManager injectorsManager;

	@PetiteInject
	protected MadvocController madvocController;

	/**
	 * Inject context into target.
	 */
	public void injectContext(Object target) {
		ServletContext servletContext = madvocController.getApplicationContext();

		injectorsManager.getMadvocContextScopeInjector().injectContext(target, madpc);
		injectorsManager.getMadvocParamsInjector().injectContext(target, target.getClass().getName());

		injectorsManager.getServletContextScopeInjector().injectContext(target, servletContext);
		injectorsManager.getApplicationScopeInjector().injectContext(target, servletContext);
	}

}