// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ScopeData;
import jodd.madvoc.injector.Target;
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
	protected InjectorsManager injectorsManager;

	@PetiteInject
	protected MadvocController madvocController;

	@PetiteInject
	protected ScopeDataResolver scopeDataResolver;

	/**
	 * Inject context into target.
	 */
	public void injectContext(Target target) {
		Class targetType = target.resolveType();

		ScopeData[] scopeData = scopeDataResolver.resolveScopeData(targetType);

		ServletContext servletContext = madvocController.getApplicationContext();

		injectorsManager.getMadvocContextScopeInjector().injectContext(target, scopeData, madpc);
		injectorsManager.getMadvocParamsInjector().injectContext(target, scopeData, madpc);

		injectorsManager.getServletContextScopeInjector().injectContext(target, scopeData, servletContext);
		injectorsManager.getApplicationScopeInjector().injectContext(target, scopeData, servletContext);
	}

}