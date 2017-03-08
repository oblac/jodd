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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jodd.io.StreamUtil;
import jodd.util.Base64;
import jodd.util.StringPool;
import jodd.util.MimeTypes;
import jodd.io.FileNameUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.MultipartRequest;
import jodd.upload.FileUpload;
import jodd.util.StringUtil;
import jodd.util.URLCoder;

/**
 * Miscellaneous servlet utilities.
 */
public class ServletUtil {

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String TYPE_MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	private static final String JAVAX_SERVLET_ERROR_EXCEPTION = "javax.servlet.error.exception";

	private static final String SCOPE_APPLICATION = "application";
	private static final String SCOPE_SESSION = "session";
	private static final String SCOPE_REQUEST = "request";
	private static final String SCOPE_PAGE = "page";


	// ---------------------------------------------------------------- multi-part

	/**
	 * Returns <code>true</code> if a request is multi-part request.
	 */
	public static boolean isMultipartRequest(HttpServletRequest request) {
		String type = request.getHeader(HEADER_CONTENT_TYPE);
		return (type != null) && type.startsWith(TYPE_MULTIPART_FORM_DATA);
	}

	/**
	 * Returns <code>true</code> if client supports gzip encoding.
	 */
	public static boolean isGzipSupported(HttpServletRequest request) {
		String browserEncodings = request.getHeader(HEADER_ACCEPT_ENCODING);
		return (browserEncodings != null) && (browserEncodings.contains("gzip"));
	}

	// ---------------------------------------------------------------- authorization
	/**
	 * Decodes the "Authorization" header and retrieves the
	 * user's name from it.  Returns <code>null</code> if the header is not present.
	 */
	public static String getAuthUsername(HttpServletRequest request) {
		String header = request.getHeader(HEADER_AUTHORIZATION);
		if (header == null) {
			return null;
		}
		String encoded = header.substring(header.indexOf(' ') + 1);
		String decoded = new String(Base64.decode(encoded));
		return decoded.substring(0, decoded.indexOf(':'));
	}

	/**
	 * Decodes the "Authorization" header and retrieves the
	 * password from it. Returns <code>null</code> if the header is not present.
	 */
	public static String getAuthPassword(HttpServletRequest request) {
		String header = request.getHeader(HEADER_AUTHORIZATION);
		if (header == null) {
			return null;
		}
		String encoded = header.substring(header.indexOf(' ') + 1);
		String decoded = new String(Base64.decode(encoded));
		return decoded.substring(decoded.indexOf(':') + 1);
	}

	/**
	 * Sends correct headers to require basic authentication for the given realm.
	 */
	public static void requireAuthentication(HttpServletResponse resp, String realm) throws IOException {
		resp.setHeader(WWW_AUTHENTICATE, "Basic realm=\"" + realm + '\"');
		resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	// ---------------------------------------------------------------- download and content disposition

	/**
	 * Prepares response for file download. Mime type and size is resolved from the file.
	 */
	public static void prepareDownload(HttpServletResponse response, File file) {
		prepareDownload(response, file, null);
	}

	/**
	 * Prepares response for file download with provided mime type.
	 */
	public static void prepareDownload(HttpServletResponse response, File file, String mimeType) {
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found: " + file);
		}
		if (file.length() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("File too big: " + file);
		}
		prepareResponse(response, file.getAbsolutePath(), mimeType, (int) file.length());
	}

	/**
	 * Prepares response for various provided data.
	 *
	 * @param response http response
	 * @param fileName file name, if full path then file name will be stripped, if null, will be ignored.
	 * @param mimeType mime type with optional charset, may be <code>null</code>
	 * @param fileSize if less then 0 it will be ignored
	 */
	public static void prepareResponse(HttpServletResponse response, String fileName, String mimeType, int fileSize) {
		if ((mimeType == null) && (fileName != null)) {
			String extension = FileNameUtil.getExtension(fileName);
			mimeType = MimeTypes.getMimeType(extension);
		}

		if (mimeType != null) {
			response.setContentType(mimeType);
		}

		if (fileSize >= 0) {
			response.setContentLength(fileSize);
		}

		// support internationalization
		// See https://tools.ietf.org/html/rfc6266#section-5 for more information.
		if (fileName != null) {
			String name = FileNameUtil.getName(fileName);
			String encodedFileName = URLCoder.encode(name);

			response.setHeader(CONTENT_DISPOSITION,
				"attachment;filename=\"" + name + "\";filename*=utf8''" + encodedFileName);
		}
	}

