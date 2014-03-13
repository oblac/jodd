// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.injector.ServletContextScopeInjector;
import jodd.petite.meta.PetiteInitMethod;
import jodd.madvoc.injector.ApplicationScopeInjector;

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
	public void inject(ActionRequest actionRequest) {
		servletContextScopeInjector.inject(actionRequest);
		applicationScopeInjector.inject(actionRequest);
	}

	/**
	 * Performs context injection when only servlet context is available. Should be called for all
	 * global instances (such as interceptors, results, etc).
	 */
	public void injectContext(Object target, ServletContext servletContext) {
		servletContextScopeInjector.injectContext(target, servletContext);
		applicationScopeInjector.injectContext(target, servletContext);
	}

	/**
	 * Outjects context.
	 */
	public void outject(ActionRequest actionRequest) {
		servletContextScopeInjector.outject(actionRequest);
		applicationScopeInjector.outject(actionRequest);
	}

}