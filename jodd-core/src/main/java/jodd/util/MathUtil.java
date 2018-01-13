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

import java.util.concurrent.ThreadLocalRandom;

/**
 * Various math utilities. <br/>
 * <b>note:</b> Any random values from this class are not cryptographically secure!
 */
public class MathUtil {

	/**
	 * Converts char digit into integer value.
	 * Accepts numeric chars (0 - 9) as well as letter (A-z).
	 */
	public static int parseDigit(final char digit) {
		if ((digit >= '0') && (digit <= '9')) {
			return digit - '0';
		}
		if (CharUtil.isLowercaseAlpha(digit)) {
			return 10 + digit - 'a';
		}
		return 10 + digit - 'A';
	}

	/**
	 * Generates pseudo-random long from specific range. Generated number is
	 * great or equals to min parameter value and less then max parameter value.
	 *
	 * @param min    lower (inclusive) boundary
	 * @param max    higher (exclusive) boundary
	 *
	 * @return pseudo-random value
	 */

	public static long randomLong(final long min, final long max) {
		return min + (long)(ThreadLocalRandom.current().nextDouble() * (max - min));
	}


	/**
	 * Generates pseudo-random integer from specific range. Generated number is
	 * great or equals to min parameter value and less then max parameter value.
	 *
	 * @param min    lower (inclusive) boundary
	 * @param max    higher (exclusive) boundary
	 *
	 * @return pseudo-random value
	 */
	public static int randomInt(final int min, final int max) {
		return min + (int)(ThreadLocalRandom.current().nextDouble() * (max - min));
	}

	/**
	 * Returns <code>true</code> if a number is even.
	 */
	public static boolean isEven(final int x) {
		return (x % 2) == 0;
	}

	/**
	 * Returns <code>true</code> if a number is odd.
	 */
	public static boolean isOdd(final int x) {
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
