// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.component.InjectorsManager;
import jodd.madvoc.injector.RequestScopeInjector;

public class MyInjectorsManager extends InjectorsManager {

	@Override
	public RequestScopeInjector createRequestScopeInjector() {
		RequestScopeInjector rsi = super.createRequestScopeInjector();
		rsi.setEncodeGetParams(true);
		return rsi;
	}
}
