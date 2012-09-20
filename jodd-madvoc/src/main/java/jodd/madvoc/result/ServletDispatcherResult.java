// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocUtil;
import jodd.servlet.DispatcherUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Dispatches to a JSP page.
 * 
 * @see ServletRedirectResult
 */
public class ServletDispatcherResult extends ActionResult {

	public static final String NAME = "dispatch";
	protected static final String EXTENSION = ".jsp";

	public ServletDispatcherResult() {
		super(NAME);
	}


	/**
	 * Dispatches to the JSP location created from result value and JSP extension.
	 * Does its forward via a RequestDispatcher. If the dispatch fails a 404 error
	 * will be sent back in the http response, what will produce error 500.
	 */
	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		String target;
		String originalResultPath = resultPath;
		while (true) {
			target = resultPath + EXTENSION;
			try {
				if (locateTarget(request, target) != null) {
					break;
				}
			} catch (MalformedURLException muex) {
				// ignore
			}
			int dotNdx = MadvocUtil.lastIndexOfDotAfterSlash(resultPath);
			if (dotNdx == -1) {
				response.sendError(SC_NOT_FOUND, "Result '" + originalResultPath + EXTENSION + "' or any its variant not found.");
				return;
			}
			resultPath = resultPath.substring(0, dotNdx);
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(target);
		if (dispatcher == null) {
			response.sendError(SC_NOT_FOUND, "Result '" + target + "' not found.");	// this should never happened
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
	 * Locates target.
	 */
	protected URL locateTarget(HttpServletRequest request, String target) throws Exception {
		return request.getSession().getServletContext().getResource(target);
	}

}
