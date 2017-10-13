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

import jodd.io.StreamUtil;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * HTML decoder.
 */
public class HtmlDecoder {

	private static final Map<String, char[]> ENTITY_MAP;
	private static final char[][] ENTITY_NAMES;

	static {
		Properties entityReferences = new Properties();

		String propertiesName = HtmlDecoder.class.getSimpleName() + ".properties";

		InputStream is = HtmlDecoder.class.getResourceAsStream(propertiesName);

		try {
			entityReferences.load(is);
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		} finally {
			StreamUtil.close(is);
		}

		ENTITY_MAP = new HashMap<>(entityReferences.size());

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

		// create sorted list of entry names

		ENTITY_NAMES = new char[ENTITY_MAP.size()][];

		int i = 0;
		for (String name : ENTITY_MAP.keySet()) {
			ENTITY_NAMES[i++] = name.toCharArray();
		}

		Arrays.sort(ENTITY_NAMES, new Comparator<char[]>() {
			public int compare(char[] o1, char[] o2) {
				return new String(o1).compareTo(new String(o2));
			}
		});
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

	private static final class Ptr {
		public int offset;
		public char c;
	}

	/**
	 * Detects the longest character reference name on given position in char array.
	 */
	public static String detectName(final char[] input, int ndx) {
		final Ptr ptr = new Ptr();

		int firstIndex = 0;
		int lastIndex = ENTITY_NAMES.length - 1;
		int len = input.length;
		char[] lastName = null;

		BinarySearchBase binarySearch = new BinarySearchBase() {
			@Override
			protected int compare(int index) {
				char[] name = ENTITY_NAMES[index];

				if (ptr.offset >= name.length) {
					return -1;
				}

				return name[ptr.offset] - ptr.c;
			}
		};

		while (true) {
			ptr.c = input[ndx];

			if (!CharUtil.isAlphaOrDigit(ptr.c)) {
				return lastName != null ? new String(lastName) : null;
			}

			firstIndex = binarySearch.findFirst(firstIndex, lastIndex);
			if (firstIndex < 0) {
				return lastName != null ? new String(lastName) : null;
			}

			char[] element = ENTITY_NAMES[firstIndex];

			if (element.length == ptr.offset + 1) {
				// total match, remember position, continue for finding the longer name
				lastName = ENTITY_NAMES[firstIndex];
			}

			lastIndex = binarySearch.findLast(firstIndex, lastIndex);

			if (firstIndex == lastIndex) {
				// only one element found, check the rest
				for (int i = ptr.offset; i < element.length; i++) {
					if (element[i] != input[ndx]) {
						return lastName != null ? new String(lastName) : null;
					}
					ndx++;
				}
				return new String(element);
			}

			ptr.offset++;

			ndx++;
			if (ndx == len) {
				return lastName != null ? new String(lastName) : null;
			}
		}
	}

	/**
	 * Returns replacement chars for given character reference.
	 */
	public static char[] lookup(String name) {
		return ENTITY_MAP.get(name);
	}

}