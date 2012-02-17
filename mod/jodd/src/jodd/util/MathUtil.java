// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Various math utilities.
 */
public class MathUtil {

	/**
	 * Converts char digit into integer value.
	 * Accepts numeric chars (0 - 9) as well as letter (A-z).
	 */
	public static int parseDigit(char digit) {
		if ((digit >= '0') && (digit <= '9')) {
			return digit - '0';
		}
		if (CharUtil.isLowercaseLetter(digit)) {
			return 10 + digit - 'a';
		}
		return 10 + digit - 'A';
	}

	/**
	 * Generates pseudo-random long from specific range. Generated number is
	 * great or equals to min parameter value and less then max parameter value.
	 * Uses {@link Math#random()}.
	 *
	 * @param min    lower (inclusive) boundary
	 * @param max    higher (exclusive) boundary
	 *
	 * @return pseudo-random value
	 */

	public static long randomLong(long min, long max) {
		return min + (long)(Math.random() * (max - min));
	}


	/**
	 * Generates pseudo-random integer from specific range. Generated number is
	 * great or equals to min parameter value and less then max parameter value.
	 * Uses {@link Math#random()}. 
	 *
	 * @param min    lower (inclusive) boundary
	 * @param max    higher (exclusive) boundary
	 *
	 * @return pseudo-random value
	 */
	public static int randomInt(int min, int max) {
		return min + (int)(Math.random() * (max - min));
	}

	/**
	 * Returns <code>true</code> if a number is even.
	 */
	public static boolean isEven(int x) {
		return (x % 2) == 0;
	}

	/**
	 * Returns <code>true</code> if a number is odd.
	 */
	public static boolean isOdd(int x) {
		return (x % 2) != 0;
	}

	/**
	 * Calculates factorial of given number.
	 */
	public static long factorial(long x) {
		if (x < 0) {
			return 0;
		}
		long factorial = 1;

		while (x > 1) {
			factorial *= x;
			x--;
		}

		return factorial;
	}
}
