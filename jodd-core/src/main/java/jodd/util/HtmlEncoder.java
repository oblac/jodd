// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Encodes text and URL strings in various ways resulting HTML-safe text.
 * All methods are <code>null</code> safe.
 */
public class HtmlEncoder {

	private static final int LEN = 161;
	private static final char[][] TEXT = new char[LEN][];
	private static final char[] NEW_LINE = "<br/>".toCharArray();

	/**
	 * Creates HTML lookup tables for faster encoding.
	 */
	static {
		for (int i = 0; i < LEN; i++) {
			TEXT[i] = new char[] {(char) i};
		}

		// special HTML characters
		TEXT['\'']	= "&#039;".toCharArray();	// apostrophe ('&apos;' doesn't work - it is not by the w3 specs)
		TEXT['"']	= "&quot;".toCharArray();	// double quote
		TEXT['&']	= "&amp;".toCharArray();	// ampersand
		TEXT['<']	= "&lt;".toCharArray();	    // lower than
		TEXT['>']	= "&gt;".toCharArray();	    // greater than
		TEXT[0xA0]	= "&nbsp;".toCharArray();	// non-breaking space
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
	 * @see #text(String)
	 * @see #block(String)
	 */
	public static String attribute(String value) {
		int len;
		if ((value == null) || ((len = value.length()) == 0)) {
			return StringPool.EMPTY;
		}
		StringBuilder buffer = new StringBuilder(len + (len >> 2));
		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
			if (c < LEN && c != '\'') {
				buffer.append(TEXT[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * Encodes a string to HTML-safe text. The following characters are replaced:
	 * <ul>
	 * <li>' with &amp;#039; (&amp;apos; doesn't work in HTML4)</li>
	 * <li>" with &amp;quot;</li>
	 * <li>&amp; with &amp;amp;</li>
	 * <li>&lt; with &amp;lt;</li>
	 * <li>&gt; with &amp;gt;</li>
	 * <li>\u00A0 with &nbsp;</li>
	 * </ul>
	 * @see #attribute(String)
	 * @see #block(String)
	 */
	public static String text(String text) {
		int len;
		if ((text == null) || ((len = text.length()) == 0)) {
			return StringPool.EMPTY;
		}

		StringBuilder buffer = new StringBuilder(len + (len >> 2));
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (c < LEN) {
				buffer.append(TEXT[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	// ---------------------------------------------------------------- encode text block

	/**
	 * Encodes text into HTML-safe block preserving paragraphs. Besides the {@link #text(String) default
	 * special characters} the following are replaced, too:
	 * <ul>
	 * <li>\n with &lt;br&gt; or ignore, if previous \r already rendered</li>
	 * <li>\r with &lt;br&gt;</li>
	 * </ul>
	 * <p>
	 *  Method accepts any of CR, LF, or CR+LF as a line terminator.
	 */
	public static String block(String text) {
		int len;
		if ((text == null) || ((len = text.length()) == 0)) {
			return StringPool.EMPTY;
		}
		StringBuilder buffer = new StringBuilder(len + (len >> 2));
		char c, prev = 0;
		for (int i = 0; i < len; i++, prev = c) {
			c = text.charAt(i);
			if (c < LEN) {

				if (c == '\n') {
					if (prev == '\r') {
						continue;		// previously '\r' (CR) was encoded, so skip '\n' (LF)
					}
					buffer.append(NEW_LINE);
					continue;
				}

				if (c == '\r') {
					buffer.append(NEW_LINE);
					continue;
				}

				buffer.append(TEXT[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

}