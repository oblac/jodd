// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.MadvocParamsInjector;
import jodd.madvoc.injector.ServletContextScopeInjector;
import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.PetiteContainer;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.madvoc.injector.MadvocContextScopeInjector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * Set of injectors that can inject/outject values using various 'global' scopes.
 * They can be used for {@link jodd.madvoc.interceptor.ActionInterceptor interceptors}
 * and {@link jodd.madvoc.result.ActionResult results}, too.
 */
public class ContextInjector {

	@PetiteInject
	protected PetiteContainer madpc;

	protected ApplicationScopeInjector applicationScopeInjector;
	protected MadvocContextScopeInjector madvocContextScopeInjector;
	protected ServletContextScopeInjector servletContextScopeInjector;
	protected MadvocParamsInjector madvocParamsInjector;

	@PetiteInitMethod(order = 1, firstOff = true)
	void createInjectors() {
		// need to have init method, so it can be called after the madpc is injected
		applicationScopeInjector = new ApplicationScopeInjector();
		madvocContextScopeInjector = new MadvocContextScopeInjector(madpc);
		servletContextScopeInjector = new ServletContextScopeInjector();
		madvocParamsInjector = new MadvocParamsInjector(madpc);
	}

	/**
	 * Performs default context injection.
	 */
	public void injectContext(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse, boolean injectParams) {
		servletContextScopeInjector.inject(target, servletRequest, servletResponse);
		madvocContextScopeInjector.inject(target);
		applicationScopeInjector.inject(target, servletRequest.getSession().getServletContext());
		if (injectParams) {
			madvocParamsInjector.inject(target);
		}
	}

	/**
	 * Performs context injection when only servlet context is available. Should be called for all
	 * global instances (such as interceptors).
	 */
	public void injectContext(Object target, ServletContext servletContext, boolean injectParams) {
		servletContextScopeInjector.inject(target, servletContext);
		madvocContextScopeInjector.inject(target);
		applicationScopeInjector.inject(target, servletContext);
		if (injectParams) {
			madvocParamsInjector.inject(target);
		}
	}

	/**
	 * Outjects context.
	 */
	public void outjectContext(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		servletContextScopeInjector.outject(target, servletResponse);
		madvocContextScopeInjector.outject(target);
		applicationScopeInjector.outject(target, servletRequest.getSession().getServletContext());
	}

}
