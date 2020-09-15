// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.servlet.DispatcherUtil;
import jodd.util.StringPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Servlet Dispatcher.
 * 
 * @see ServletRedirectActionResult
 */
public class ServletDispatcherActionResult extends AbstractTemplateViewActionResult {

	private static final Logger log = LoggerFactory.getLogger(ServletDispatcherActionResult.class);

	protected final String[] defaultViewExtensions = new String[] {".jspf", ".jsp"};
	protected final String defaultViewPageName = "index";

	/**
	 * Renders the view by dispatching to the target JSP.
	 */
	@Override
	protected void renderView(final ActionRequest actionRequest, final String target) throws Exception {
		final HttpServletRequest request = actionRequest.getHttpServletRequest();
		final HttpServletResponse response = actionRequest.getHttpServletResponse();

		final RequestDispatcher dispatcher = request.getRequestDispatcher(target);
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
	 * Locates target using path with various extensions appended.
	 */
	@Override
	protected String locateTarget(final ActionRequest actionRequest, String path) {
		String target;

		if (path.endsWith(StringPool.SLASH)) {
			path = path + defaultViewPageName;
		}

		for (final String ext : defaultViewExtensions) {
			target = path + ext;

			if (targetExists(actionRequest, target)) {
				return target;
			}
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if target exists.
	 */
	protected boolean targetExists(final ActionRequest actionRequest, final String target) {
		if (log.isDebugEnabled()) {
			log.debug("target check: " + target);
		}

		final ServletContext servletContext = actionRequest.getHttpServletRequest().getServletContext();

		try {
			return servletContext.getResource(target) != null;
		} catch (final MalformedURLException ignore) {
			return false;
		}
	}

	@Override
	protected Redirect resultOf(Object value) {
		if (value == null) {
			value = StringPool.EMPTY;
		}
		return Redirect.to((String) value);
	}
}
