// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;

/**
 * Process chain results. Chaining is very similar to forwarding, except it is done
 * by {@link jodd.madvoc.MadvocServletFilter} and not by container. Chaining to next action request
 * happens after the complete execution of current one: after all interceptors and this result has been
 * finished.
 */
public class ChainResult extends BaseActionResult<String> {

	public static final String NAME = "chain";

	public ChainResult() {
		super(NAME);
	}

	/**
	 * Sets the {@link jodd.madvoc.ActionRequest#setNextActionPath(String) next action request} for the chain.
	 */
	public void render(ActionRequest actionRequest, String resultValue, String resultPath) throws Exception {
		actionRequest.setNextActionPath(resultPath);
	}

}