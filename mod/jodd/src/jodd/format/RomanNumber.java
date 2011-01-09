// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

import jodd.util.StringPool;

/**
 * Works with roman numbers.
 */
public class RomanNumber {

	public static final int[] VALUES = new int[] {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	public static final String[] LETTERS = new String[] {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

	/**
	 * Converts to roman number.
	 */
	public static String romanize(int value) {
		String roman = StringPool.EMPTY;
		int n = value;
		for (int i = 0; i < LETTERS.length; i++) {
			while (n >= VALUES[i]) {
				roman += LETTERS[i];
				n -= VALUES[i];
			}
		}
		return roman;
	}

	/**
	 * Converts to arab numbers.
	 */
	public static int numberize(String roman) {
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
	public static boolean isRoman(String roman) {
		return roman.equals(romanize(numberize(roman)));
	}
}
