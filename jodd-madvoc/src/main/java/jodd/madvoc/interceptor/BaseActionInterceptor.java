// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.BaseActionWrapper;

/**
 * Base {@link jodd.madvoc.interceptor.ActionInterceptor action intercepter}.
 */
public abstract class BaseActionInterceptor extends BaseActionWrapper implements ActionInterceptor {

	public final Object invoke(ActionRequest actionRequest) throws Exception {
		if (enabled) {
			return intercept(actionRequest);
		} else {
			return actionRequest.invoke();
		}
	}

	@Override
	public String toString() {
		return "interceptor: " + this.getClass();
	}

}