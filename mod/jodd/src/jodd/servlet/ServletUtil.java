// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jodd.exception.UncheckedException;
import jodd.io.StreamUtil;
import jodd.util.Base64;
import jodd.util.StringPool;
import jodd.util.MimeTypes;
import jodd.io.FileNameUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.FileUpload;
import jodd.util.StringUtil;

/**
 * Miscellaneous servlet utilities.
 */
public class ServletUtil {

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String TYPE_MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	private static final String JAVAX_SERVLET_ERROR_EXCEPTION = "javax.servlet.error.exception";

	public static final String METHOD_GET 		= "GET";
	public static final String METHOD_HEAD 		= "HEAD";
	public static final String METHOD_PUT 		= "PUT";
	public static final String METHOD_POST 		= "POST";
	public static final String METHOD_DELETE 	= "DELETE";
	public static final String METHOD_OPTIONS 	= "OPTIONS";
	public static final String METHOD_TRACE 	= "TRACE";
	public static final String METHOD_CONNECT 	= "CONNECT";

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
	 * Sets the "Vary response header" to User-Agent to indicate that the page content
	 * varies depending on which user agent (browser) is being used.
	 */
	public static void setBrowserVary(HttpServletResponse response) {
		response.setHeader( "Vary", "User-Agent");
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

	// ---------------------------------------------------------------- content disposition

	/**
	 * Prepares response for file download. Mime type and size is resolved from the file.
	 */
	public static void prepareDownload(HttpServletResponse response, File file) {
		prepareDownload(response, file, null);
	}

	/**
	 * Prepares response for file download with specified mime type.
	 */
	public static void prepareDownload(HttpServletResponse response, File file, String mimeType) {
		if (file.exists() == false) {
			throw new IllegalArgumentException("File not found: " + file);
		}
		if (file.length() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("File too big: " + file);
		}
		prepareDownload(response, file.getAbsolutePath(), mimeType, (int) file.length());
	}

	/**
	 * Prepares response for file download. Resolves mime type from file name extension.
	 */
	public static void prepareDownload(HttpServletResponse response, String fileName, int fileSize) {
		prepareDownload(response, fileName, null, fileSize);
	}

	/**
	 * Prepares response for file download.
	 *
	 * @param response http response
	 * @param fileName file name, if full path then file name will be stripped, if null, will be ignored.
	 * @param mimeType may be <code>null</code>
	 * @param fileSize if less then 0 will be ignored
	 */
	public static void prepareDownload(HttpServletResponse response, String fileName, String mimeType, int fileSize) {
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

		if (fileName != null) {
			String name = FileNameUtil.getName(fileName);
			response.setHeader(CONTENT_DISPOSITION,"attachment;filename=\"" + name + '\"');
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
		ArrayList<Cookie> list = new ArrayList<Cookie>(cookies.length);
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


	// ---------------------------------------------------------------- request/session

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
	 * @see #getContextPath(javax.servlet.ServletContext)
	 */
	public static String getContextPath() {
		return getContextPath(PageContextThreadLocal.get());
	}

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
	 * Transfers attributes from provided map to the request.
	 */
	public static void setRequestAttributes(ServletRequest request, Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns a new map containing request attributes.
	 */
	public static Map<String, Object> getRequestAttributes(ServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		loadRequestAttributes(map, request);
		return map;
	}

	/**
	 * Loads existing map with request attributes.
	 */
	public static void loadRequestAttributes(Map<String, Object> map, ServletRequest request) {
		Enumeration names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, request.getAttribute(name));
		}
	}

	/**
	 * Returns a new map containing request parameters. Request parameter may
	 * be either String or String[].
	 * @see #getRequestParameter(ServletRequest, String)
	 */
	public static Map<String, Object> getRequestParameters(ServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		loadRequestParameters(map, request);
		return map;
	}

	public static void loadRequestParameters(Map<String, Object> map, ServletRequest request) {
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, getRequestParameter(request, name));
		}
	}

