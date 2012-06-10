// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Protects from exposing session ids in URLs for security reasons.
 * Does the following:
 * <li>invalidates session if session id is exposed in the URL
 * <li>removes session id from URLs.
 */
public class RemoveSessionFromUrlFilter implements Filter {

	/**
	 * Filters requests to remove URL-based session identifiers.
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isRequestedSessionIdFromURL(httpRequest)) {
			HttpSession session = httpRequest.getSession(false);

			if (session != null) {
				session.invalidate();		// clear session if session id in URL
			}
		}

		// wrap response to remove URL encoding
		HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
			@Override
			public String encodeRedirectUrl(String url) {
				return url;
			}

			@Override
			public String encodeRedirectURL(String url) {
				return url;
			}

			@Override
			public String encodeUrl(String url) {
				return url;
			}

			@Override
			public String encodeURL(String url) {
				return url;
			}
		};

		chain.doFilter(request, wrappedResponse);
	}

	/**
	 * Detects if session ID exist in the URL. It works more reliable
	 * than <code>servletRequest.isRequestedSessionIdFromURL()</code>.
	 */
	protected boolean isRequestedSessionIdFromURL(HttpServletRequest servletRequest) {
		if (servletRequest.isRequestedSessionIdFromURL()) {
			return true;
		}

		HttpSession session = servletRequest.getSession(false);
		if (session != null) {
			String sessionId = session.getId();
			StringBuffer requestUri = servletRequest.getRequestURL();

			return requestUri.indexOf(sessionId) != -1;
		}

		return false;
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}
}
