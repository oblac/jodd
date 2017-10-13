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
 * <ul>
 * <li>invalidates session if session id is exposed in the URL</li>
 * <li>removes session id from URLs.</li>
 * </ul>
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
