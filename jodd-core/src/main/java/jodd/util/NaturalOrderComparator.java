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
 * Compares two strings in natural, alphabetical, way.
 */
public class NaturalOrderComparator<T> implements Comparator<T>, Serializable {
	private static final long serialVersionUID = 1;

	protected final boolean ignoreCase;

	public NaturalOrderComparator() {
		ignoreCase = false;
	}

	public NaturalOrderComparator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
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

		int ndx1 = 0, ndx2 = 0;
		int zeroCount1, zeroCount2;
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
					zeroCount1 = 0;	// counts only last 0 prefixes, space char interrupts the array of 0s
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

			// process digits

			boolean isDigitChar1 = CharUtil.isDigit(char1);
			boolean isDigitChar2 = CharUtil.isDigit(char2);

			if (isDigitChar1 && isDigitChar2) {
				result = compareDigits(str1, ndx1, str2, ndx2);
				if (result != 0) {
					// not equals, return
					return result;
				}
				// equal numbers
				if (zeroCount1 != zeroCount2) {
					return zeroCount1 - zeroCount2;
				}
			}

			if (char1 == 0 && char2 == 0) {
				// the end; the strings are the same, maybe compare ascii?
				return zeroCount1 - zeroCount2;
			}

			// check when one of the numbers is just zeros
			if (isDigitChar1 || isDigitChar2) {
				if (zeroCount1 != zeroCount2) {
					return zeroCount2 - zeroCount1;
				}
			}

			// checks when both numbers are zero
			if (zeroCount1 != zeroCount2) {
				return zeroCount1 - zeroCount2;
			}

			// compare chars
			if (ignoreCase) {
				char1 = Character.toLowerCase(char1);
				char2 = Character.toLowerCase(char2);
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
	 * Safe charAt.
	 */
	private static char charAt(String s, int i) {
		if (i >= s.length()) {
			return 0;
		}
		return s.charAt(i);
	}
}
