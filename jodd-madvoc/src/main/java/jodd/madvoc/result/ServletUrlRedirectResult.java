// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.bean.BeanTemplateParser;
import jodd.madvoc.ActionRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simply sends redirect to an external location.
 */
public class ServletUrlRedirectResult extends BaseActionResult<String> {

	public static final String NAME = "url";

	protected BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

	public ServletUrlRedirectResult() {
		super(NAME);
	}

	/**
	 * Redirects to the external location.
	 */
	public void render(ActionRequest actionRequest, String resultValue, String resultPath) throws Exception {
		resultValue = beanTemplateParser.parse(resultValue, actionRequest.getAction());
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		response.sendRedirect(resultValue);
	}

}