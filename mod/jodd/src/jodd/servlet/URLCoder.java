// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.StringPool;
import jodd.JoddDefault;

import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Encodes URLs better, significantly faster and more convenient.
 * This encoder handles path and queries differently, as
 * defined by specification!
 */
public class URLCoder {

	protected static final char[][] URL_CHARS = new char[256][];
	protected static final char[][] URI_CHARS = new char[128][];

	static {
		for (char c = 0; c < URL_CHARS.length; c++) {
			try {
				URL_CHARS[c] = URLEncoder.encode(String.valueOf(c), StringPool.ISO_8859_1).toCharArray();
			} catch (UnsupportedEncodingException ueex) {
				ueex.printStackTrace();
			}
			if (c < URI_CHARS.length) {
				try {
					URI uri = new URI("a", "", '/' + String.valueOf(c), null, null);
					URI_CHARS[c] = uri.toString().substring(5).toCharArray();
				} catch (URISyntaxException usex) {
					usex.printStackTrace();
				}
			}
		}
	}

	public static String url1(String value) {
		return url(value, PageContextThreadLocal.get());
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
		String result = ServletUtil.resolveUrl(url(value, encoding), request);
		if (ServletUtil.isAbsoluteUrl(result) == false) {
			result = response.encodeURL(result);        // rewrite relative URLs
		}
		return result;
	}

	public static String url(String url) {
		return url(url, JoddDefault.encoding);
	}

	/**
	 * Faster smart URL encoding. URL is parsed after the '?' sign.
	 * Both parameter name and values are parsed. This method is not 100% correct:
	 * it can't make a difference between <code>'&'</code> char in parameter value and
	 * <code>'&'</code> used as a delimiter. For more precise version,
	 * use {@link #build()}.
	 */
	public static String url(String url, String encoding) {
		int paramNdx = url.indexOf('?');
		if (paramNdx == -1) {
			return encodePath(url);
		}
		StringBuilder result = new StringBuilder(url.length() >> 1);
		appendPath(result, url.substring(0, paramNdx));
		result.append('?');
		paramNdx++;

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
				appendQuery(result, name, encoding);
				result.append('=');
				String value = q.substring(eqNdx + 1);
				if (value.length() > 0) {
					appendQuery(result, value, encoding);
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

	// ---------------------------------------------------------------- query

	protected static void appendQuery(StringBuilder result, String value, String encoding) {
		byte[] bytes;
		try {
			bytes = value.getBytes(encoding);
		} catch (UnsupportedEncodingException ueex) {
			throw new IllegalArgumentException(ueex.toString());
		}
		for (byte b : bytes) {
			int i = b & 0xFF;
			result.append(URL_CHARS[i]);
		}
	}

	/**
	 * Encodes <b>query</b> part of the URL.
	 */
	public static String encodeQuery(String value, String encoding) {
		StringBuilder sb = new StringBuilder(value.length());
		appendQuery(sb, value, encoding);
		return sb.toString();
	}

	/**
	 * Encodes <b>query</b> part of the URL.
	 */
	public static String encodeQuery(String value) {
		return encodeQuery(value, JoddDefault.encoding);
	}

	// ---------------------------------------------------------------- path

	protected static void appendPath(StringBuilder result, String value) {
		int len = value.length();

		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
			if (c < 128) {
				result.append(URI_CHARS[c]);
			} else {
				quoteNon7bit(result, c);
			}
		}
	}


	/**
	 * Encodes <b>path</b> part of the URL.
	 */
	public static String encodePath(String value) {
		StringBuilder sb = new StringBuilder(value.length());
		appendPath(sb, value);
		return sb.toString();
	}


	// ---------------------------------------------------------------- build url

	public static URLBuilder build() {
		return new URLBuilder(null, null, JoddDefault.encoding);
	}

	public static URLBuilder build(String path) {
		return build().path(path);
	}

	public static URLBuilder build(PageContext pageContext) {
		return new URLBuilder((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), JoddDefault.encoding);
	}

	public static URLBuilder build(HttpServletRequest request, HttpServletResponse response) {
		return new URLBuilder(request, response, JoddDefault.encoding);
	}

	// ---------------------------------------------------------------- util

	private static void quoteNon7bit(StringBuilder dest, char c) {
		if ((Character.isSpaceChar(c) || Character.isISOControl(c))) {
			appendEncoded(dest, c);
		} else {
			dest.append(c);
		}
	}

	private static void appendEncoded(StringBuilder sb, char c) {
		byte[] bytes;
		try {
			bytes = String.valueOf(c).getBytes("UTF-8");
		} catch (UnsupportedEncodingException ignore) {
			return;
		}
		for (byte b : bytes) {
			int i = b & 0xFF;
			if (i >= 0x80) {
				sb.append('%');
				sb.append(HEX_DIGITS[i >> 4]);
				sb.append(HEX_DIGITS[i & 0x0F]);
			} else {
				sb.append((char) i);
			}
		}
	}

	private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
}
