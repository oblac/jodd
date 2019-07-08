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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Probably the best natural strings comparator.
 */
public class NaturalOrderComparator<T> implements Comparator<T>, Serializable {

	/* copied from Perl6 code */
	private static final char[] ACCENT_CHARS = new char[]{
		'À', 'A', 'Á', 'A', 'Â', 'A', 'Ã', 'A', 'Ä', 'A', 'Å', 'A',
		'à', 'a', 'á', 'a', 'â', 'a', 'ã', 'a', 'ä', 'a', 'å', 'a',
		'Ç', 'C', 'ç', 'c',
		'È', 'E', 'É', 'E', 'Ê', 'E', 'Ë', 'E',
		'è', 'e', 'é', 'e', 'ê', 'e', 'ë', 'e',
		'Ì', 'I', 'Í', 'I', 'Î', 'I', 'Ï', 'I',
		'ì', 'i', 'í', 'i', 'î', 'i', 'ï', 'i',
		'Ò', 'O', 'Ó', 'O', 'Ô', 'O', 'Õ', 'O', 'Ö', 'O',
		'Ø', 'O', 'ò', 'o', 'ó', 'o', 'ô', 'o', 'õ', 'o', 'ö', 'o', 'ø', 'o',
		'Ñ', 'N', 'ñ', 'n',
		'Ù', 'U', 'Ú', 'U', 'Û', 'U', 'Ü', 'U', 'ù', 'u', 'ú', 'u', 'û', 'u', 'ü', 'u',
		'Ý', 'Y', 'ÿ', 'y', 'ý', 'y',
	};

	protected final boolean ignoreCase;
	protected final boolean ignoreAccents;
	protected final boolean skipSpaces;

	public NaturalOrderComparator() {
		this(false, true, true);
	}

	public NaturalOrderComparator(final boolean ignoreCase, final boolean ignoreAccents, final boolean skipSpaces) {
		this.ignoreCase = ignoreCase;
		this.ignoreAccents = ignoreAccents;
		this.skipSpaces = skipSpaces;
	}

	/**
	 * Compare digits at certain position in two strings.
	 * The longest run of digits wins. That aside, the greatest
	 * value wins.
	 * @return if numbers are different, only 1 element is returned.
	 */
	protected int[] compareDigits(final String str1, int ndx1, final String str2, int ndx2) {
		// iterate all digits in the first string

		int zeroCount1 = 0;
		while (charAt(str1, ndx1) == '0') {
			zeroCount1++;
			ndx1++;
		}

		int len1 = 0;
		while (true) {
			final char char1 = charAt(str1, ndx1);
			final boolean isDigitChar1 = CharUtil.isDigit(char1);
			if (!isDigitChar1) {
				break;
			}
			len1++;
			ndx1++;
		}

		// iterate all digits in the second string and compare with the first

		int zeroCount2 = 0;
		while (charAt(str2, ndx2) == '0') {
			zeroCount2++;
			ndx2++;
		}

		int len2 = 0;

		int ndx1_new = ndx1 - len1;
		int equalNumbers = 0;

		while (true) {
			final char char2 = charAt(str2, ndx2);
			final boolean isDigitChar2 = CharUtil.isDigit(char2);
			if (!isDigitChar2) {
				break;
			}
			if (equalNumbers == 0 && (ndx1_new < ndx1)) {
				equalNumbers = charAt(str1, ndx1_new++) - char2;
			}
			len2++;
			ndx2++;
		}

		// compare

		if (len1 != len2) {
			// numbers are not equals size
			return new int[] {len1 - len2};
		}

		if (equalNumbers != 0) {
			return new int[] {equalNumbers};
		}

		// numbers are equal, but number of zeros is different
		return new int[] {0, zeroCount1 - zeroCount2, ndx1, ndx2};
	}

	@Override
	public int compare(final T o1, final T o2) {
		String str1 = o1.toString();
		String str2 = o2.toString();

		if (ignoreAccents) {
			str1 = StringUtil.replace(str1, "ß", "ss");
			str2 = StringUtil.replace(str2, "ß", "ss");

			str1 = StringUtil.replace(str1, "æ", "ae");
			str2 = StringUtil.replace(str2, "æ", "ae");

			str1 = StringUtil.replace(str1, "Æ", "AE");
			str2 = StringUtil.replace(str2, "Æ", "AE");
		}

		int ndx1 = 0, ndx2 = 0;
		char char1, char2;
		int lastZeroDifference = 0;

		while (true) {
			char1 = charAt(str1, ndx1);
			char2 = charAt(str2, ndx2);

			// skip over spaces in both strings
			if (skipSpaces) {
				while (Character.isSpaceChar(char1)) {
					ndx1++;
					char1 = charAt(str1, ndx1);
				}

				while (Character.isSpaceChar(char2)) {
					ndx2++;
					char2 = charAt(str2, ndx2);
				}
			}

			// check for numbers

			final boolean isDigitChar1 = CharUtil.isDigit(char1);
			final boolean isDigitChar2 = CharUtil.isDigit(char2);

			if (isDigitChar1 && isDigitChar2) {
				// numbers detected!

				final int[] result = compareDigits(str1, ndx1, str2, ndx2);

				if (result[0] != 0) {
					// not equals, return
					return result[0];
				}

				// equals, save zero difference if not already saved
				if (lastZeroDifference == 0) {
					lastZeroDifference = result[1];
				}

				ndx1 = result[2];
				ndx2 = result[3];
				continue;
			}

			if (char1 == 0 && char2 == 0) {
				// both strings end; the strings are the same
				return lastZeroDifference;
			}

			// compare chars
			if (ignoreCase) {
				char1 = Character.toLowerCase(char1);
				char2 = Character.toLowerCase(char2);
			}

			if (ignoreAccents) {
				char1 = fixAccent(char1);
				char2 = fixAccent(char2);
			}

			if (char1 < char2) {
				return -1;
			}
			if (char1 > char2) {
				return 1;
			}

			ndx1++;
			ndx2++;
		}
	}

	/**
	 * Fixes accent char.
	 */
	private char fixAccent(final char c) {
		for (int i = 0; i < ACCENT_CHARS.length; i+=2) {
			final char accentChar = ACCENT_CHARS[i];
			if (accentChar == c) {
				return ACCENT_CHARS[i + 1];
			}
		}
		return c;
	}

	/**
	 * Safe {@code charAt} that returns 0 when ndx is out of boundaries.
	 */
	private static char charAt(final String string, final int ndx) {
		if (ndx >= string.length()) {
			return 0;
		}
		return string.charAt(ndx);
	}
}
