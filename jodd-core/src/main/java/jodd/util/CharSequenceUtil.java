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

public class CharSequenceUtil {

	public static boolean equals(final CharSequence charSequence1, final CharSequence charSequence2) {
		int len = charSequence1.length();

		if (len != charSequence2.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			if (charSequence1.charAt(i) != charSequence2.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	public static boolean equalsToLowercase(final CharSequence charSequence, final CharSequence name) {
		int len = charSequence.length();

		if (len != name.length()) {
			return false;
		}

		if (compare(charSequence, name, len)) return false;

		return true;
	}

	public static boolean startsWithLowercase(final CharSequence charSequence, final CharSequence chars) {
		int length = chars.length();
		if (charSequence.length() < length) {
			return false;
		}

		if (compare(charSequence, chars, length)) return false;

		return true;
	}

	private static boolean compare(final CharSequence charSequence, final CharSequence chars, final int length) {
		for (int i = 0; i < length; i++) {
			char c = charSequence.charAt(i);

			c = CharUtil.toLowerAscii(c);

			if (c != chars.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsIgnoreCase(final CharSequence charSequence1, final CharSequence charSequence2) {
		int len = charSequence1.length();

		if (len != charSequence2.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			char c1 = charSequence1.charAt(i);
			c1 = CharUtil.toLowerAscii(c1);

			char c2 = charSequence2.charAt(i);
			c2 = CharUtil.toLowerAscii(c2);

			if (c1 != c2) {
				return false;
			}
		}
		return true;
	}


	// ---------------------------------------------------------------- find


	/**
	 * Match if one character equals to any of the given character.
	 *
	 * @return <code>true</code> if characters match any character from given array,
	 *         otherwise <code>false</code>
	 */
	public static boolean equalsOne(final char c, final CharSequence match) {
		for (int i = 0; i < match.length(); i++) {
			char aMatch = match.charAt(i);
			if (c == aMatch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds index of the first character in given charsequence the matches any from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(final CharSequence source, final int index, final CharSequence match) {
		for (int i = index; i < source.length(); i++) {
			if (equalsOne(source.charAt(i), match)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the matches any from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(final char[] source, final int index, final char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] == match) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Finds index of the first character in given charsequence the differs from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(final CharSequence source, final int index, final CharSequence match) {
		for (int i = index; i < source.length(); i++) {
			if (!equalsOne(source.charAt(i), match)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(final char[] source, final int index, final char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] != match) {
				return i;
			}
		}
		return -1;
	}

}
