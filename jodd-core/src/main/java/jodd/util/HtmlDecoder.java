// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.StreamUtil;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * HTML decoder.
 */
public class HtmlDecoder {

	private static final Map<String, char[]> ENTITY_MAP;

	static {
		Properties entityReferences = new Properties();

		String propertiesName = HtmlDecoder.class.getSimpleName() + ".properties";

		InputStream is = HtmlDecoder.class.getResourceAsStream(propertiesName);

		try {
			entityReferences.load(is);
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage());
		} finally {
			StreamUtil.close(is);
		}

		ENTITY_MAP = new HashMap<String, char[]>(entityReferences.size());

		Enumeration keys = entityReferences.propertyNames();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			String values = entityReferences.getProperty(name);
			String[] array = StringUtil.splitc(values, ',');

			char[] chars;

			String hex = array[0];
			char value = (char) Integer.parseInt(hex, 16);

			if (array.length == 2) {
				String hex2 = array[1];
				char value2 = (char) Integer.parseInt(hex2, 16);

				chars = new char[]{value, value2};
			} else {
				chars = new char[]{value};
			}

			ENTITY_MAP.put(name, chars);
		}
	}

	/**
	 * Decodes HTML text. Assumes that all character references are properly closed with semi-colon.
	 */
	public static String decode(String html) {

		int ndx = html.indexOf('&');
		if (ndx == -1) {
			return html;
		}

		StringBuilder result = new StringBuilder(html.length());

		int lastIndex = 0;
		int len = html.length();
mainloop:
		while (ndx != -1) {
			result.append(html.substring(lastIndex, ndx));

			lastIndex = ndx;
			while (html.charAt(lastIndex) != ';') {
				lastIndex++;
				if (lastIndex == len) {
					lastIndex = ndx;
					break mainloop;
				}
			}

			if (html.charAt(ndx + 1) == '#') {
				// decimal/hex
				char c = html.charAt(ndx + 2);
				int radix;
				if ((c == 'x') || (c == 'X')) {
					radix = 16;
					ndx += 3;
				} else {
					radix = 10;
					ndx += 2;
				}

				String number = html.substring(ndx, lastIndex);
				int i = Integer.parseInt(number, radix);
				result.append((char) i);
				lastIndex++;
			} else {
				// token
				String encodeToken = html.substring(ndx + 1, lastIndex);

				char[] replacement = ENTITY_MAP.get(encodeToken);
				if (replacement == null) {
					result.append('&');
					lastIndex = ndx + 1;
				} else {
					result.append(replacement);
					lastIndex++;
				}
			}
			ndx = html.indexOf('&', lastIndex);
		}
		result.append(html.substring(lastIndex));
		return result.toString();
	}

	/**
	 * Detects HTML name on given location, after the {@code &} sign.
	 */
	public static String detectName(char[] input, int ndx) {
		char[] sb = new char[40];
		int len = input.length;

		// add first char as there is no ref name with length 1
		if (ndx + 1 >= len) {
			return null;
		}
		sb[0] = input[ndx];
		ndx++;
		int offset = 1;

		while (true) {
			char c = input[ndx];

			if (c == ';') {
				return null;
			}

			sb[offset++] = c;

			String name = new String(sb, 0, offset);

			char[] ref = ENTITY_MAP.get(name);

			if (ref != null) {
				return name;
			}

			ndx++;

			if (ndx == len) {
				return null;
			}
		}
	}

}