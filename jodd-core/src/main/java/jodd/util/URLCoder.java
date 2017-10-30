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

package jodd.util;

import jodd.core.JoddCore;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jodd.util.CharUtil.isAlpha;
import static jodd.util.CharUtil.isDigit;
import static jodd.util.CharUtil.isPchar;
import static jodd.util.CharUtil.isSubDelimiter;
import static jodd.util.CharUtil.isUnreserved;

/**
 * Encodes URLs correctly, significantly faster and more convenient.
 * <p>
 * Here is an example of full URL:
 * {@literal https://jodd:ddoj@www.jodd.org:8080/file;p=1?q=2#third}.
 * It consist of:
 * <ul>
 *     <li>scheme (https)</li>
 *     <li>user (jodd)</li>
 *     <li>password (ddoj)</li>
 *     <li>host (www.jodd.org)</li>
 *     <li>port (8080)</li>
 *     <li>path (file)</li>
 *     <li>path parameter (p=1)</li>
 *     <li>query parameter (q=2)</li>
 *     <li>fragment (third)</li>
 * </ul>
 * Each URL part has its own encoding rules. The <b>only</b> correct way of
 * encoding URLs is to encode each part separately, and then to concatenate
 * results. For easier query building you can use {@link #build(String) builder}.
 * It provides fluent interface for defining query parameters.
 */
public class URLCoder {

	private static final String SCHEME_PATTERN = "([^:/?#]+):";

	private static final String HTTP_PATTERN = "(http|https):";

	private static final String USERINFO_PATTERN = "([^@/]*)";

	private static final String HOST_PATTERN = "([^/?#:]*)";

	private static final String PORT_PATTERN = "(\\d*)";

	private static final String PATH_PATTERN = "([^?#]*)";

	private static final String QUERY_PATTERN = "([^#]*)";

	private static final String LAST_PATTERN = "(.*)";

	// Regex patterns that matches URIs. See RFC 3986, appendix B

	private static final Pattern URI_PATTERN = Pattern.compile(
			"^(" + SCHEME_PATTERN + ")?" + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN +
					")?" + ")?" + PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");

	private static final Pattern HTTP_URL_PATTERN = Pattern.compile(
			'^' + HTTP_PATTERN + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN + ")?" + ")?" +
					PATH_PATTERN + "(\\?" + LAST_PATTERN + ")?");

	/**
	 * Enumeration to identify the parts of a URI.
	 * <p>
	 * Contains methods to indicate whether a given character is valid in a specific URI component.
	 *
	 * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>
	 */
	enum URIPart {

		UNRESERVED {
			@Override
			public boolean isValid(char c) {
				return isUnreserved(c);
			}
		},

		SCHEME {
			@Override
			public boolean isValid(char c) {
				return isAlpha(c) || isDigit(c) || c == '+' || c == '-' || c == '.';
			}
		},
//		AUTHORITY {
//			@Override
//			public boolean isValid(char c) {
//				return isUnreserved(c) || isSubDelimiter(c) || c == ':' || c == '@';
//			}
//		},
		USER_INFO {
			@Override
			public boolean isValid(char c) {
				return isUnreserved(c) || isSubDelimiter(c) || c == ':';
			}
		},
		HOST {
			@Override
			public boolean isValid(char c) {
				return isUnreserved(c) || isSubDelimiter(c);
			}
		},
		PORT {
			@Override
			public boolean isValid(char c) {
				return isDigit(c);
			}
		},
		PATH {
			@Override
			public boolean isValid(char c) {
				return isPchar(c) || c == '/';
			}
		},
		PATH_SEGMENT {
			@Override
			public boolean isValid(char c) {
				return isPchar(c);
			}
		},
		QUERY {
			@Override
			public boolean isValid(char c) {
				return isPchar(c) || c == '/' || c == '?';
			}
		},
		QUERY_PARAM {
			@Override
			public boolean isValid(char c) {
				if (c == '=' || c == '+' || c == '&' || c == ';') {
					return false;
				}
				return isPchar(c) || c == '/' || c == '?';
			}
		},
		FRAGMENT {
			@Override
			public boolean isValid(char c) {
				return isPchar(c) || c == '/' || c == '?';
			}
		};

		/**
		 * Indicates whether the given character is allowed in this URI component.
		 *
		 * @return <code>true</code> if the character is allowed; {@code false} otherwise
		 */
		public abstract boolean isValid(char c);

	}


	// ---------------------------------------------------------------- util methods

	/**
	 * Encodes single URI component.
	 */
	private static String encodeUriComponent(String source, String encoding, URIPart uriPart) {
		if (source == null) {
			return null;
		}

		byte[] bytes;
		try {
			bytes = encodeBytes(source.getBytes(encoding), uriPart);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}

		char[] chars = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			chars[i] = (char) bytes[i];
		}
		return new String(chars);
	}

