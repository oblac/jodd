// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Encodes text and URL strings in various ways resulting HTML-safe text.
 * All methods are <code>null</code> safe.
 */
public class HtmlEncoder {

	private static final int LEN = 161;
	private static final int LEN_XML = 0x40;
	private static final char[][] TEXT = new char[LEN][];
	private static final char[][] TEXT_XML = new char[LEN_XML][];

	private static final char[] QUOT = "&quot;".toCharArray();

	/**
	 * Creates HTML lookup tables for faster encoding.
	 */
	static {
		for (int i = 0; i < LEN; i++) {
			TEXT[i] = new char[] {(char) i};
		}
		for (int i = 0; i < LEN_XML; i++) {
			TEXT_XML[i] = new char[] {(char) i};
		}

		// HTML illegal chars

		for (int i = 0; i <= 31; i++) {
			if (i == 9 || i == 10 || i == 13) {
				continue;
			}
			TEXT[i] = null;
		}

		TEXT[127] = null;

		for (int i = 128; i <= 159; i++) {
			TEXT[i] = null;
		}

		// HTML characters
		TEXT['&']	= "&amp;".toCharArray();	// ampersand
		TEXT['<']	= "&lt;".toCharArray();	    // lower than
		TEXT['>']	= "&gt;".toCharArray();	    // greater than
		TEXT[0xA0]	= "&nbsp;".toCharArray();	// non-breaking space

		// XML characters
		TEXT_XML['&']	= "&amp;".toCharArray();	// ampersand
		TEXT_XML['\"']	= "&quot;".toCharArray();	// double-quote
		TEXT_XML['\'']	= "&#39;".toCharArray();	// single-quote (&apos; is not working for all browsers)
		TEXT_XML['<']	= "&lt;".toCharArray();	    // lower than
		TEXT_XML['>']	= "&gt;".toCharArray();	    // greater than
	}

	// ---------------------------------------------------------------- encode text

	/**
	 * Encodes HTML attribute value string to safe text. It is assumed that attribute value
	 * is quoted with the double quotes. The following characters are replaced:
	 * <ul>
	 * <li>" with &amp;quot;</li>
	 * <li>&amp; with &amp;amp;</li>
	 * <li>&lt; with &amp;lt;</li>
	 * <li>&gt; with &amp;gt;</li>
	 * <li>\u00A0 with &amp;nbsp;</li>
	 * </ul>
	 *
	 * Some characters are invalid by the spec and they can not be encoded.
	 *
	 * @see #text(CharSequence)
	 */
	public static String attribute(CharSequence value) {
		return _encodeHtml(value, '\"', QUOT);
	}

	/**
	 * Encodes a string to HTML-safe text. The following characters are replaced:
	 * <ul>
	 * <li>&amp; with &amp;amp;</li>
	 * <li>&lt; with &amp;lt;</li>
	 * <li>&gt; with &amp;gt;</li>
	 * <li>\u00A0 with &nbsp;</li>
	 * </ul>
	 *
	 * Some characters are invalid by the spec and they can not be encoded.
	 *
	 * @see #attribute(java.lang.CharSequence)
	 */
	public static String text(CharSequence text) {
		return _encodeHtml(text, (char) 0, null);
	}

	/**
	 * Encodes HTML.
	 */
	private static String _encodeHtml(CharSequence text, char addonChar, char[] addonCharReplacement) {
		int len;
		if ((text == null) || ((len = text.length()) == 0)) {
			return StringPool.EMPTY;
		}

		StringBuilder buffer = new StringBuilder(len + (len >> 2));
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (c < LEN) {
				char[] encoded = TEXT[c];

				if (encoded == null) {
					continue;
				}

				if (c == addonChar) {
					buffer.append(addonCharReplacement);
				} else {
					buffer.append(encoded);
				}
			} else {
				if ((c >= 0xD800 && c <= 0xDFFF) || (c == 0xFFFE) || (c == 0xFFFF)) {
					continue;
				}
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	// ---------------------------------------------------------------- xml

	/**
	 * Encodes XML string. In XML there are only 5 predefined character entities.
	 */
	public static String xml(CharSequence text) {
		int len;
		if ((text == null) || ((len = text.length()) == 0)) {
			return StringPool.EMPTY;
		}

		StringBuilder buffer = new StringBuilder(len + (len >> 2));

		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (c < LEN_XML) {
				buffer.append(TEXT_XML[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}


}