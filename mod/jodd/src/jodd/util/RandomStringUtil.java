// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Random;

/**
 * Generates random strings.
 */
public class RandomStringUtil {

	protected static final Random rnd = new Random();

	// ---------------------------------------------------------------- string

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters specified.
	 */
	public static String random(int count, char[] chars) {
		if (count == 0) {
			return StringPool.EMPTY;
		}
		char[] result = new char[count];
		while (count-- > 0) {
			result[count] = chars[rnd.nextInt(chars.length)];
		}
		return new String(result);
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters specified.
	 */
	public static String random(int count, String chars) {
		return random(count, chars.toCharArray());
	}

	// ---------------------------------------------------------------- range

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the provided range.
	 */
	public static String random(int count, char start, char end) {
		if (count == 0) {
			return StringPool.EMPTY;
		}
		char[] result = new char[count];
		int len = end - start + 1;
		while (count-- > 0) {
			result[count] = (char) (rnd.nextInt(len) + start);
		}
		return new String(result);
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters whose
	 * ASCII value is between <code>32</code> and <code>126</code> (inclusive).
	 */
	public static String randomAscii(int count) {
		return random(count, (char)32, (char)126);
	}

	/**
	 * Creates random string that consist only of numbers.
	 */
	public static String randomNumeric(int count) {
		return random(count, '0', '9');
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the multiple sets defined by range pairs.
	 * All ranges must be in accedenting order.
	 */
	public static String randomRanges(int count, char... ranges) {
		if (count == 0) {
			return StringPool.EMPTY;
		}
		int i = 0;
		int len = 0;
		int lens[] = new int[ranges.length];
		while (i < ranges.length) {
			int gap = ranges[i + 1] - ranges[i] + 1;
			len += gap;
			lens[i] = len;
			i += 2;
		}

		char[] result = new char[count];
		while (count-- > 0) {
			char c = 0;
			int r = rnd.nextInt(len);
			for (i = 0; i < ranges.length; i += 2) {
				if (r < lens[i]) {
					r += ranges[i];
					if (i != 0) {
						r -= lens[i - 2];
					}
					c = (char) r;
					break;
				}
			}
			result[count] = c;
		}
		return new String(result);
	}

	protected static char[] alphaRange = new char[] {'A', 'Z', 'a', 'z'};
	protected static char[] alphaNumericRange = new char[] {'0', '9', 'A', 'Z', 'a', 'z'};

	/**
	 * Creates random string of characters.
	 */
	public static String randomAlpha(int count) {
		return randomRanges(count, alphaRange);
	}

	/**
	 * Creates random string of characters and digits. 
	 */
	public static String randomAlphaNumeric(int count) {
		return randomRanges(count, alphaNumericRange);
	}

}