	// ---------------------------------------------------------------- cookie

	/**
	 * Finds and returns cookie from client by its name.
	 * Only the first cookie is returned.
	 * @see #getAllCookies(javax.servlet.http.HttpServletRequest, String)
	 * @return cookie value or <code>null</code> if cookie with specified name doesn't exist.
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * Returns all cookies from client that matches provided name.
	 * @see #getCookie(javax.servlet.http.HttpServletRequest, String) 
	 */
	public static Cookie[] getAllCookies(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		ArrayList<Cookie> list = new ArrayList<>(cookies.length);
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				list.add(cookie);
			}
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.toArray(new Cookie[list.size()]);
	}

	// ---------------------------------------------------------------- request body

	/**
	 * Reads HTTP request body. Useful only with POST requests. Once body is read,
	 * it cannot be read again!
	 */
	public static String readRequestBody(HttpServletRequest request) throws IOException {
		BufferedReader buff = request.getReader();
		StringWriter out = new StringWriter();
		StreamUtil.copy(buff, out);
		return out.toString();
	}


	// ---------------------------------------------------------------- context path

	/**
	 * Returns correct context path, as by Servlet definition. Different
	 * application servers return all variants: "", null, "/".
	 * <p>
	 * The context path always comes first in a request URI. The path
	 * starts with a "/" character but does not end with a "/" character.
	 * For servlets in the default (root) context, this method returns "".
	 */
	public static String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (contextPath == null || contextPath.equals(StringPool.SLASH)) {
			contextPath = StringPool.EMPTY;
		}
		return contextPath;
	}

	/**
	 * Returns correct context path, as by Servlet definition. Different
	 * application servers return all variants: "", null, "/".
	 * <p>
	 * The context path always comes first in a request URI. The path
	 * starts with a "/" character but does not end with a "/" character.
	 * For servlets in the default (root) context, this method returns "".
	 */
	public static String getContextPath(ServletContext servletContext) {
		String contextPath = servletContext.getContextPath();
		if (contextPath == null || contextPath.equals(StringPool.SLASH)) {
			contextPath = StringPool.EMPTY;
		}
		return contextPath;
	}

	/**
	 * @see #getContextPath(javax.servlet.ServletContext)
	 */
	public static String getContextPath(PageContext pageContext) {
		return getContextPath(pageContext.getServletContext());
	}

	/**
	 * Stores context path in server context and request scope.
	 */
	public static void storeContextPath(PageContext pageContext, String contextPathVariableName) {
		String ctxPath = getContextPath(pageContext);

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		request.setAttribute(contextPathVariableName, ctxPath);

		ServletContext servletContext = pageContext.getServletContext();
		servletContext.setAttribute(contextPathVariableName, ctxPath);
	}

	/**
	 * Stores context path in page context and request scope.
	 */
	public static void storeContextPath(ServletContext servletContext, String contextPathVariableName) {
		String ctxPath = getContextPath(servletContext);

		servletContext.setAttribute(contextPathVariableName, ctxPath);
	}

	// ---------------------------------------------------------------- attributes and values

	/**
	 * Returns non-<code>null</code> attribute value. Scopes are examined in the
	 * following order: page, request, session, application.
	 */
	public static Object attribute(PageContext pageContext, String name) {
		Object value = pageContext.getAttribute(name);
		if (value != null) {
			return value;
		}
		return attribute((HttpServletRequest) pageContext.getRequest(), name);
	}
	/**
	 * Returns non-<code>null</code> attribute value. Scopes are examined in the
	 * following order: request, session, application.
	 */
	public static Object attribute(HttpServletRequest request, String name) {
		Object value = request.getAttribute(name);
		if (value != null) {
			return value;
		}
		value = request.getSession().getAttribute(name);
		if (value != null) {
			return value;
		}
		return request.getSession().getServletContext().getAttribute(name);
	}

	/**
	 * Returns value of property/attribute. The following value sets are looked up:
	 * <ul>
	 *     <li>page context attributes</li>
	 *     <li>request attributes</li>
	 *     <li>request parameters (multi-part request detected)</li>
	 *     <li>session attributes</li>
	 *     <li>context attributes</li>
	 * </ul>
	 */
	public static Object value(PageContext pageContext, String name) {
		Object value = pageContext.getAttribute(name);
		if (value != null) {
			return value;
		}
		return value((HttpServletRequest) pageContext.getRequest(), name);
	}

	/**
	 * Returns value of property/attribute. The following value sets are looked up:
	 * <ul>
	 *     <li>request attributes</li>
	 *     <li>request parameters (multi-part request detected)</li>
	 *     <li>session attributes</li>
	 *     <li>context attributes</li>
	 * </ul>
	 */
	public static Object value(HttpServletRequest request, String name) {
		Object value = request.getAttribute(name);
		if (value != null) {
			return value;
		}

		if (isMultipartRequest(request)) {
			try {
				MultipartRequest multipartRequest = MultipartRequest.getInstance(request);
				value = multipartRequest.getParameter(name);
			} catch (IOException ignore) {
			}
		}
		else {
			String[] params = request.getParameterValues(name);
			if (params != null) {
				if (params.length == 1) {
					value = params[0];
				} else {
					value = params;
				}
			}
		}
		if (value != null) {
			return value;
		}

		value = request.getSession().getAttribute(name);
		if (value != null) {
			return value;
		}
		return request.getSession().getServletContext().getAttribute(name);
	}

	// ---------------------------------------------------------------- scope attributes

	/**
	 * Sets scope attribute.
	 */
	public static void setScopeAttribute(String name, Object value, String scope, PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String scopeValue = scope != null ? scope.toLowerCase() : SCOPE_PAGE;
		if (scopeValue.equals(SCOPE_PAGE)) {
			pageContext.setAttribute(name, value);
		}
		else if (scopeValue.equals(SCOPE_REQUEST)) {
			request.setAttribute(name, value);
		}
		else if (scopeValue.equals(SCOPE_SESSION)) {
			request.getSession().setAttribute(name, value);
		}
		else if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().setAttribute(name, value);
        }
		else {
			throw new IllegalArgumentException("Invalid scope: " + scope);
        }
	}

	/**
	 * Removes scope attribute.
	 */
	public static void removeScopeAttribute(String name, String scope, PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String scopeValue = scope != null ? scope.toLowerCase() : SCOPE_PAGE;
		if (scopeValue.equals(SCOPE_PAGE)) {
			pageContext.removeAttribute(name);
		}
		else if (scopeValue.equals(SCOPE_REQUEST)) {
			request.removeAttribute(name);
		}
		else if (scopeValue.equals(SCOPE_SESSION)) {
			request.getSession().removeAttribute(name);
		}
		else if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().removeAttribute(name);
        }
		else {
			throw new IllegalArgumentException("Invalid scope: " + scope);
        }
	}

	// ---------------------------------------------------------------- resolve URL

	/**
	 * Valid characters in a scheme, as specified by RFC 1738.
	 */
	public static final String VALID_SCHEME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";

	/**
     * Returns <code>true</code> if current URL is absolute, <code>false</code> otherwise.
     */
    public static boolean isAbsoluteUrl(String url) {
	    if (url == null) {      	    // a null URL is not absolute
		    return false;
	    }
	    int colonPos;                   // fast simple check first
	    if ((colonPos = url.indexOf(':')) == -1) {
		    return false;
	    }

	    // if we DO have a colon, make sure that every character
	    // leading up to it is a valid scheme character
	    for (int i = 0; i < colonPos; i++) {
		    if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) == -1) {
			    return false;
		    }
	    }
	    return true;
    }

	/**
	 * Strips a servlet session ID from <code>url</code>.  The session ID
	 * is encoded as a URL "path parameter" beginning with "jsessionid=".
	 * We thus remove anything we find between ";jsessionid=" (inclusive)
	 * and either EOS or a subsequent ';' (exclusive).
	 */
	public static String stripSessionId(String url) {
		StringBuilder u = new StringBuilder(url);
		int sessionStart;
		while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {
			int sessionEnd = u.toString().indexOf(';', sessionStart + 1);
			if (sessionEnd == -1) {
				sessionEnd = u.toString().indexOf('?', sessionStart + 1);
			}
			if (sessionEnd == -1) {
				sessionEnd = u.length();
			}
			u.delete(sessionStart, sessionEnd);
		}
		return u.toString();
	}

	public static String resolveUrl(String url, HttpServletRequest request) {
		if (isAbsoluteUrl(url)) {
			return url;
		}
		if (url.startsWith(StringPool.SLASH)) {
			return getContextPath(request) + url;
		} else {
			return url;
		}
	}

	public static String resolveUrl(String url, String context) {
		if (isAbsoluteUrl(url)) {
			return url;
		}
		if (!context.startsWith(StringPool.SLASH) || !url.startsWith(StringPool.SLASH)) {
			throw new IllegalArgumentException("Values of both 'context' and 'url' must start with '/'.");
		}
		if (context.equals(StringPool.SLASH)) {
			return url;
		} else {
			return (context + url);
		}
	}

	// ---------------------------------------------------------------- params

	/**
	 * Returns HTTP request parameter as String or String[].
	 */
	public static Object getRequestParameter(ServletRequest request, String name) {
		String[] values = request.getParameterValues(name);
		if (values == null) {
			return null;
		}
		if (values.length == 1) {
			return values[0];
		}
		return values;
	}

	/**
	 * Checks if some parameter is in GET parameters.
	 */
	public boolean isGetParameter(HttpServletRequest request, String name) {
		name = URLCoder.encodeQueryParam(name) + '=';
		String query = request.getQueryString();
		String[] nameValuePairs = StringUtil.splitc(query, '&');
		for (String nameValuePair : nameValuePairs) {
			if (nameValuePair.startsWith(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prepares parameters for further processing.
	 * @param paramValues	string array of param values
	 * @param trimParams	trim parameters
	 * @param treatEmptyParamsAsNull	empty parameters should be treated as <code>null</code>
	 * @param ignoreEmptyRequestParams	if all parameters are empty, return <code>null</code>
	 */
	public static String[] prepareParameters(
			String[] paramValues,
			boolean trimParams,
			boolean treatEmptyParamsAsNull,
			boolean ignoreEmptyRequestParams) {

		if (trimParams || treatEmptyParamsAsNull || ignoreEmptyRequestParams) {
			int emptyCount = 0;
			int total = paramValues.length;
			for (int i = 0; i < paramValues.length; i++) {
				String paramValue = paramValues[i];
				if (paramValue == null) {
					emptyCount++;
					continue;
				}
				if (trimParams) {
					paramValue = paramValue.trim();
				}
				if (paramValue.length() == 0) {
					emptyCount++;
					if (treatEmptyParamsAsNull) {
						paramValue = null;
					}
				}
				paramValues[i] = paramValue;
			}
			if ((ignoreEmptyRequestParams) && (emptyCount == total)) {
				return null;
			}
		}
		return paramValues;
	}


	// ---------------------------------------------------------------- copy

	/**
	 * Copies all request parameters to attributes.
	 */
	public static void copyParamsToAttributes(
			HttpServletRequest servletRequest,
			boolean trimParams,
			boolean treatEmptyParamsAsNull,
			boolean ignoreEmptyRequestParams) {

		Enumeration paramNames = servletRequest.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			String[] paramValues = servletRequest.getParameterValues(paramName);
			paramValues = prepareParameters(paramValues, trimParams, treatEmptyParamsAsNull, ignoreEmptyRequestParams);
			if (paramValues == null) {
				continue;
			}
			servletRequest.setAttribute(paramName, paramValues.length == 1 ? paramValues[0] : paramValues);
		}

		// multipart
		if (!(servletRequest instanceof MultipartRequestWrapper)) {
			return;
		}
		MultipartRequestWrapper multipartRequest = (MultipartRequestWrapper) servletRequest;
		if (!multipartRequest.isMultipart()) {
			return;
		}
		paramNames = multipartRequest.getFileParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}
			FileUpload[] paramValues = multipartRequest.getFiles(paramName);
			servletRequest.setAttribute(paramName, paramValues.length == 1 ? paramValues[0] : paramValues);
		}
	}

	// ---------------------------------------------------------------- version

	private static boolean isVersion2_5;

	static {
		try {
			ServletContext.class.getMethod("getContextPath");
			isVersion2_5 = true;
		} catch (Exception ignore) {
		}
	}

	/**
	 * Returns <code>true</code> if current servlets version is 2.5 or higher.
	 */
	public static boolean isServletsVersion2_5() {
		return isVersion2_5;
	}

	// ---------------------------------------------------------------- errors

	/**
	 * Returns servlet error.
	 */
	public static Throwable getServletError(ServletRequest request) {
		return (Throwable) request.getAttribute(JAVAX_SERVLET_ERROR_EXCEPTION);
	}

	/**
	 * Sets servlet error.
	 */
	public static void setServletError(ServletRequest request, Throwable throwable) {
		request.setAttribute(JAVAX_SERVLET_ERROR_EXCEPTION, throwable);
	}


	// ---------------------------------------------------------------- debug

	/**
	 * Returns a string with debug info from all servlet objects.
	 * @see #debug(HttpServletRequest, PageContext)
	 */
	public static String debug(HttpServletRequest request) {
		return debug(request,  null);
	}
	/**
	 * Returns a string with debug info from all servlet objects.
	 * @see #debug(HttpServletRequest, PageContext)
	 */
	public static String debug(PageContext pageContext) {
		return debug((HttpServletRequest) pageContext.getRequest(),  pageContext);
	}

	/**
	 * Returns a string with debug info from all servlet objects, including the page context.
	 */
	protected static String debug(HttpServletRequest request, PageContext pageContext) {
		StringBuilder result = new StringBuilder();
		result.append("\nPARAMETERS\n----------\n");
		Enumeration enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			Object[] value = request.getParameterValues(name);
			result.append(name).append('=');
			if (value == null) {
				result.append("<null>");
			} else if (value.length == 1) {
				result.append(value[0]).append('\n');
			} else {
				result.append('[');
				for (int i = 0, valueLength = value.length; i < valueLength; i++) {
					if (i == 0) {
						result.append(',');
					}
					result.append(value[i]);
				}
				result.append("]\n");
			}
		}

		HttpSession session = request.getSession();
		ServletContext context = session.getServletContext();

		loop:
		for (int i = 0; i < 4; i++) {
			switch (i) {
				case 0: result.append("\nREQUEST\n-------\n");
						enumeration = request.getAttributeNames();
						break;
				case 1: result.append("\nSESSION\n-------\n");
						enumeration = session.getAttributeNames();
						break;
				case 2: result.append("\nAPPLICATION\n-----------\n");
						enumeration = context.getAttributeNames();
						break;
				case 3:	if (pageContext == null) {
							break loop;
						}
						result.append("\nPAGE\n----\n");
						enumeration = pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);
			}
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				Object value = null;
				switch (i) {
					case 0: value = request.getAttribute(name); break;
					case 1: value = session.getAttribute(name); break;
					case 2: value = context.getAttribute(name); break;
					case 3: value = pageContext.getAttribute(name); break;
				}
				result.append(name).append('=');
				if (value == null) {
					result.append("<null>\n");
				} else {
					String stringValue;
					try {
						stringValue = value.toString();
					} catch (Exception ignore) {
						stringValue = "<" + value.getClass() + ">\n";
					}
					result.append(stringValue).append('\n');
				}
			}
		}
		return result.toString();
	}

	// ---------------------------------------------------------------- cache

	/**
	 * Prevents HTTP cache.
	 */
	public static void preventCaching(HttpServletResponse response) {
		response.setHeader("Cache-Control", "max-age=0, must-revalidate, no-cache, no-store, private, post-check=0, pre-check=0");  // HTTP 1.1
		response.setHeader("Pragma","no-cache");        // HTTP 1.0
		response.setDateHeader ("Expires", 0);          // prevents caching at the proxy server
	}


}
