// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Encodes text and URL strings in various ways resulting HTML-safe text.
 * All methods are <code>null</code> safe.
 * Invalid HTML chars are not checked with these methods, they are just
 * passed as they are.
 */
public class HtmlEncoder {

	private static final int LEN = 0xA1;
	private static final int LEN_XML = 0x40;
	private static final char[][] TEXT = new char[LEN][];
	private static final char[][] ATTR_SQ = new char[LEN][];
	private static final char[][] ATTR_DQ = new char[LEN][];
	private static final char[][] TEXT_XML = new char[LEN_XML][];

	private static final char[] AMP = "&amp;".toCharArray();
	private static final char[] QUOT = "&quot;".toCharArray();
	private static final char[] APOS = "&#39;".toCharArray();
	private static final char[] LT = "&lt;".toCharArray();
	private static final char[] GT = "&gt;".toCharArray();
	private static final char[] NBSP = "&nbsp;".toCharArray();

	/**
	 * Creates HTML lookup tables for faster encoding.
	 */
	static {
		for (int i = 0; i < LEN_XML; i++) {
			TEXT_XML[i] = TEXT[i] = ATTR_SQ[i] = ATTR_DQ[i] = new char[] {(char) i};
		}
		for (int i = LEN_XML; i < LEN; i++) {
			TEXT[i] = ATTR_SQ[i] = ATTR_DQ[i] = new char[] {(char) i};
		}

		// HTML characters
		TEXT['&']	= AMP;		// ampersand
		TEXT['<']	= LT;	    // less than
		TEXT['>']	= GT;		// greater than
		TEXT[0xA0]	= NBSP;

		// SINGLE QUOTE
		ATTR_SQ['&']	= AMP;		// ampersand
		ATTR_SQ['\'']	= APOS;		// single quote
		ATTR_SQ[0xA0]	= NBSP;

		// DOUBLE QUOTE
		ATTR_DQ['&']	= AMP;		// ampersand
		ATTR_DQ['\"']	= QUOT;		// double quote
		ATTR_DQ[0xA0]	= NBSP;

		// XML characters
		TEXT_XML['&']	= AMP;		// ampersand
		TEXT_XML['\"']	= QUOT;		// double-quote
		TEXT_XML['\'']	= APOS;		// single-quote (&apos; is not working for all browsers)
		TEXT_XML['<']	= LT;	    // less than
		TEXT_XML['>']	= GT;	    // greater than
	}

	// ---------------------------------------------------------------- encode text

	/**
	 * Encodes attribute value that will be double quoted.
	 * In this case, only these entities are encoded:
	 * <ul>
	 *     <li><code>&amp;</code> with <code>&amp;amp;</code></li>
	 *     <li><code>"</code> with <code>&amp;quot;</code></li>
	 *     <li><code>&amp;nbsp;</code></li>
	 * </ul>
	 */
	public static String attributeDoubleQuoted(CharSequence value) {
		return encode(value, ATTR_DQ, LEN);
	}

	/**
	 * Encodes attribute value that will be single quoted.
	 * In this case, only two entities are encoded:
	 * <ul>
	 *     <li><code>&amp;</code> with <code>&amp;amp;</code></li>
	 *     <li><code>'</code> with <code>&amp;#39;</code></li>
	 *     <li><code>&amp;nbsp;</code></li>
	 * </ul>
	 */
	public static String attributeSingleQuoted(CharSequence value) {
		return encode(value, ATTR_SQ, LEN);
	}

	/**
	 * Encodes a string to HTML-safe text. The following characters are replaced:
	 * <ul>
	 * <li><code>&amp;</code> with <code>&amp;amp;</code></li>
	 * <li><code>&lt;</code> with <code>&amp;lt;</code></li>
	 * <li><code>&gt;</code> with <code>&amp;gt;</code></li>
	 * <li><code>\u00A0</code> with <code>&nbsp;</code></li>
	 * </ul>
	 */
	public static String text(CharSequence text) {
		return encode(text, TEXT, LEN);
	}

	/**
	 * Encodes XML string. In XML there are only 5 predefined character entities.
	 */
	public static String xml(CharSequence text) {
		return encode(text, TEXT_XML, LEN_XML);
	}

	// ---------------------------------------------------------------- private

	private static String encode(CharSequence text, char[][] buff, int bufflen) {
		int len;
		if ((text == null) || ((len = text.length()) == 0)) {
			return StringPool.EMPTY;
		}

		StringBuilder buffer = new StringBuilder(len + (len >> 2));

		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);

			if (c < bufflen) {
				buffer.append(buff[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}


}