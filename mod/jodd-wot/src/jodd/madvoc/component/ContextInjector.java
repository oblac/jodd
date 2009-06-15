// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.petite.meta.PetiteInject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * Context injector inject data from application and madvoc context.
 * Adapter for {@link jodd.madvoc.injector.ApplicationScopeInjector} and {@link jodd.madvoc.injector.MadvocContextScopeInjector}
 */
public class ContextInjector {

	@PetiteInject
	protected ApplicationScopeInjector applicationScopeInjector;

	@PetiteInject
	protected MadvocContextScopeInjector madvocContextScopeInjector;

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
