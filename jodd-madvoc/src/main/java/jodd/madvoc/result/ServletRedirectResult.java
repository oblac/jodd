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

import java.io.IOException;

/**
 * Simply redirects to a page using <code>RequestDispatcher</code>.
 * 
 * @see ServletDispatcherResult
 * @see jodd.madvoc.result.ServletUrlRedirectResult
 */
public class ServletRedirectResult extends BaseActionResult<String> {

	public static final String NAME = "redirect";

	protected BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

	public ServletRedirectResult() {
		super(NAME);
	}

	protected ServletRedirectResult(String name) {
		super(name);
	}

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Redirects to the given location. Provided path is parsed, action is used as a value context.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultPath = resultMapper.resolveResultPathString(actionRequest.getActionPath(), resultValue);

		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		String path = resultPath;
		path = beanTemplateParser.parse(path, actionRequest.getAction());

		redirect(request, response, path);
	}

	protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		DispatcherUtil.redirect(request, response, path);
	}

}