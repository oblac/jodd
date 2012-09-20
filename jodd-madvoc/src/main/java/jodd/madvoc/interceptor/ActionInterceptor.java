// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;

/**
 * Action interceptor.
 */
public abstract class ActionInterceptor {

	protected boolean enabled = true;

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
	 * Returns <code>true</code> if interceptor is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Defines if interceptor is enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Invoked on interceptor initialization.
	 */
	public void init() {}

	/**
	 * Invokes interceptor using <code>enabled</code> information.
	 * When interceptor is disabled, control is passed to the next one.
	 * When interceptor is enabled, it will be invoked before the next
	 * one (or the action).
	 */
	public final Object invoke(ActionRequest actionRequest) throws Exception {
		if (enabled) {
			return intercept(actionRequest);
		} else {
			return actionRequest.invoke();
		}
	}

	/**
	 * Intercepts action requests.
	 */
	public abstract Object intercept(ActionRequest actionRequest) throws Exception;

	@Override
	public String toString() {
		return "interceptor: " + super.toString();
	}
}
