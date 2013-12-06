// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;

/**
 * Action interceptor.
 */
public interface ActionInterceptor {

	/**
	 * Returns <code>true</code> if interceptor is initialized.
	 */
	boolean isInitialized();

	/**
	 * Returns <code>true</code> if interceptor is enabled.
	 */
	boolean isEnabled();

	/**
	 * Defines if interceptor is enabled.
	 */
	void setEnabled(boolean enabled);

	/**
	 * Initializes interceptor.
	 */
	void init();

	/**
	 * Invokes interceptor using <code>enabled</code> information.
	 * When interceptor is disabled, control is passed to the next one.
	 * When interceptor is enabled, it will be invoked before the next
	 * one (or the action).
	 */
	Object invoke(ActionRequest actionRequest) throws Exception;

	/**
	 * Intercepts action requests.
	 */
	Object intercept(ActionRequest actionRequest) throws Exception;

}
