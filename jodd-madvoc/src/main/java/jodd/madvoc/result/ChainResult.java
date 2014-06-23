// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;

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

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Sets the {@link jodd.madvoc.ActionRequest#setNextActionPath(String) next action request} for the chain.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultBasePath = actionRequest.getActionConfig().getResultBasePath();

		String resultPath = resultMapper.resolveResultPathString(resultBasePath, resultValue);

		actionRequest.setNextActionPath(resultPath);
	}

}