	/**
	 * Loads session attributes into a map.
	 */
	public static void loadSessionAttributes(Map<String, Object> destination, HttpSession session) {
		Enumeration names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			destination.put(name, session.getAttribute(name));
		}
	}

	public static Map<String, Object> getSessionAttributes(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		loadSessionAttributes(map, session);
		return map;
	}

	public static void setSessionAttributes(HttpSession session, Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			session.setAttribute(entry.getKey(), entry.getValue());
		}
	}


	// ---------------------------------------------------------------- get value

	/**
	 * Returns non-<code>null</code> attribute value. Scopes are examined in the
	 * following order: page, request, session, application.
	 */
	public static Object attrValue(PageContext pageContext, String name) {
		Object value = pageContext.getAttribute(name);
		if (value != null) {
			return value;
		}
		return attrValue((HttpServletRequest) pageContext.getRequest(), name);
	}
	/**
	 * Returns non-<code>null</code> attribute value. Scopes are examined in the
	 * following order: request, session, application.
	 */
	public static Object attrValue(HttpServletRequest request, String name) {
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
	 * Returns non-null value of property/attribute. Scopes are examined in the following
	 * order: page (if exist), request, request parameters, session, application.
	 */
	public static Object value(PageContext pageContext, String name) {
		Object value = pageContext.getAttribute(name);
		if (value != null) {
			return value;
		}
		return value((HttpServletRequest) pageContext.getRequest(), name);
	}
	
	/**
	 * Returns non-null value of property/attribute. Scopes are examined in the following
	 * order: page (if exist), request, request parameters, session, application.
	 */
	public static Object value(HttpServletRequest request, String name) {
		Object value = request.getAttribute(name);
		if (value != null) {
			return value;
		}
		value = request.getParameter(name);
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
	 * Sets scope attribute.
	 */
	public static void setScopeAttribute(String name, Object value, String scope, PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String scopeValue = scope != null ? scope.toLowerCase() : SCOPE_PAGE;
		if (scopeValue.equals(SCOPE_PAGE)) {
			pageContext.setAttribute(name, value);
		} else if (scopeValue.equals(SCOPE_REQUEST)) {
			request.setAttribute(name, value);
		} else if (scopeValue.equals(SCOPE_SESSION)) {
			request.getSession().setAttribute(name, value);
		} else if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().setAttribute(name, value);
        } else {
			throw new UncheckedException("Invalid scope: " + scope);
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
		} else if (scopeValue.equals(SCOPE_REQUEST)) {
			request.removeAttribute(name);
		} else if (scopeValue.equals(SCOPE_SESSION)) {
			request.getSession().removeAttribute(name);
		} else if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().removeAttribute(name);
        } else {
			throw new UncheckedException("Invalid scope: " + scope);
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
	public static String stripSession(String url) {
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

	public static String resolveUrl(String url, PageContext pageContext) {
		return resolveUrl(url, (HttpServletRequest) pageContext.getRequest());
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
	 * Checks if some parameter is in GET parameters.
	 */
	public boolean isGetParameter(HttpServletRequest request, String key) {
		key = URLCoder.encodeQuery(key) + '=';
		String query = request.getQueryString();
		String[] nameValuePairs = StringUtil.splitc(query, '&');
		for (String nameValuePair : nameValuePairs) {
			if (nameValuePair.startsWith(key)) {
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
	public static String[] prepareParameters(String[] paramValues, boolean trimParams, boolean treatEmptyParamsAsNull, boolean ignoreEmptyRequestParams) {
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
			if ((ignoreEmptyRequestParams == true) && (emptyCount == total)) {
				return null;
			}
		}
		return paramValues;
	}


	// ---------------------------------------------------------------- copy

	/**
	 * Copies all request parameters to attributes.
	 */
	public static void copyParamsToAttributes(HttpServletRequest servletRequest, boolean trimParams, boolean treatEmptyParamsAsNull, boolean ignoreEmptyRequestParams) {
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
		if ((servletRequest instanceof MultipartRequestWrapper) == false) {
			return;
		}
		MultipartRequestWrapper multipartRequest = (MultipartRequestWrapper) servletRequest;
		if (multipartRequest.isMultipart() == false) {
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
				case 0: result.append("\nREQUEST\n--------\n");
						enumeration = request.getAttributeNames();
						break;
				case 1: result.append("\nSESSION\n--------\n");
						enumeration = session.getAttributeNames();
						break;
				case 2: result.append("\nAPPLICATION\n-----------\n");
						enumeration = context.getAttributeNames();
						break;
				case 3:	if (pageContext == null) {
							break loop;
						}
						result.append("\nPAGE\n-----------\n");
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
