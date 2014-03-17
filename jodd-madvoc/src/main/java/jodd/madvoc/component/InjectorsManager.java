// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.ActionPathMacroInjector;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.injector.MadvocParamsInjector;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.ServletContextScopeInjector;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_DEFINE;

/**
 * Injectors manager creates and holds instances of all injectors.
 */
public class InjectorsManager {

	@PetiteInject
	protected PetiteContainer madpc;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ScopeDataResolver scopeDataResolver;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ActionPathMacroInjector actionPathMacroInjector;
	protected MadvocContextScopeInjector madvocContextScopeInjector;
	protected MadvocParamsInjector madvocParamsInjector;
	protected ApplicationScopeInjector applicationScopeInjector;
	protected ServletContextScopeInjector servletContextScopeInjector;

	@PetiteInitMethod(order = 1, invoke = POST_DEFINE)
	void createInjectors() {
		requestScopeInjector = new RequestScopeInjector(madvocConfig, scopeDataResolver);
		sessionScopeInjector = new SessionScopeInjector(madvocConfig, scopeDataResolver);
		actionPathMacroInjector = new ActionPathMacroInjector();
		madvocContextScopeInjector = new MadvocContextScopeInjector(madvocConfig, scopeDataResolver, madpc);
		madvocParamsInjector = new MadvocParamsInjector(madpc);
		applicationScopeInjector = new ApplicationScopeInjector(madvocConfig, scopeDataResolver);
		servletContextScopeInjector = new ServletContextScopeInjector(madvocConfig, scopeDataResolver);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones <code>InjectorManager</code> and creates new set of injectors.
	 */
	@Override
	public InjectorsManager clone() {
		InjectorsManager injectorsManager = new InjectorsManager();
		injectorsManager.madpc = this.madpc;
		injectorsManager.madvocConfig = this.madvocConfig;
		injectorsManager.scopeDataResolver = this.scopeDataResolver;
		injectorsManager.createInjectors();
		return injectorsManager;
	}


	// ---------------------------------------------------------------- getter

	public RequestScopeInjector getRequestScopeInjector() {
		return requestScopeInjector;
	}

	public SessionScopeInjector getSessionScopeInjector() {
		return sessionScopeInjector;
	}

	public ActionPathMacroInjector getActionPathMacroInjector() {
		return actionPathMacroInjector;
	}

	public MadvocContextScopeInjector getMadvocContextScopeInjector() {
		return madvocContextScopeInjector;
	}

	public MadvocParamsInjector getMadvocParamsInjector() {
		return madvocParamsInjector;
	}

	public ApplicationScopeInjector getApplicationScopeInjector() {
		return applicationScopeInjector;
	}

	public ServletContextScopeInjector getServletContextScopeInjector() {
		return servletContextScopeInjector;
	}
}