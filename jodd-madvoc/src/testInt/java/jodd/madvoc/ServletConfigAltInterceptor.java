// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.interceptor.ServletConfigInterceptor;

public class ServletConfigAltInterceptor extends ServletConfigInterceptor {

	@Override
	public void init() {
		super.init();

		injectorsManager = this.injectorsManager.clone();

		RequestScopeInjector requestScopeInjector = injectorsManager.getRequestScopeInjector();
		RequestScopeInjector.Config requestScopeInjectorConfig = requestScopeInjector.getConfig();

		requestScopeInjectorConfig.setCopyParamsToAttributes(true);
		requestScopeInjectorConfig.setInjectParameters(false);
		requestScopeInjectorConfig.setTreatEmptyParamsAsNull(true);
	}

}