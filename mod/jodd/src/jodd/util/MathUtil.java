// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Various math utilities.
 */
public class MathUtil {

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
}
