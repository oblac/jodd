// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Common interface for {@link jodd.madvoc.interceptor.ActionInterceptor}
 * and {@link jodd.madvoc.filter.ActionFilter}.
 */
public interface ActionWrapper {

	/**
	 * Returns <code>true</code> if wrapper is enabled.
	 */
	boolean isEnabled();

	/**
	 * Defines if wrapper is enabled.
	 */
	void setEnabled(boolean enabled);

	/**
	 * Initializes wrapper once everything is injected.
	 */
	void init();

	/**
	 * Invokes wrapper using <code>enabled</code> information.
	 * When wrapper is disabled, control is passed to the next one.
	 * When wrapper is enabled, it will be invoked before the next
	 * one (or before the action).
	 */
	Object invoke(ActionRequest actionRequest) throws Exception;

}