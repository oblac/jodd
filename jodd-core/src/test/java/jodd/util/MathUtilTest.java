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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MathUtilTest {

	@Test
	public void testOddEven() {
		assertTrue(MathUtil.isEven(0));
		assertTrue(MathUtil.isOdd(1));
		assertTrue(MathUtil.isOdd(-1));
		assertTrue(MathUtil.isEven(2));
		assertTrue(MathUtil.isEven(-2));
	}

	@Test
	public void testFactorial() {
		assertEquals(0, MathUtil.factorial(-1));
		assertEquals(1, MathUtil.factorial(0));
		assertEquals(1, MathUtil.factorial(1));
		assertEquals(2, MathUtil.factorial(2));
		assertEquals(6, MathUtil.factorial(3));
		assertEquals(3628800, MathUtil.factorial(10));
		assertEquals(1307674368000L, MathUtil.factorial(15));
	}

	@Test
	public void testParseDigit() {
		assertEquals(0, MathUtil.parseDigit('0'));
		assertEquals(1, MathUtil.parseDigit('1'));
		assertEquals(8, MathUtil.parseDigit('8'));
		assertEquals(9, MathUtil.parseDigit('9'));
		assertEquals(10, MathUtil.parseDigit('A'));
		assertEquals(10, MathUtil.parseDigit('a'));
		assertEquals(15, MathUtil.parseDigit('F'));
		assertEquals(15, MathUtil.parseDigit('f'));
	}

	@Test
	public void testRandom() {
		assertTrue(0 == MathUtil.randomInt(0, 0));
		int randomInt = MathUtil.randomInt(-10, 10);
		assertTrue(randomInt < 10);
		assertTrue(randomInt >= -10);

		assertTrue(0 == MathUtil.randomLong(0, 0));
		long randomLong = MathUtil.randomLong(-100000, 100000);
		assertTrue(randomLong < 100000);
		assertTrue(randomLong >= -100000);
	}
}
