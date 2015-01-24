// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Random;

/**
 * Generates random strings.
 */
public class RandomStringUtil {

	protected static final RandomString rndString = new RandomString();

	// ---------------------------------------------------------------- string

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters specified.
	 */
	public static String random(int count, char[] chars) {
		return rndString.random(count,chars);
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters specified.
	 */
	public static String random(int count, String chars) {
		return rndString.random(count, chars);
	}

	// ---------------------------------------------------------------- range

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the provided range.
	 */
	public static String random(int count, char start, char end) {
		return rndString.random(count,start,end);
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters whose
	 * ASCII value is between <code>32</code> and <code>126</code> (inclusive).
	 */
	public static String randomAscii(int count) {
		return rndString.randomAscii(count);
	}

	/**
	 * Creates random string that consist only of numbers.
	 */
	public static String randomNumeric(int count) {
		return rndString.randomNumeric(count);
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the multiple sets defined by range pairs.
	 * All ranges must be in acceding order.
	 */
	public static String randomRanges(int count, char... ranges) {
		return rndString.randomRanges(count,ranges);
	}

	protected static final char[] ALPHA_RANGE = new char[] {'A', 'Z', 'a', 'z'};
	protected static final char[] ALPHA_NUMERIC_RANGE = new char[] {'0', '9', 'A', 'Z', 'a', 'z'};

	/**
	 * Creates random string of characters.
	 */
	public static String randomAlpha(int count) {
		return rndString.randomAlpha(count);
	}

	/**
	 * Creates random string of characters and digits. 
	 */
	public static String randomAlphaNumeric(int count) {
		return rndString.randomAlphaNumeric(count);
	}

	/**
	 * Use {@code rnd} to create a RandomString
	 */
	public static RandomString create(Random rnd)
	{
		return new RandomString(rnd);
	}
	/**
	 * Create a RandomString with {@code new Random}
	 */
	public static RandomString create()
	{
		return new RandomString(new Random());
	}

	/**
	 * Generates random strings with a {@link java.util.Random}.
	 */
	public static class RandomString
	{
		protected final Random rnd;

		protected RandomString()
		{
			this(new Random());
		}
		protected RandomString(Random rnd)
		{
			this.rnd = rnd;
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
			return random(count, (char)32, (char)126);
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

	}
}