	/**
	 * Encodes byte array using allowed characters from {@link URIPart}.
	 */
	private static byte[] encodeBytes(byte[] source, URIPart uriPart) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length);
		for (byte b : source) {
			if (b < 0) {
				b += 256;
			}
			if (uriPart.isValid((char) b)) {
				bos.write(b);
			} else {
				bos.write('%');
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				bos.write(hex1);
				bos.write(hex2);
			}
		}
		return bos.toByteArray();
	}

	// ---------------------------------------------------------------- main methods

	/**
	 * Encodes string using default RFCP rules.
	 */
	public static String encode(String string, String encoding) {
		return encodeUriComponent(string, encoding, URIPart.UNRESERVED);
	}
	public static String encode(String string) {
		return encodeUriComponent(string, JoddCore.defaults().getEncoding(), URIPart.UNRESERVED);
	}

	/**
	 * Encodes the given URI scheme with the given encoding.
	 */
	public static String encodeScheme(String scheme, String encoding) {
		return encodeUriComponent(scheme, encoding, URIPart.SCHEME);
	}
	public static String encodeScheme(String scheme) {
		return encodeUriComponent(scheme, JoddCore.defaults().getEncoding(), URIPart.SCHEME);
	}

/*	/**
	 * Encodes the given URI authority with the given encoding.
	 *

	public static String encodeAuthority(String authority, String encoding) {
		return encodeUriComponent(authority, encoding, URIPart.AUTHORITY);
	}
	public static String encodeAuthority(String authority) {
		return encodeUriComponent(authority, JoddCore.encoding, URIPart.AUTHORITY);
	}
*/

	/**
	 * Encodes the given URI user info with the given encoding.
	 */
	public static String encodeUserInfo(String userInfo, String encoding) {
		return encodeUriComponent(userInfo, encoding, URIPart.USER_INFO);
	}
	public static String encodeUserInfo(String userInfo) {
		return encodeUriComponent(userInfo, JoddCore.defaults().getEncoding(), URIPart.USER_INFO);
	}

	/**
	 * Encodes the given URI host with the given encoding.
	 */
	public static String encodeHost(String host, String encoding) {
		return encodeUriComponent(host, encoding, URIPart.HOST);
	}
	public static String encodeHost(String host) {
		return encodeUriComponent(host, JoddCore.defaults().getEncoding(), URIPart.HOST);
	}

	/**
	 * Encodes the given URI port with the given encoding.
	 */
	public static String encodePort(String port, String encoding) {
		return encodeUriComponent(port, encoding, URIPart.PORT);
	}
	public static String encodePort(String port) {
		return encodeUriComponent(port, JoddCore.defaults().getEncoding(), URIPart.PORT);
	}

	/**
	 * Encodes the given URI path with the given encoding.
	 */
	public static String encodePath(String path, String encoding) {
		return encodeUriComponent(path, encoding, URIPart.PATH);
	}
	public static String encodePath(String path) {
		return encodeUriComponent(path, JoddCore.defaults().getEncoding(), URIPart.PATH);
	}

	/**
	 * Encodes the given URI path segment with the given encoding.
	 */
	public static String encodePathSegment(String segment, String encoding) {
		return encodeUriComponent(segment, encoding, URIPart.PATH_SEGMENT);
	}
	public static String encodePathSegment(String segment) {
		return encodeUriComponent(segment, JoddCore.defaults().getEncoding(), URIPart.PATH_SEGMENT);
	}

	/**
	 * Encodes the given URI query with the given encoding.
	 */
	public static String encodeQuery(String query, String encoding) {
		return encodeUriComponent(query, encoding, URIPart.QUERY);
	}
	public static String encodeQuery(String query) {
		return encodeUriComponent(query, JoddCore.defaults().getEncoding(), URIPart.QUERY);
	}

	/**
	 * Encodes the given URI query parameter with the given encoding.
	 */
	public static String encodeQueryParam(String queryParam, String encoding) {
		return encodeUriComponent(queryParam, encoding, URIPart.QUERY_PARAM);
	}
	public static String encodeQueryParam(String queryParam) {
		return encodeUriComponent(queryParam, JoddCore.defaults().getEncoding(), URIPart.QUERY_PARAM);
	}

	/**
	 * Encodes the given URI fragment with the given encoding.
	 */
	public static String encodeFragment(String fragment, String encoding) {
		return encodeUriComponent(fragment, encoding, URIPart.FRAGMENT);
	}
	public static String encodeFragment(String fragment) {
		return encodeUriComponent(fragment, JoddCore.defaults().getEncoding(), URIPart.FRAGMENT);
	}


	// ---------------------------------------------------------------- url

	/**
	 * @see #encodeUri(String, String)
	 */
	public static String encodeUri(String uri) {
		return encodeUri(uri, JoddCore.defaults().getEncoding());
	}
	/**
	 * Encodes the given source URI into an encoded String. All various URI components are
	 * encoded according to their respective valid character sets.
	 * <p>This method does <b>not</b> attempt to encode "=" and "{@literal &}"
	 * characters in query parameter names and query parameter values because they cannot
	 * be parsed in a reliable way.
	 */
	public static String encodeUri(String uri, String encoding) {
		Matcher m = URI_PATTERN.matcher(uri);
		if (m.matches()) {
			String scheme = m.group(2);
			String authority = m.group(3);
			String userinfo = m.group(5);
			String host = m.group(6);
			String port = m.group(8);
			String path = m.group(9);
			String query = m.group(11);
			String fragment = m.group(13);

			return encodeUriComponents(scheme, authority, userinfo, host, port, path, query, fragment, encoding);
		}
		throw new IllegalArgumentException("Invalid URI: " + uri);
	}

	/**
	 * @see #encodeHttpUrl(String, String)
	 */
	public static String encodeHttpUrl(String httpUrl) {
		return encodeHttpUrl(httpUrl, JoddCore.defaults().getEncoding());
	}
	/**
	 * Encodes the given HTTP URI into an encoded String. All various URI components are
	 * encoded according to their respective valid character sets.
	 * <p>This method does <b>not</b> support fragments ({@code #}),
	 * as these are not supposed to be sent to the server, but retained by the client.
	 * <p>This method does <b>not</b> attempt to encode "=" and "{@literal &}"
	 * characters in query parameter names and query parameter values because they cannot
	 * be parsed in a reliable way.
	 */
	public static String encodeHttpUrl(String httpUrl, String encoding) {
		Matcher m = HTTP_URL_PATTERN.matcher(httpUrl);
		if (m.matches()) {
			String scheme = m.group(1);
			String authority = m.group(2);
			String userinfo = m.group(4);
			String host = m.group(5);
			String portString = m.group(7);
			String path = m.group(8);
			String query = m.group(10);

			return encodeUriComponents(scheme, authority, userinfo, host, portString, path, query, null, encoding);
		}
		throw new IllegalArgumentException("Invalid HTTP URL: " + httpUrl);
	}

	private static String encodeUriComponents(
			String scheme, String authority, String userInfo,
			String host, String port, String path, String query,
			String fragment, String encoding) {

		StringBuilder sb = new StringBuilder();

		if (scheme != null) {
			sb.append(encodeScheme(scheme, encoding));
			sb.append(':');
		}

		if (authority != null) {
			sb.append("//");
			if (userInfo != null) {
				sb.append(encodeUserInfo(userInfo, encoding));
				sb.append('@');
			}
			if (host != null) {
				sb.append(encodeHost(host, encoding));
			}
			if (port != null) {
				sb.append(':');
				sb.append(encodePort(port, encoding));
			}
		}

		sb.append(encodePath(path, encoding));

		if (query != null) {
			sb.append('?');
			sb.append(encodeQuery(query, encoding));
		}

		if (fragment != null) {
			sb.append('#');
			sb.append(encodeFragment(fragment, encoding));
		}

		return sb.toString();
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Creates URL builder for user-friendly way of building URLs.
	 * Provided path is parsed and {@link #encodeUri(String) encoded}.
	 * @see #build(String, boolean)
	 */
	public static Builder build(String path) {
		return build(path, true);
	}

	/**
	 * Creates URL builder with given path that can be optionally encoded.
	 * Since most of the time path is valid and does not require to be encoded,
	 * use this method to gain some performance. When encoding flag is turned off,
	 * provided path is used without processing.
	 * <p>
	 * The purpose of builder is to help with query parameters. All other URI parts
	 * should be set previously or after the URL is built.
	 */
	public static Builder build(String path, boolean encodePath) {
		return new Builder(path, encodePath, JoddCore.defaults().getEncoding());
	}

	public static class Builder {
		protected final StringBuilder url;
		protected final String encoding;
		protected boolean hasParams;

		public Builder(String path,  boolean encodePath, String encoding) {
			this.encoding = encoding;
			url = new StringBuilder();
			if (encodePath) {
				url.append(encodeUri(path, encoding));
			} else {
				url.append(path);
			}
			this.hasParams = url.indexOf(StringPool.QUESTION_MARK) != -1;
		}

		/**
		 * Appends new query parameter to the url.
		 */
		public Builder queryParam(String name, String value) {
			url.append(hasParams ? '&' : '?');
			hasParams = true;

			url.append(encodeQueryParam(name, encoding));

			if ((value != null) && (value.length() > 0)) {
				url.append('=');
				url.append(encodeQueryParam(value, encoding));
			}
			return this;
		}

		/**
		 * Returns full URL.
		 */
		@Override
		public String toString() {
			return url.toString();
		}
	}

}