// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.interceptor.ServletConfigInterceptor;

public class ServletConfigAltInterceptor extends ServletConfigInterceptor {

	@Override
	public void init() {
		super.init();
		requestScopeInjector.getConfig().setCopyParamsToAttributes(true);
		requestScopeInjector.getConfig().setInjectParameters(false);
		requestScopeInjector.getConfig().setTreatEmptyParamsAsNull(true);
	}

}