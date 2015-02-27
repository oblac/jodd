// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ActionWrapper;

/**
 * Action interceptor.
 */
public interface ActionInterceptor extends ActionWrapper {

	/**
	 * Intercepts action requests.
	 */
	Object intercept(ActionRequest actionRequest) throws Exception;

}
