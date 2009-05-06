// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.StringPool;

/**
 * Encodes text and URL strings in various ways resulting HTML-safe text.
 * All methods are <code>null</code> safe.
 */
public class HtmlEncoder {

	protected static final char[][] TEXT = new char[64][];
	protected static final char[][] BLOCK = new char[64][];

	/**
	 * Creates HTML lookup tables for faster encoding.
	 */
	static {
		for (int i = 0; i < 64; i++) {
			TEXT[i] = new char[] {(char) i};
		}

		// special HTML characters
		TEXT['\'']	= "&#039;".toCharArray();	// apostrophe ('&apos;' doesn't work - it is not by the w3 specs)
		TEXT['"']	= "&quot;".toCharArray();	// double quote
		TEXT['&']	= "&amp;".toCharArray();	// ampersand
		TEXT['<']	= "&lt;".toCharArray();	    // lower than
		TEXT['>']	= "&gt;".toCharArray();	    // greater than

		// text table
		System.arraycopy(TEXT, 0, BLOCK, 0, 64);
		BLOCK['\n']	= "<br>".toCharArray();     // ascii 10, new line
		BLOCK['\r']	= "<br>".toCharArray();     // ascii 13, carriage return
	}

	// ---------------------------------------------------------------- encode

	public static String text(Object object) {
		if (object == null) {
			return StringPool.EMPTY;
		}
		return text(object.toString());
	}

	/**
	 * Encodes a string to HTML-safe text. The following characters are replaced:
	 * <ul>
	 * <li>' with &amp;#039; (&amp;apos; doesn't work)</li>
	 * <li>" with &amp;quot;</li>
	 * <li>&amp; with &amp;amp;</li>
	 * <li>&lt; with &amp;lt;</li>
	 * <li>&gt; with &amp;gt;</li>
	 * </ul>
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
			if (c < 64) {
				buffer.append(TEXT[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	// ---------------------------------------------------------------- enocode text

	public static String block(Object object) {
		if (object == null) {
			return StringPool.EMPTY;
		}
		return block(object.toString());
	}


	/**
	 * Encodes text into HTML-safe block preserving paragraphs. Besides the {@link #text(String) default
	 * special characters} the following are replaced, too:
	 * <ul>
	 * <li>\n with &lt;br&gt;</li>
	 * <li>\r with &lt;br&gt;</li>
	 * </ul>
	 *
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
			if ((c == '\n') && (prev == '\r')) {
				continue;		// previously '\r' (CR) was encoded, so skip '\n' (LF)
			}
			if (c < 64) {
				buffer.append(BLOCK[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}


	// ---------------------------------------------------------------- encode text strict

	public static String strict(Object object) {
		if (object == null) {
			return StringPool.EMPTY;
		}
		return strict(object.toString());
	}


	/**
	 * Encodes text int HTML-safe block and preserves format using smart spaces.
	 * Additionally to {@link #block(String)}, the following characters are replaced:
	 *
	 * <ul>
	 * <li>\n with &lt;br&gt;</li>
	 * <li>\r with &lt;br&gt;</li>
	 * </ul>
	 * <p>
	 * This method preserves the format as much as possible, using the combination of
	 * not-breakable and common spaces.
	 */
	public static String strict(String text) {
		int len;
		if ((text == null) || ((len = text.length()) == 0)) {
			return StringPool.EMPTY;
		}
		StringBuilder buffer = new StringBuilder(len + (len >> 2));
		char c, prev = 0;
		boolean prevSpace = false;
		for (int i = 0; i < len; i++, prev = c) {
			c = text.charAt(i);

			if (c == ' ') {
				if (prev != ' ') {
					prevSpace = false;
				}
				if (prevSpace == false) {
					buffer.append(' ');
				} else {
					buffer.append("&nbsp;");
				}
				prevSpace = !prevSpace;
				continue;
			}
			if ((c == '\n') && (prev == '\r')) {
				continue;		// previously '\r' (CR) was encoded, so skip '\n' (LF)
			}
			if (c < 64) {
				buffer.append(BLOCK[c]);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

}
