// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.PetiteContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Context injector inject data from application and madvoc context.
 */
public class ContextInjector {

	@PetiteInject
	protected PetiteContainer petiteContainer;

	@PetiteInject
	protected ScopeDataManager scopeDataManager;

	protected ApplicationScopeInjector applicationInjector;
	protected MadvocContextScopeInjector madvocContextInjector;
	

	@PetiteInitMethod(order = 1)
	void contextInjectorInit() {
		applicationInjector = new ApplicationScopeInjector(scopeDataManager);
		madvocContextInjector = new MadvocContextScopeInjector(scopeDataManager, petiteContainer);
		init();
	}

	/**
	 * Additional custom initialization, invoked after manager is ready.
	 */
	protected void init() {}

	/**
	 * Performs default injection.
	 */
	public void inject(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		madvocContextInjector.inject(target, servletRequest, servletResponse);
		applicationInjector.inject(target, servletRequest);
	}

	public void outject(Object target, HttpServletRequest servletRequest) {
		madvocContextInjector.outject(target, servletRequest);
		applicationInjector.outject(target, servletRequest);
	}

}
