// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.JoddDefault;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Encodes URLs better, significantly faster and more convenient.
 * This encoder handles <b>path</b> and <p>queries</p> differently,
 * as defined by the specification!
 * <p>
 * There are several ways how <code>URLCoder</code> can be used.
 * <p>
 * The simplest way - but somewhat not correct (in certain usecases) -
 * is to use {@link #encodeUrl(String)} and provide full URL.
 *
 * <p>
 * The precise way would be building target URL using
 * {@link #encodePath(String)} and {@link #encodeQuery(String)} methods.
 * for each URL element. For example:
 * <code>
 * String targetUrl = encodePath("http://jodd.org") + "&" +
 * 		encodeQuery("param") + "=" + encodeQuery("value");
 * </code>
 *
 * <p>
 * However, this is not the most user-friendly way. The user-friendly way
 * is using the {@link Builder builder} class with fluent interface.
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

	/**
	 * @see #encodeUrl
	 */
	public static String encodeUrl(String url) {
		return URLCoder.encodeUrl(url, JoddDefault.encoding);
	}

	/**
	 * Faster smart URL encoding. URL is parsed after the '?' sign.
	 * Both parameter name and values are parsed.
	 * <p>
	 * <b>Note</b>: This method is NOT 100% correct: it can't make a
	 * difference between <code>'&'</code> char in parameter value and
	 * <code>'&'</code> used as a delimiter.
	 */
	public static String encodeUrl(String url, String encoding) {
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
				appendQuery(result, q, encoding);
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
				// quoteNon7bit
				if ((Character.isSpaceChar(c) || Character.isISOControl(c))) {
					appendEncoded(result, c);
				} else {
					result.append(c);
				}
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


	// ---------------------------------------------------------------- util

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

	// ---------------------------------------------------------------- builder

	/**
	 * Creates URL builder for user-friendly way of building URLs.
	 */
	public static Builder build() {
		return new Builder(JoddDefault.encoding);
	}

	public static class Builder {

		protected final StringBuilder url;
		protected final String encoding;
		protected boolean hasParams;

		public Builder(String encoding) {
			url = new StringBuilder();
			this.hasParams = false;
			this.encoding = encoding;
		}

		/**
		 * Defines path.
		 */
		public Builder path(String value) {
			if (hasParams) {
				throw new IllegalArgumentException("Path element can't come after query parameters");
			}
			appendPath(url, value);
			return this;
		}

		/**
		 * Appends new parameter to url.
		 */
		public Builder param(String name, Object value) {
			return param(name, value == null ? null : value.toString());
		}

		/**
		 * Appends new parameter to url.
		 */
		public Builder param(String name, String value) {
			url.append(hasParams ? '&' : '?');
			hasParams = true;
			appendQuery(url, name, encoding);
			if ((value != null) && (value.length() > 0)) {
				url.append('=');
				appendQuery(url, value, encoding);
			}
			return this;
		}

		public Builder param(String nameValue) {
			url.append(hasParams ? '&' : '?');

			hasParams = true;

			int eqNdx = nameValue.indexOf('=');
			String name;
			String value = null;

			if (eqNdx == -1) {
				name = nameValue;
			} else {
				name = nameValue.substring(0, eqNdx);
				value = nameValue.substring(eqNdx + 1);
			}

			appendQuery(url, name, encoding);

			if ((value != null) && (value.length() > 0)) {
				url.append('=');
				appendQuery(url, value, encoding);
			}

			return this;
		}

		/**
		 * Returns built URL.
		 */
		@Override
		public String toString() {
			return url.toString();
		}
	}

}
