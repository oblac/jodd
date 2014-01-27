// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.util.URLCoder;
import jodd.servlet.DispatcherUtil;
import jodd.util.RandomStringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Process move results.
 */
public class MoveResult extends BaseActionResult<String> {

	public static final String NAME = "move";

	public MoveResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Returns unique id, random long value.
	 */
	protected String generateUniqueId() {
		return RandomStringUtil.randomAlphaNumeric(32);
	}

	/**
	 * Saves action in the session under some id that is added as request parameter.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultPath = resultMapper.resolveResultPath(actionRequest.getActionConfig(), resultValue);

		HttpServletRequest httpServletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = httpServletRequest.getSession();

		String id = generateUniqueId();
		session.setAttribute(id, actionRequest);

		resultPath = URLCoder.build(resultPath).queryParam(madvocConfig.getAttributeMoveId(), id).toString();
		DispatcherUtil.redirect(httpServletRequest, actionRequest.getHttpServletResponse(), resultPath);
	}

}