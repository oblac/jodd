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

package jodd.servlet;

import jodd.util.StringPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * Request utilities for path, uri, query strings etc..
 */
public class DispatcherUtil {

	// ---------------------------------------------------------------- include

	/**
	 * Include page which path is relative to the current HTTP request.
	 */
	public static boolean include(ServletRequest request, ServletResponse response, String page) throws IOException, ServletException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		if (dispatcher != null) {
			dispatcher.include(request, response);
			return true;
		}
		return false;
	}

	/**
	 * Include named resource.
	 */
	public static boolean includeNamed(HttpServletRequest request, ServletResponse response, String resource) throws IOException, ServletException {
		return includeNamed(request.getSession().getServletContext(), request, response, resource);
	}


	/**
	 * Include named resource.
	 */
	public static boolean includeNamed(ServletContext context, ServletRequest request, ServletResponse response, String page) throws IOException, ServletException {
		RequestDispatcher dispatcher = context.getNamedDispatcher(page);
		if (dispatcher != null) {
			dispatcher.include(request, response);
			return true;
		}
		return false;
	}



	/**
	 * Include page which path relative to the root of the ServletContext.
	 */
	public static boolean includeAbsolute(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
		return includeAbsolute(request.getSession().getServletContext(), request, response, page);
	}


	/**
	 * Include page which path relative to the root of the ServletContext.
	 */
	public static boolean includeAbsolute(ServletContext context, ServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
		RequestDispatcher dispatcher = context.getRequestDispatcher(page);
		if (dispatcher != null) {
			dispatcher.include(request, response);
			return true;
		}
		return false;
	}

	// ---------------------------------------------------------------- forward

	/**
	 * Forward to page which path is relative to the current HTTP request.
	 */
	public static boolean forward(ServletRequest request, ServletResponse response, String page) throws IOException, ServletException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		if (dispatcher != null) {
			dispatcher.forward(request, response);
			return true;
		}
		return false;
	}

	/**
	 * Forward to named resource.
	 */
	public static boolean forwardNamed(HttpServletRequest request, ServletResponse response, String resource) throws IOException, ServletException {
		return forwardNamed(request.getSession().getServletContext(), request, response, resource);
	}

	/**
	 * Forward to named resource.
	 */
	public static boolean forwardNamed(ServletContext context, ServletRequest request, ServletResponse response, String resource) throws IOException, ServletException {
		RequestDispatcher dispatcher = context.getNamedDispatcher(resource);
		if (dispatcher != null) {
			dispatcher.forward(request, response);
			return true;
		}
		return false;
	}

	/**
	 * Forward to page path relative to the root of the ServletContext.
	 */
	public static boolean forwardAbsolute(HttpServletRequest request, ServletResponse response, String page) throws IOException, ServletException {
		return forwardAbsolute(request.getSession().getServletContext(), request, response, page);
	}

	/**
	 * Forward to page path relative to the root of the ServletContext.
	 */
	public static boolean forwardAbsolute(ServletContext context, ServletRequest request, ServletResponse response, String resource) throws IOException, ServletException {
		RequestDispatcher dispatcher = context.getRequestDispatcher(resource);
		if (dispatcher != null) {
			dispatcher.forward(request, response);
			return true;
		}
		return false;
	}

	// ---------------------------------------------------------------- redirect


	/**
	 * Performs redirection (302) to specified url.
	 */
	public static void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		if (url.startsWith(StringPool.SLASH)) {
			url = ServletUtil.getContextPath(request) + url;
		}
		response.sendRedirect(response.encodeRedirectURL(url));
	}

	/**
	 * Performs permanent redirection (301) to specified url.
	 */
	public static void redirectPermanent(HttpServletRequest request, HttpServletResponse response, String url) {
		if (url.startsWith(StringPool.SLASH)) {
			url = ServletUtil.getContextPath(request) + url;
		}
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", url);
	}


	// ---------------------------------------------------------------- full urls

	/**
	 * Returns full URL: uri + query string, including the context path.
	 */
	public static String getFullUrl(HttpServletRequest request) {
		String url = request.getRequestURI();
		String query = request.getQueryString();
		if ((query != null) && (query.length() != 0)) {
			url += '?' + query;
		}
		return url;
	}

	/**
	 * Returns url, without context path, convenient for request dispatcher.
	 */
	public static String getUrl(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		String query = request.getQueryString();
		if ((query != null) && (query.length() != 0)) {
			servletPath += '?' + query;
		}
		return servletPath;
	}


	// ---------------------------------------------------------------- include

	public static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
	public static final String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
	public static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
	public static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
	public static final String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";


	public static String getIncludeRequestUri(HttpServletRequest request) {
		return (String) request.getAttribute(INCLUDE_REQUEST_URI);
	}

	public static String getIncludeContextPath(HttpServletRequest request) {
		return (String) request.getAttribute(INCLUDE_CONTEXT_PATH);
	}

	public static String getIncludeServletPath(HttpServletRequest request) {
		return (String) request.getAttribute(INCLUDE_SERVLET_PATH);
	}

	public static String getIncludePathInfo(HttpServletRequest request) {
		return (String) request.getAttribute(INCLUDE_PATH_INFO);
	}

	public static String getIncludeQueryString(HttpServletRequest request) {
		return (String) request.getAttribute(INCLUDE_QUERY_STRING);
	}

	// ---------------------------------------------------------------- forward

	public static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
	public static final String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
	public static final String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
	public static final String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
	public static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";

	public static String getForwardRequestUri(HttpServletRequest request) {
		return (String) request.getAttribute(FORWARD_REQUEST_URI);
	}

	public static String getForwardContextPath(HttpServletRequest request) {
		return (String) request.getAttribute(FORWARD_CONTEXT_PATH);
	}

	public static String getForwardServletPath(HttpServletRequest request) {
		return (String) request.getAttribute(FORWARD_SERVLET_PATH);
	}

	public static String getForwardPathInfo(HttpServletRequest request) {
		return (String) request.getAttribute(FORWARD_PATH_INFO);
	}

	public static String getForwardQueryString(HttpServletRequest request) {
		return (String) request.getAttribute(FORWARD_QUERY_STRING);
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Returns <code>true</code> if current page is included.
	 */
	public static boolean isPageIncluded(HttpServletRequest request, HttpServletResponse response) {
		return (response.isCommitted() || (getIncludeServletPath(request) != null));
	}

	/**
	 * Returns <code>true</code> if request is a top-level one, i.e. previously
	 * not included or forwarded.
	 */
	public static boolean isTopLevelRequest(HttpServletRequest request) {
		return (getForwardRequestUri(request) == null) && (getIncludeRequestUri(request) == null);
	}

	// ---------------------------------------------------------------- smarter info

	/**
	 * Returns the base (top-level) uri.
	 */
	public static String getBaseRequestUri(HttpServletRequest request) {
		String result = getForwardRequestUri(request);
		if (result == null) {
			result = request.getRequestURI();
		}
		return result;
	}
	/**
	 * Get current request uri.
	 */
	public static String getRequestUri(HttpServletRequest request) {
		String result = getIncludeRequestUri(request);
		if (result == null) {
			result = request.getRequestURI();
		}
		return result;
	}

	public static String getBaseContextPath(HttpServletRequest request) {
		String result = getForwardContextPath(request);
		if (result == null) {
			result = ServletUtil.getContextPath(request);
		}
		return result;
	}
	public static String getContextPath(HttpServletRequest request) {
		String result = getIncludeContextPath(request);
		if (result == null) {
			result = ServletUtil.getContextPath(request);
		}
		return result;
	}


	public static String getBaseServletPath(HttpServletRequest request) {
		String result = getForwardServletPath(request);
		if (result == null) {
			result = request.getServletPath();
		}
		return result;
	}
	public static String getServletPath(HttpServletRequest request) {
		String result = getIncludeServletPath(request);
		if (result == null) {
			result = request.getServletPath();
		}
		return result;
	}


	public static String getBasePathInfo(HttpServletRequest request) {
		String result = getForwardPathInfo(request);
		if (result == null) {
			result = request.getPathInfo();
		}
		return result;
	}
	public static String getPathInfo(HttpServletRequest request) {
		String result = getIncludePathInfo(request);
		if (result == null) {
			result = request.getPathInfo();
		}
		return result;
	}


	public static String getBaseQueryString(HttpServletRequest request) {
		String result = getForwardQueryString(request);
		if (result == null) {
			result = request.getQueryString();
		}
		return result;
	}
	public static String getQueryString(HttpServletRequest request) {
		String result = getIncludeQueryString(request);
		if (result == null) {
			result = request.getQueryString();
		}
		return result;
	}
}
