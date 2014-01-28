// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.servlet.DispatcherUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import java.net.MalformedURLException;

/**
 * Dispatches to a JSP page.
 * 
 * @see ServletRedirectResult
 */
public class ServletDispatcherResult extends BaseActionResult<String> {

	public static final String NAME = "dispatch";
	protected static final String EXTENSION = ".jsp";

	public ServletDispatcherResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Dispatches to the JSP location created from result value and JSP extension.
	 * Does its forward via a RequestDispatcher. If the dispatch fails, a 404 error
	 * will be sent back in the http response.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultPath = resultMapper.resolveResultPath(actionRequest.getActionConfig(), resultValue);

		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		String target;
		String originalResultPath = resultPath;
		while (true) {
			target = resultPath + EXTENSION;

			if (targetExist(request, target)) {
				break;
			}

			int dotNdx = MadvocUtil.lastIndexOfDotAfterSlash(resultPath);
			if (dotNdx == -1) {
				response.sendError(SC_NOT_FOUND, "Result not resolved: " + originalResultPath + EXTENSION);
				return;
			}

			resultPath = resultPath.substring(0, dotNdx);
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(target);
		if (dispatcher == null) {
			response.sendError(SC_NOT_FOUND, "Result not found: " + target);	// this should never happened
			return;
		}

		// If we're included, then include the view, otherwise do forward.
		// This allow the page to, for example, set content type.
		if (DispatcherUtil.isPageIncluded(request, response)) {
			dispatcher.include(request, response);
		} else {
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Returns <code>true</code> if target exists.
	 */
	protected boolean targetExist(HttpServletRequest request, String target) {
		try {
			return request.getSession().getServletContext().getResource(target) != null;
		} catch (MalformedURLException ignore) {
			return false;
		}
	}

}