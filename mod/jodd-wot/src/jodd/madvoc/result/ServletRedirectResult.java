// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.madvoc.ActionRequest;
import jodd.servlet.DispatcherUtil;
import jodd.bean.BeanTool;


/**
 * Simply forwards to a page, without specifying new extension.
 * 
 * @see ServletDispatcherResult
 */
public class ServletRedirectResult extends ActionResult {

	public static final String NAME = "redirect";

	public ServletRedirectResult() {
		super(NAME);
	}

	/**
	 * Redirects to the given location. Does its redirection via a RequestDispatcher.
	 */
	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		resultPath = BeanTool.parseTemplate(resultPath, actionRequest.getAction());
		DispatcherUtil.redirect(request, response, resultPath);
	}
}
