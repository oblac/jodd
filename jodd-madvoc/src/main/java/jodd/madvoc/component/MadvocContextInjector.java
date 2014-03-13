// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.injector.MadvocParamsInjector;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_DEFINE;

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

	@PetiteInitMethod(order = 1, invoke = POST_DEFINE)
	void createInjectors() {
		madvocContextScopeInjector = new MadvocContextScopeInjector();
		madvocParamsInjector = new MadvocParamsInjector(madpc);
	}

	/**
	 * Performs Madvoc context injection.
	 */
	public void injectMadvocContext(Object target) {
		madvocContextScopeInjector.injectContext(target, madpc);
	}

	/**
	 * Performs Madvoc params injection.
	 */
	public void injectMadvocParams(Object target) {
		madvocParamsInjector.injectContext(target, target.getClass().getName());
	}

}