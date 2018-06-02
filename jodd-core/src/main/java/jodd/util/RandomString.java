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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
	public static RandomString get() {
		return INSTANCE;
	}

	protected final Random rnd;

	/**
	 * Creates new random string.
	 */
	public RandomString() {
		this(ThreadLocalRandom.current());
	}

	/**
	 * Creates new random string with given random object,
	 * so random strings can be repeated.
	 */
	public RandomString(final Random rnd) {
		this.rnd = rnd;
	}

	/**
	 * Creates new random string with given seed.
	 */
	public RandomString(final long seed) {
		this.rnd = new Random(seed);
	}

	// ---------------------------------------------------------------- string

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the set of characters specified.
	 */
	public String random(int count, final char[] chars) {
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
	public String random(final int count, final String chars) {
		return random(count, chars.toCharArray());
	}

	// ---------------------------------------------------------------- range

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the provided range.
	 */
	public String random(int count, final char start, final char end) {
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
	public String randomAscii(final int count) {
		return random(count, (char) 32, (char) 126);
	}

	/**
	 * Creates random string that consist only of numbers.
	 */
	public String randomNumeric(final int count) {
		return random(count, '0', '9');
	}

	/**
	 * Creates random string whose length is the number of characters specified.
	 * Characters are chosen from the multiple sets defined by range pairs.
	 * All ranges must be in acceding order.
	 */
	public String randomRanges(int count, final char... ranges) {
		if (count == 0) {
			return StringPool.EMPTY;
		}
		int i = 0;
		int len = 0;
		int[] lens = new int[ranges.length];
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
	public String randomAlpha(final int count) {
		return randomRanges(count, ALPHA_RANGE);
	}

	/**
	 * Creates random string of characters and digits.
	 */
	public String randomAlphaNumeric(final int count) {
		return randomRanges(count, ALPHA_NUMERIC_RANGE);
	}

	/**
	 * Creates random string that contains only Base64 characters.
	 */
	public String randomBase64(final int count) {
		return random(count, Base64.CHARS);
	}

}