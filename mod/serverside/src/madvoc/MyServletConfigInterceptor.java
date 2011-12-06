// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.interceptor.ServletConfigInterceptor;

public class MyServletConfigInterceptor extends ServletConfigInterceptor {

	@Override
	public void init() {
		super.init();
		requestScopeInjector.getConfig().setEncodeGetParams(true);
	}

}
