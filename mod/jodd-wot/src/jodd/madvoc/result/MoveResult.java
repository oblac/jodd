// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.servlet.URLCoder;
import jodd.servlet.DispatcherUtil;
import jodd.util.RandomStringUtil;

import javax.servlet.http.HttpSession;

/**
 * Process move results.
 */
public class MoveResult extends ActionResult {

	public static final String NAME = "move";

	public MoveResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	/**
	 * Returns unique id, random long value.
	 */
	protected String generateUniqueId() {
		return RandomStringUtil.randomAlphaNumeric(32);
	}

	/**
	 * Saves action in the session under some id that is added as request parameter.
	 */
	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpSession session = actionRequest.getHttpServletRequest().getSession();
		String id = generateUniqueId();
		session.setAttribute(id, actionRequest);
		resultPath = URLCoder.build().base(resultPath).param(madvocConfig.getAttrNames().moveId, id).toString();
		DispatcherUtil.redirect(actionRequest.getHttpServletRequest(), actionRequest.getHttpServletResponse(), resultPath);
	}

}
