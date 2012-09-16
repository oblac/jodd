// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.io.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * HTML decoder.
 */
public class HtmlDecoder {

	private static final Map<String, Character> ENTITY_MAP;

	static {
		Properties entityReferences = new Properties();

		InputStream is = HtmlDecoder.class.getResourceAsStream(HtmlDecoder.class.getSimpleName() + ".properties");
		if (is == null) {
			throw new IllegalStateException("Entity reference file missing");
		}

		try {
			entityReferences.load(is);
		}
		catch (IOException ioex) {
			throw new IllegalStateException(ioex.getMessage());
		} finally {
			StreamUtil.close(is);
		}

		ENTITY_MAP = new HashMap<String, Character>(entityReferences.size());

		Enumeration keys = entityReferences.propertyNames();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			String hex = entityReferences.getProperty(name);
			int value = Integer.parseInt(hex, 16);
			ENTITY_MAP.put(name, Character.valueOf((char) value));
		}
	}

	/**
	 * Decodes HTML text.
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
				Character replacement = ENTITY_MAP.get(encodeToken);
				if (replacement == null) {
					result.append(encodeToken);
				} else {
					result.append(replacement.charValue());
					lastIndex++;
				}
			}
			ndx = html.indexOf('&', lastIndex);
		}
		result.append(html.substring(lastIndex));
		return result.toString();
	}

}
