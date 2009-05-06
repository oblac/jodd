// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.petite.meta.PetiteInject;

/**
 * Petite-aware interceptors manager.
 */
public class PetiteInterceptorManager extends InterceptorsManager {

	@PetiteInject
	protected PetiteMadvocComponent petiteMadvocComponent;

	/**
	 * Acquires interceptor from Petite container.
	 */
	@Override
	protected ActionInterceptor createInterceptor(Class<? extends ActionInterceptor> interceptorClass) {
		return petiteMadvocComponent.getPetiteContainer().createBean(interceptorClass);
	}
}
