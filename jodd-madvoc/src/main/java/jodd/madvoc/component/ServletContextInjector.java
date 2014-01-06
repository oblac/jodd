// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.ServletContextScopeInjector;
import jodd.petite.meta.PetiteInitMethod;
import jodd.madvoc.injector.ApplicationScopeInjector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_DEFINE;

/**
 * Set of injectors that can inject/outject values using various servlets 'global' scopes.
 * They can be used for {@link jodd.madvoc.interceptor.ActionInterceptor interceptors}
 * and {@link jodd.madvoc.result.ActionResult results}, too.
 *
 * @see MadvocContextInjector
 */
public class ServletContextInjector {

	protected ApplicationScopeInjector applicationScopeInjector;
	protected ServletContextScopeInjector servletContextScopeInjector;

	@PetiteInitMethod(order = 1, invoke = POST_DEFINE)
	void createInjectors() {
		applicationScopeInjector = new ApplicationScopeInjector();
		servletContextScopeInjector = new ServletContextScopeInjector();
	}

	/**
	 * Performs default context injection.
	 */
	public void injectContext(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		servletContextScopeInjector.inject(target, servletRequest, servletResponse);
		applicationScopeInjector.inject(target, servletRequest.getSession().getServletContext());
	}

	/**
	 * Performs context injection when only servlet context is available. Should be called for all
	 * global instances (such as interceptors).
	 */
	public void injectContext(Object target, ServletContext servletContext) {
		servletContextScopeInjector.inject(target, servletContext);
		applicationScopeInjector.inject(target, servletContext);
	}

	/**
	 * Outjects context.
	 */
	public void outjectContext(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		servletContextScopeInjector.outject(target, servletResponse);
		applicationScopeInjector.outject(target, servletRequest.getSession().getServletContext());
	}

}