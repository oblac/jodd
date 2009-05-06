// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import java.io.IOException;
import java.io.File;
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

import jodd.util.Base64;
import jodd.util.StringPool;
import jodd.util.MimeTypes;
import jodd.io.FileNameUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.FileUpload;

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
	 * Prepares response for file download. Sets correct mime type, file name and file size.
	 */
	public static void prepareDownload(HttpServletResponse response, String fileName, int fileSize) {
		String extension = FileNameUtil.getExtension(fileName);
		String name = FileNameUtil.getName(fileName);
		String mime = MimeTypes.getMimeType(extension);
		response.setContentType(mime);
		response.setHeader(CONTENT_DISPOSITION,"attachment;filename=\"" + name + '\"');
		if (fileSize >= 0) {
			response.setContentLength(fileSize);
		}
	}

	/**
	 * Prepares response for file download.
	 */
	public static void prepareDownload(HttpServletResponse response, File file) {
		if (file.exists() == false) {
			throw new IllegalArgumentException("Unable to prepare file for download. File '" + file + "' doesn't exist.");
		}
		if (file.length() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Unable to prepare file for download. File '" + file + "' is too big.");
		}
		prepareDownload(response, file.getAbsolutePath(), (int) file.length());
	}

	// ---------------------------------------------------------------- cookie

	/**
	 * Returns cookie value from client.
	 *
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


	// ---------------------------------------------------------------- request/session


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
			return (request.getContextPath() + url);
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

	// ---------------------------------------------------------------- copy

	/**
	 * Copies all request parameters to attributes.
	 */
	public static void copyParamsToAttributes(HttpServletRequest servletRequest, boolean ignoreEmptyRequestParams) {
		Enumeration paramNames = servletRequest.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			String[] paramValues = servletRequest.getParameterValues(paramName);
			// ignore empty parameters
			if (ignoreEmptyRequestParams == true) {
				int ignoreCount = 0;
				for (int i = 0; i < paramValues.length; i++) {
					if (paramValues[i].length() == 0) {
						paramValues[i] = null;
						ignoreCount++;
					}
				}
				if (ignoreCount == paramValues.length) {
					continue;	// ignore null parameters
				}
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
	 * Returns a string with debug info from all servlet objects, including pageScope.
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
				for (Object v : value) {
					result.append(v).append(',');
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
					result.append(value).append('\n');
				}
			}
		}
		return result.toString();
	}

}
