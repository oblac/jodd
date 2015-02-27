// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Random;

/**
 * Class that generates random strings.
 */
public class RandomString {

	protected static final char[] ALPHA_RANGE = new char[] {'A', 'Z', 'a', 'z'};
	protected static final char[] ALPHA_NUMERIC_RANGE = new char[] {'0', '9', 'A', 'Z', 'a', 'z'};

	protected final static RandomString INSTANCE = new RandomString();

	/**
	 * Returns default instance of <code>RandomString</code>.
	 */
	public static RandomString getInstance() {
		return INSTANCE;
	}

	protected final Random rnd;

	/**
	 * Creates new random string.
	 */
	public RandomString() {
		this(new Random());
	}

	/**
	 * Creates new random string with given random object,
	 * so random strings can be repeated.
	 */
	public RandomString(Random rnd) {
		this.rnd = rnd;
	}

	/**
	 * Creates new random string with given seed.
	 */
	public RandomString(long seed) {
		this.rnd = new Random(seed);
	}

	// ---------------------------------------------------------------- string

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters specified.
	 */
	public String random(int count, char[] chars) {
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
	public String random(int count, String chars) {
		return random(count, chars.toCharArray());
	}

	// ---------------------------------------------------------------- range

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the provided range.
	 */
	public String random(int count, char start, char end) {
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
	public String randomAscii(int count) {
		return random(count, (char) 32, (char) 126);
	}

	/**
	 * Creates random string that consist only of numbers.
	 */
	public String randomNumeric(int count) {
		return random(count, '0', '9');
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the multiple sets defined by range pairs.
	 * All ranges must be in acceding order.
	 */
	public String randomRanges(int count, char... ranges) {
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

	/**
	 * Creates random string of characters.
	 */
	public String randomAlpha(int count) {
		return randomRanges(count, ALPHA_RANGE);
	}

	/**
	 * Creates random string of characters and digits.
	 */
	public String randomAlphaNumeric(int count) {
		return randomRanges(count, ALPHA_NUMERIC_RANGE);
	}

	/**
	 * Creates random string that contains only Base64 characters.
	 */
	public String randomBase64(int count) {
		return random(count, Base64.CHARS);
	}

}