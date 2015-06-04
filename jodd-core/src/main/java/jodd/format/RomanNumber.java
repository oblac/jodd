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

package jodd.format;

/**
 * Conversion to and from Roman numbers.
 */
public class RomanNumber {

	private static final int[] VALUES = new int[] {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	private static final String[] LETTERS = new String[] {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

	/**
	 * Converts to Roman number.
	 */
	public static String convertToRoman(int value) {
		if (value <= 0) {
			throw new IllegalArgumentException();
		}
		StringBuilder roman = new StringBuilder();
		int n = value;
		for (int i = 0; i < LETTERS.length; i++) {
			while (n >= VALUES[i]) {
				roman.append(LETTERS[i]);
				n -= VALUES[i];
			}
		}
		return roman.toString();
	}

	/**
	 * Converts to Arabic numbers.
	 */
	public static int convertToArabic(String roman) {
		int start = 0, value = 0;
		for (int i = 0; i < LETTERS.length; i++) {
			while (roman.startsWith(LETTERS[i], start)) {
				value += VALUES[i];
				start += LETTERS[i].length();
			}
		}
		return start == roman.length() ? value : -1;
	}

	/**
	 * Checks if some string is valid roman number.
	 */
	public static boolean isValidRomanNumber(String roman) {
		try {
			return roman.equals(convertToRoman(convertToArabic(roman)));
		} catch (IllegalArgumentException ignore) {
			return false;
		}
	}
}
