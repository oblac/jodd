// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;

/**
 * Action result renders the returned value from an action.
 * Results are singletons for the web application. Results
 * may have a result type, a string identification of the type
 * used when actions return string result.
 */
public interface ActionResult<T> {

	/**
	 * Returns the type of this action result.
	 * Returned type can be <code>null</code> for results
	 * that does not need to be found using string identification;
	 * i.e. when action does not return a string result.
	 */
	String getResultType();

	/**
	 * Returns type of result value, passed to the {@link #render(jodd.madvoc.ActionRequest, Object, String) render method}
	 * and defined by generics.
	 */
	Class<T> getResultValueType();

	/**
	 * Renders result on given action result value.
	 * @param actionRequest action request
	 * @param resultValue action method result, may be <code>null</code>
	 * @param resultPath result path
	 */
	void render(ActionRequest actionRequest, T resultValue, String resultPath) throws Exception;

	/**
	 * Returns <code>true</code> if result is initialized.
	 */
	boolean isInitialized();

	/**
	 * Initializes the result. After this call,
	 * {@link #isInitialized()} returns <code>true</code>.
	 */
	void init();

}