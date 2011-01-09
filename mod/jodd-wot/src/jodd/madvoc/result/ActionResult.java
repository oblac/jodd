// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;

/**
 * Action result for specified result type. Action results are singletons for the web application.
 */
public abstract class ActionResult {

	protected String type;

	/**
	 * Creates new action result.
	 */
	protected ActionResult(String type) {
		this.type = type;
	}

	/**
	 * Returns the type of this action result.
	 */
	public String getType() {
		return type;
	}


	/**
	 * Executes result on given action result value.
	 * @param actionRequest action request
	 * @param resultObject reference to action method result, may be null
	 * @param resultValue string representation of result, may be null
	 * @param resultPath result path
	 */
	public abstract void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception;


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
	 * Invoked on result initialization.
	 */
	public void init() {}


	@Override
	public String toString() {
		return "result: " + type;
	}
}
