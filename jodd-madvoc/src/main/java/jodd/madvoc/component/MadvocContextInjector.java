// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.injector.MadvocParamsInjector;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

/**
 * Set of injectors that can inject/outject values using various Madvoc 'global' scopes.
 * They can be used for {@link jodd.madvoc.interceptor.ActionInterceptor interceptors}
 * and {@link jodd.madvoc.result.ActionResult results}, too.
 *
 * @see ServletContextInjector
 */
public class MadvocContextInjector {

	@PetiteInject
	protected PetiteContainer madpc;

	protected MadvocContextScopeInjector madvocContextScopeInjector;
	protected MadvocParamsInjector madvocParamsInjector;

	@PetiteInitMethod(order = 1, firstOff = true)
	void createInjectors() {
		// need to have init method, so it can be called after the madpc is injected
		madvocContextScopeInjector = new MadvocContextScopeInjector(madpc);
		madvocParamsInjector = new MadvocParamsInjector(madpc);
	}

	/**
	 * Performs context injection.
	 */
	public void injectContext(Object target) {
		madvocContextScopeInjector.inject(target);
		madvocParamsInjector.inject(target);
	}

	/**
	 * Outjects context.
	 */
	public void outjectContext(Object target) {
		madvocContextScopeInjector.outject(target);
	}

}
