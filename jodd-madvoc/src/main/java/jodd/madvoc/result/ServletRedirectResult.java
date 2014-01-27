// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.bean.BeanTemplateParser;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.servlet.DispatcherUtil;

/**
 * Simply redirects to a page using <code>RequestDispatcher</code>.
 * 
 * @see ServletDispatcherResult
 */
public class ServletRedirectResult extends BaseActionResult<String> {

	public static final String NAME = "redirect";

	protected BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

	public ServletRedirectResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Redirects to the given location. Provided path is parsed, action is used as a value context.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultPath = resultMapper.resolveResultPath(actionRequest.getActionConfig(), resultValue);

		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		resultPath = beanTemplateParser.parse(resultPath, actionRequest.getAction());

		DispatcherUtil.redirect(request, response, resultPath);
	}

}