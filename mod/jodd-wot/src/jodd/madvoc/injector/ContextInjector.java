// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.component.ScopeDataManager;
import jodd.petite.PetiteContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * Context injector injects data from application and madvoc context.
 * It works as an adapter for {@link jodd.madvoc.injector.ApplicationScopeInjector} and {@link jodd.madvoc.injector.MadvocContextScopeInjector}
 */
public class ContextInjector {

	protected final PetiteContainer madpc;
	protected final ScopeDataManager scopeDataManager;

	protected ApplicationScopeInjector applicationScopeInjector;
	protected MadvocContextScopeInjector madvocContextScopeInjector;


	public ContextInjector(ScopeDataManager scopeDataManager, PetiteContainer madpc) {
		this.scopeDataManager = scopeDataManager;
		this.madpc = madpc;
		applicationScopeInjector = new ApplicationScopeInjector(scopeDataManager);
		madvocContextScopeInjector = new MadvocContextScopeInjector(scopeDataManager, madpc);
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
		madvocContextScopeInjector.inject(target, servletRequest, servletResponse);
		applicationScopeInjector.inject(target, servletRequest.getSession().getServletContext());
	}

	/**
	 * Performs outjections.
	 */
	public void outject(Object target, HttpServletRequest servletRequest) {
		madvocContextScopeInjector.outject(target, servletRequest);
		applicationScopeInjector.outject(target, servletRequest.getSession().getServletContext());
	}

	/**
	 * Performs injection when only servlet context is availiable. Should be called for all
	 * global instances (such as interceptors).
	 */
	public void inject(Object target, ServletContext servletContext) {
		madvocContextScopeInjector.inject(target, servletContext);
		applicationScopeInjector.inject(target, servletContext);
	}

}
