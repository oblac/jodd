// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.JoddDefault;
import jodd.util.MathUtil;
import jodd.util.StringPool;

import java.io.UnsupportedEncodingException;

/**
 * URL decoder.
 */
public class URLDecoder {

	/**
	 * Decodes URL elements.
	 */
	public static String decode(String url) {
		return decode(url, JoddDefault.encoding);
	}

	/**
	 * Decodes URL elements.
	 */
	public static String decode(String url, String encoding) {

		int queryIndex = url.indexOf('?');
		if (queryIndex != -1) {
			url = url.substring(0, queryIndex) + url.substring(queryIndex).replace('+', ' ');
		}

		int ndx = url.indexOf('%');
		if (ndx == -1) {
			return url;
		}

		StringBuilder result = new StringBuilder(url.length());

		int lastIndex = 0;
		int len = url.length();
		while (ndx != -1) {
			result.append(url.substring(lastIndex, ndx));
			ndx++;
			if (ndx + 2 <= len) {
				int value = MathUtil.parseDigit(url.charAt(ndx));
				value <<= 4;
				value += MathUtil.parseDigit(url.charAt(ndx + 1));

				result.append((char) value);
				lastIndex = ndx + 2;
			}
			ndx = url.indexOf('%', lastIndex);
		}
		result.append(url.substring(lastIndex));

		try {
			return new String(result.toString().getBytes(StringPool.ISO_8859_1), encoding);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

}