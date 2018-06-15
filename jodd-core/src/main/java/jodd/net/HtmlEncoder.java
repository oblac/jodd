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

package jodd.net;

import jodd.util.StringPool;

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

	/*
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
	 *     <li><code>&amp;&nbsp;</code></li>
	 * </ul>
	 */
	public static String attributeDoubleQuoted(final CharSequence value) {
		return encode(value, ATTR_DQ, LEN);
	}

	/**
	 * Encodes attribute value that will be single quoted.
	 * In this case, only two entities are encoded:
	 * <ul>
	 *     <li><code>&amp;</code> with <code>&amp;amp;</code></li>
	 *     <li><code>'</code> with <code>&amp;#39;</code></li>
	 *     <li><code>&amp;&nbsp;</code></li>
	 * </ul>
	 */
	public static String attributeSingleQuoted(final CharSequence value) {
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
	public static String text(final CharSequence text) {
		return encode(text, TEXT, LEN);
	}

	/**
	 * Encodes XML string. In XML there are only 5 predefined character entities.
	 */
	public static String xml(final CharSequence text) {
		return encode(text, TEXT_XML, LEN_XML);
	}

	// ---------------------------------------------------------------- private

	private static String encode(final CharSequence text, final char[][] buff, final int bufflen) {
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