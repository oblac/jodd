// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;

/**
 * This is a simple join of {@link IdRequestInjectorInterceptor} and {@link PrepareInterceptor}.
 * Since it is common to use these interceptors together, this one joins then into one interceptor.
 * This join is a bit more efficient, since ids will be injected only if action is
 * {@link Preparable}. 
 */
public class PrepareAndIdInjectorInterceptor extends IdRequestInjectorInterceptor {

	/**
	 * If action is preparable, injects ids from request and invokes <code>prepare()</code>.
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		Object action = actionRequest.getAction();
		if (action instanceof Preparable) {
			injectIdsFromRequest(actionRequest);
			((Preparable) action).prepare();
		}
		return actionRequest.invoke();
	}

}
