// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.interceptor.ServletConfigInterceptor;

/**
 * Alternative servlet configurator that first copies all request parameters to attributes and inject only attributes. 
 * @see jodd.madvoc.interceptor.ServletConfigInterceptor
 */
public class ServletConfigAltInterceptor extends ServletConfigInterceptor {

	@Override
	public void init() {
		super.init();
		requestScopeInjector = injectorsManager.createRequestScopeInjector();
		requestScopeInjector.setCopyParamsToAttributes(true);
		requestScopeInjector.setInjectParameters(false);
//		requestScopeInjector.setTreatEmptyParamsAsNull(true);
	}

}
