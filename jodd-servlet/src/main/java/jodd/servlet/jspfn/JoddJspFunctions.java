// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.jspfn;

import jodd.JoddDefault;
import jodd.datetime.JDateTime;
import jodd.servlet.ServletUtil;
import jodd.servlet.URLBuilder;
import jodd.servlet.URLCoder;
import jodd.util.StringUtil;
import jodd.util.ObjectUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import static jodd.util.StringPool.EMPTY;

/**
 * Some JSP functions. Mainly delegates to other utilities,
 * just performs some input checking.
 */
public class JoddJspFunctions {

	private static final String CTX_VAR_NAME = "CTX";

	/**
	 * Performs page initialization. The following is done:
	 * <ul>
	 * <li>PageContextThreadLocal is set</li>
	 * <li>CTX request attribute is set with context path value</li>
	 * <li>CTX context attribute is set with context path value</li>
	 * </ul>
	 */
	public static void initPage(PageContext pageContext) {
		ServletUtil.storePageContextInThread(pageContext);
		ServletUtil.storeContextPath(pageContext, CTX_VAR_NAME);
	}


	// ---------------------------------------------------------------- string manipulation

	public static String toUpperCase(String input) {
		if (input == null) {
			return EMPTY;
		}
		return input.toUpperCase();
	}

	public static String toLowerCase(String input) {
		if (input == null) {
			return EMPTY;
		}
		return input.toLowerCase();
	}

	public static String replace(String input, String substringBefore, String substringAfter) {
		if (input == null) {
			input = EMPTY;
		}
		if (input.length() == 0) {
			return EMPTY;
		}
		if (substringBefore == null) {
			substringBefore = EMPTY;
		}
		if (substringBefore.length() == 0) {
			return input;
		}
		return StringUtil.replace(input, substringBefore, substringAfter);
	}

	// ---------------------------------------------------------------- substrings

	public static String substring(String input, int beginIndex, int endIndex) {
		if (input == null) {
			input = EMPTY;
		}
		int length = input.length();

		if (beginIndex >= length) {
			return EMPTY;
		}
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		if (endIndex < 0 || endIndex > input.length()) {
			endIndex = input.length();
		}
		if (endIndex < beginIndex) {
			return EMPTY;
		}
		return input.substring(beginIndex, endIndex);
	}

	// ---------------------------------------------------------------- collections

	/**
	 * Returns the length of provided object (collection, array and so on).
	 * If object doesn't have a length, exception is thrown.
	 */
	public static int length(Object obj) {
		int result = ObjectUtil.length(obj);
		if (result == -1) {
			throw new IllegalArgumentException("No length");
		}
		return result;
	}

	// ---------------------------------------------------------------- test

	/**
	 * Tests condition and returns <code>true</code> of <code>false</code> value.
	 * Works like ternary operator.
	 */
	public static Object test(boolean condition, Object trueValue, Object falseValue) {
		return condition ? trueValue : falseValue;
	}


	// ---------------------------------------------------------------- datetime

	/**
	 * Formats JDateTime.
	 */
	public static String fmtTime(JDateTime jdt, String format) {
		return jdt.toString(format);
	}

	// ---------------------------------------------------------------- url

	public static String url1(String value) {
		return url(value, ServletUtil.getPageContextFromThread());
	}
	public static String url(String value, PageContext pageContext) {
		return url(value, JoddDefault.encoding, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
	}
	public static String url(String value, String encoding, PageContext pageContext) {
		return url(value, encoding, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
	}
	public static String url(String value, HttpServletRequest request, HttpServletResponse response) {
		return url(value, JoddDefault.encoding, request, response);
	}
	public static String url(String value, String encoding, HttpServletRequest request, HttpServletResponse response) {
		String result = ServletUtil.resolveUrl(URLCoder.url(value, encoding), request);
		if (ServletUtil.isAbsoluteUrl(result) == false) {
			result = response.encodeURL(result);        // rewrite relative URLs
		}
		return result;
	}

	public static URLBuilder build(PageContext pageContext) {
		return new URLBuilder((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), JoddDefault.encoding);
	}

	public static URLBuilder build(HttpServletRequest request, HttpServletResponse response) {
		return new URLBuilder(request, response, JoddDefault.encoding);
	}

}