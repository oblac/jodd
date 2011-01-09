// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;

/**
 * Action interceptor.
 */
public abstract class ActionInterceptor {

	protected boolean initialized;

	/**
	 * Returns <code>true</code> if interceptor is initialized.
	 */
	public final boolean isInitialized() {
	    return initialized;
	}

	/**
	 * Marks that interceptor is initialized.
	 */
	public final void initialized() {
		initialized = true;
	}

	/**
	 * Invoked on interceptor initialization.
	 */
	public void init() {}

	/**
	 * Intercepts action requests.
	 */
	public abstract Object intercept(ActionRequest actionRequest) throws Exception;

	@Override
	public String toString() {
		return "interceptor: " + super.toString();
	}
}
