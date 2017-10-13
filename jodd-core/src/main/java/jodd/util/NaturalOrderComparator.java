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

	public NaturalOrderComparator() {
		this(false, true);
	}

	public NaturalOrderComparator(boolean ignoreCase, boolean ignoreAccents) {
		this.ignoreCase = ignoreCase;
		this.ignoreAccents = ignoreAccents;
	}

	/**
	 * Compare digits at certain position in two strings.
	 * The longest run of digits wins. That aside, the greatest
	 * value wins.
	 */
	protected int compareDigits(String str1, int ndx1, String str2, int ndx2) {
		int bias = 0;

		while (true) {
			char char1 = charAt(str1, ndx1);
			char char2 = charAt(str2, ndx2);

			boolean isDigitChar1 = CharUtil.isDigit(char1);
			boolean isDigitChar2 = CharUtil.isDigit(char2);

			if (!isDigitChar1 && !isDigitChar2) {
				return bias;
			}
			if (!isDigitChar1) {
				return -1;
			}
			if (!isDigitChar2) {
				return 1;
			}

			if (char1 < char2) {
				if (bias == 0) {
					bias = -1;
				}
			} else if (char1 > char2) {
				if (bias == 0) {
					bias = 1;
				}
			} else if (char1 == 0 && char2 == 0) {
				return bias;
			}

			ndx1++;
			ndx2++;
		}
	}

	public int compare(T o1, T o2) {
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
		int zeroCount1, zeroCount2;
		int zerosDelta = 0;
		int lastAllZerosResult = 0;
		char char1, char2;

		int result;

		while (true) {
			// only count the number of zeroes leading the last number compared
			zeroCount1 = zeroCount2 = 0;

			char1 = charAt(str1, ndx1);
			char2 = charAt(str2, ndx2);

			// skip over leading spaces or zeros in both strings

			while (Character.isSpaceChar(char1) || char1 == '0') {
				if (char1 == '0') {
					zeroCount1++;
				} else {
					zeroCount1 = 0;		// counts only last 0 prefixes, space char interrupts the array of 0s
				}
				ndx1++;
				char1 = charAt(str1, ndx1);
			}

			while (Character.isSpaceChar(char2) || char2 == '0') {
				if (char2 == '0') {
					zeroCount2++;
				} else {
					zeroCount2 = 0;
				}
				ndx2++;
				char2 = charAt(str2, ndx2);
			}

			if (zeroCount1 > 0 || zeroCount2 > 0) {
				zerosDelta = zeroCount1 - zeroCount2;
			}

			// process remaining digits

			boolean isDigitChar1 = CharUtil.isDigit(char1);
			boolean isDigitChar2 = CharUtil.isDigit(char2);

			if (isDigitChar1 && isDigitChar2) {
				result = compareDigits(str1, ndx1, str2, ndx2);
				if (result != 0) {
					// not equals, return
					return result;
				}
				// if numbers are equal
				if (zeroCount1 != zeroCount2) {
					return zerosDelta;
				}
			}

			if (char1 == 0 && char2 == 0) {
				// both strings end; the strings are the same
				if (lastAllZerosResult == 0) {
					return zerosDelta;
				}
				return lastAllZerosResult;
			}

			// check when one of the numbers is just zeros; as the other
			// string is still a number
			if (isDigitChar1 || isDigitChar2) {
				if (zeroCount1 > 0 && zeroCount2 > 0) {
					if (zeroCount1 != zeroCount2) {
						return -zerosDelta;
					}
				}
			}

			// check if both numbers are zeros
			if (zerosDelta != 0) {
				// so we really have both number with at least one zero?
				if (zeroCount1 > 0 && zeroCount2 > 0) {
					lastAllZerosResult = zerosDelta;
				} else {
					// one of the number is empty strings
					// the other char defines the order!


					if (zeroCount1 > 0) {
						if (char2 > '0') {
							return -zerosDelta;
						} else {
							return zerosDelta;
						}
					} else if (zeroCount2 > 0) {
						if (char1 > '0') {
							return -zerosDelta;
						}
						else  {
							return zerosDelta;
						}
					}

					return 0;
				}
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
	private char fixAccent(char c) {
		for (int i = 0; i < ACCENT_CHARS.length; i+=2) {
			char accentChar = ACCENT_CHARS[i];
			if (accentChar == c) {
				return ACCENT_CHARS[i + 1];
			}
		}
		return c;
	}

	/**
	 * Safe {@code charAt} that returns 0 when ndx is out of boundaries.
	 */
	private static char charAt(String string, int ndx) {
		if (ndx >= string.length()) {
			return 0;
		}
		return string.charAt(ndx);
	}
}
