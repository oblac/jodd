// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import static jodd.util.StringPool.UTF_8;
import jodd.util.StringPool;

import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Encodes URLs.
 */
public class UrlEncoder {

	protected static final char[][] URL = new char[256][];

	static {
		for (char c = 0; c < 256; c++) {
			try {
				URL[c] = URLEncoder.encode(String.valueOf(c), StringPool.ISO_8859_1).toCharArray();
			} catch (UnsupportedEncodingException ueex) {
				ueex.printStackTrace();
			}
		}
	}

	public static String url(String value, PageContext pageContext) {
		return url(value, UTF_8, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
	}
	public static String url(String value, String encoding, PageContext pageContext) {
		return url(value, encoding, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
	}
	public static String url(String value, HttpServletRequest request, HttpServletResponse response) {
		return url(value, UTF_8, request, response);
	}
	public static String url(String value, String encoding, HttpServletRequest request, HttpServletResponse response) {
		String result = ServletUtil.resolveUrl(url(value, encoding), request);
		if (ServletUtil.isAbsoluteUrl(result) == false) {
			result = response.encodeURL(result);        // rewrite relative URLs
		}
		return result;
	}

	public static String url(String url) {
		return url(url, UTF_8);
	}

	/**
	 * Faster smart URL encoding. URL is parsed after the '?' sign.
	 * Both parameter name and values are parsed. This method is not 100% correct:
	 * it can't make a difference between '&' char in parameter value and '&' used as
	 * a delimiter. For more precise version, use baseUrl.
	 */
	public static String url(String url, String encoding) {
		int paramNdx = url.indexOf('?');
		if (paramNdx == -1) {
			return url;
		}
		StringBuilder result = new StringBuilder(url.length() >> 1);
		paramNdx++;
		result.append(url.substring(0, paramNdx));
		while (true) {
			int ampNdx = url.indexOf('&', paramNdx);
			String q;
			if (ampNdx == -1) {
				q = url.substring(paramNdx);
			} else {
				q = url.substring(paramNdx, ampNdx);
			}
			int eqNdx = q.indexOf('=');
			if (eqNdx == -1) {
				result.append(q);
			} else {
				String name = q.substring(0, eqNdx);
				appendUrl(result, name, encoding);
				result.append('=');
				String value = q.substring(eqNdx + 1);
				if (value.length() > 0) {
					appendUrl(result, value, encoding);
				}
			}
			if (ampNdx == -1) {
				break;
			}
			result.append('&');
			paramNdx = ampNdx + 1;
		}
		return result.toString();
	}


	protected static void appendUrl(StringBuilder result, String value, String encoding) {
		byte[] bytes;
		try {
			bytes = value.getBytes(encoding);
		} catch (UnsupportedEncodingException ueex) {
			throw new IllegalArgumentException(ueex.toString());
		}
		for (byte b : bytes) {
			int i = b;
			if (i < 0) {
				i += 256;
			}
			result.append(URL[i]);
		}
	}

	// ---------------------------------------------------------------- build url

	public static UrlBuilder buildUrl(String url) {
		return new UrlBuilder(url, UTF_8, null, null);
	}
	public static UrlBuilder buildUrl(String url, String encoding) {
		return new UrlBuilder(url, encoding, null, null);
	}

	public static UrlBuilder buildUrl(String url, PageContext pageContext) {
		return new UrlBuilder(url, UTF_8, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
	}
	public static UrlBuilder buildUrl(String url, String encoding, PageContext pageContext) {
		return new UrlBuilder(url, encoding, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
	}

	public static UrlBuilder buildUrl(String url, HttpServletRequest request, HttpServletResponse response) {
		return new UrlBuilder(url, UTF_8, request, response);
	}
	public static UrlBuilder buildUrl(String url, String encoding, HttpServletRequest request, HttpServletResponse response) {
		return new UrlBuilder(url, encoding, request, response);
	}

	
}
