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
	 * Returns the name of this action result.
	 * Returned name can be <code>null</code> for results
	 * that does not need to be found using string name identification;
	 * i.e. when action does not return a string result.
	 */
	String getResultName();

	/**
	 * Returns type of result value, passed to the {@link #render(jodd.madvoc.ActionRequest, Object) render method}
	 * and defined by generics. Returns <code>null</code> when this action result does not need
	 * to be registered for result value type (eg when used in @Action annotation).
	 */
	Class<T> getResultValueType();

	/**
	 * Renders result on given action result value.
	 * @param actionRequest action request
	 * @param resultValue action method result, may be <code>null</code>
	 */
	void render(ActionRequest actionRequest, T resultValue) throws Exception;

	/**
	 * Initializes the result.
	 */
	void init();

}