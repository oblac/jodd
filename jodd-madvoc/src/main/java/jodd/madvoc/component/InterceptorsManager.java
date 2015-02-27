// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;

import java.util.Set;

/**
 * Manager for Madvoc interceptors. By default, all interceptors are cached,
 * so there will be only one instance per its type.
 * @see jodd.madvoc.component.FiltersManager
 */
public class InterceptorsManager extends WrapperManager<ActionInterceptor> {

	@Override
	protected ActionInterceptor[] createArray(int len) {
		return new ActionInterceptor[len];
	}

	@Override
	protected Class<? extends ActionInterceptor> getDefaultWebAppWrapper() {
		return DefaultWebAppInterceptors.class;
	}

	@Override
	protected Class<? extends ActionInterceptor>[] getDefaultWrappers() {
		return madvocConfig.getDefaultInterceptors();
	}

	/**
	 * Returns all interceptors.
	 */
	public Set<ActionInterceptor> getAllInterceptors() {
		return super.getAll();
	}

}