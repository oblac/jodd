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

package jodd.buffer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FastBuffersTest {

	@Test
	void testEmptyBuffer() {
		FastLongBuffer flb = new FastLongBuffer();

		assertEquals(0, flb.size());
		assertTrue(flb.isEmpty());
		assertArrayEquals(new long[0], flb.toArray());
	}

	@Test
	void testCommon() {
		FastIntBuffer fib = new FastIntBuffer(2);
		fib.append(1);
		fib.append(2);
		fib.append(3);

		fib.append(new int[]{4, 5, 6, 7, 8, 9});
		fib.append(new int[]{10, 11, 12, 13, 14, 15}, 3, 1);

		int[] expected = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 13};

		assertEquals(10, fib.size());
		assertTrue(Arrays.equals(expected, fib.toArray()));

		fib.clear();
		assertEquals(0, fib.size());
	}

	@Test
	void testAt() {
		FastCharBuffer fcb = new FastCharBuffer(2);
		fcb.append("12abc");

		assertEquals('1', fcb.charAt(0));
		assertEquals('2', fcb.charAt(1));
		assertEquals('a', fcb.charAt(2));
		assertEquals('b', fcb.charAt(3));
		assertEquals('c', fcb.charAt(4));

		assertEquals("12abc", fcb.toString());
	}

	@Test
	void testAtExceptions() {
		FastCharBuffer fcb = new FastCharBuffer();

		try {
			fcb.charAt(-1);
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
		}

		try {
			fcb.charAt(0);
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
		}

		try {
			fcb.charAt(1);
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
		}

		fcb.append('a');
		assertEquals('a', fcb.charAt(0));

		try {
			fcb.charAt(1);
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
		}
	}

	@Test
	void testArray() {
		String str = "12abcd12345678qw";
		FastCharBuffer fcb = new FastCharBuffer(2);
		fcb.append(str);

		assertEquals(16, fcb.length());
		assertEquals(str, fcb.toString());

		assertEquals(str.subSequence(3, 4).toString(), fcb.subSequence(3, 4).toString());
		for (int i = 0; i < 16; i++) {
			for (int j = i; j < 16; j++) {
				assertEquals(str.subSequence(i, j).toString(), fcb.subSequence(i, j).toString());
			}
		}
	}

	@Test
	void testAppend() {
		String str = "1AB123412345678QWER";
		FastCharBuffer fcb = new FastCharBuffer(1);
		fcb.append(str);
		assertEquals("1AB123412345678QWER", fcb.toString());

		FastCharBuffer fcb2 = new FastCharBuffer(1);
		fcb2.append("qASzxcvPOIUY");
		fcb2.append(fcb);

		assertEquals("qASzxcvPOIUY1AB123412345678QWER", fcb2.toString());
	}

}
