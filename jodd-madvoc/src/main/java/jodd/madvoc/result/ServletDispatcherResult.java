// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.ResultPath;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.servlet.DispatcherUtil;
import jodd.util.StringPool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Servlet Dispatcher.
 * 
 * @see ServletRedirectResult
 */
public class ServletDispatcherResult extends BaseActionResult<String> {

	private static final Logger log = LoggerFactory.getLogger(ServletDispatcherResult.class);

	public static final String NAME = "dispatch";
	protected HashMap<String, String> targetCache;

	protected String[] extensions = new String[] {".jspf", ".jsp"};

	public ServletDispatcherResult() {
		super(NAME);
		targetCache = new HashMap<String, String>(256);
	}

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Dispatches to the JSP location created from result value and JSP extension.
	 * Does its forward via a <code>RequestDispatcher</code>. If the dispatch fails, a 404 error
	 * will be sent back in the http response.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String actionAndResultPath = actionRequest.getActionPath() + (resultValue != null ? ':' + resultValue : StringPool.EMPTY);
		String target = targetCache.get(actionAndResultPath);

		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		ServletContext servletContext = request.getSession().getServletContext();

		if (target == null) {
			if (log.isDebugEnabled()) {
				log.debug("target not found: " + actionAndResultPath);
			}
			ResultPath resultPath = resultMapper.resolveResultPath(actionRequest.getActionPath(), resultValue);

			String actionPath = resultPath.getPath();
			String path = actionPath;
			String value = resultPath.getValue();

			loop:
			while (true) {
				for (String ext : extensions) {
					// variant #1: with value
					if (value != null) {
						if (path == null) {
							// only value remains
							int lastSlashNdx = actionPath.lastIndexOf('/');
							if (lastSlashNdx != -1) {
								target = actionPath.substring(0, lastSlashNdx + 1) + value + ext;
							} else {
								target = '/' + value + ext;
							}
						} else {
							target = path + '.' + value + ext;
						}

						if (targetExist(servletContext, target)) {
							break loop;
						}
					}
				}

				for (String ext : extensions) {
					// variant #2: without value

					if (path != null) {
						target = path + ext;

						if (targetExist(servletContext, target)) {
							break loop;
						}
					}
				}

				// continue

				if (path == null) {
					response.sendError(SC_NOT_FOUND, "Result not found: " + resultPath);
					return;
				}

				int dotNdx = MadvocUtil.lastIndexOfDotAfterSlash(path);
				if (dotNdx == -1) {
					path = null;
				} else {
					path = path.substring(0, dotNdx);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("target found: " + target);
			}

			// store target in cache
			targetCache.put(actionAndResultPath, target);
		}

		// the target exists, continue

		target = processTarget(servletContext, target);

		RequestDispatcher dispatcher = request.getRequestDispatcher(target);
		if (dispatcher == null) {
			response.sendError(SC_NOT_FOUND, "Result not found: " + target);	// should never happened
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
	protected boolean targetExist(ServletContext servletContext, String target) {
		if (log.isDebugEnabled()) {
			log.debug("target check: " + target);
		}
		try {
			return servletContext.getResource(target) != null;
		} catch (MalformedURLException ignore) {
			return false;
		}
	}

	/**
	 * Processes target and returns a new target location of located JSP file.
	 */
	protected String processTarget(ServletContext servletContext, String target) {
		return target;
	}

}