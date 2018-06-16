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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FastDoubleBufferTest extends FastBufferTestBase {

	@Test
	void testAppend() {
		FastDoubleBuffer buff = new FastDoubleBuffer(3);

		buff.append(buff);
		buff.append(173);
		buff.append(array(8, 98));

		assertArrayEquals(array((double)173, (double)8, (double)98), buff.toArray(), 0.1);

		buff.append(buff);

		assertArrayEquals(array((double)173, (double)8, (double)98, (double)173, (double)8, (double)98), buff.toArray(), 0.1);

		buff.append(array(173, 5, 3), 1, 1);

		assertArrayEquals(array((double)173, (double)8, (double)98, (double)173, (double)8, (double)98, (double)5), buff.toArray(), 0.1);

		FastDoubleBuffer buff2 = new FastDoubleBuffer(3);
		buff2.append(buff);

		assertEquals(7, buff2.toArray().length);
	}


	@Test
	void testClear() {
		FastDoubleBuffer buff = new FastDoubleBuffer();

		assertTrue(buff.isEmpty());

		buff.append(1);

		assertFalse(buff.isEmpty());

		buff.clear();

		assertTrue(buff.isEmpty());

		try {
			buff.get(0);
			fail("error");
		} catch (IndexOutOfBoundsException ignore) {
		}

		double[] arr = buff.toArray();

		assertEquals(0, arr.length);
	}

	@Test
	void testToArray() {
		FastDoubleBuffer buff = new FastDoubleBuffer();

		int sum = 0;

		for (int j = 0; j < COUNT; j++) {
			for (int i = 1; i <= SIZE; i++) {
				buff.append(i);
				sum += i;
			}
		}

		buff.append(173);
		sum += 173;

		double[] array = buff.toArray();
		int sum2 = 0;
		for (double l : array) {
			sum2 += l;
		}

		assertEquals(sum, sum2);


		array = buff.toArray(1, buff.size() - 2);
		sum2 = 0;
		for (double l : array) {
			sum2 += l;
		}

		assertEquals(sum - 1 - 173, sum2);
	}

	@Test
	void testToSubArray() {
		FastDoubleBuffer buff = new FastDoubleBuffer();

		int total = SIZE + (SIZE / 2);

		for (int i = 1; i <= total; i++) {
			buff.append(i);
		}

		double[] array = buff.toArray(SIZE + 1, total - SIZE - 1);

		assertEquals(total - SIZE - 1, array.length);
		assertEquals(SIZE + 2, array[0], 0.1);
	}

	@Test
	void testBig() {
		List<Double> l = new ArrayList<>();
		FastDoubleBuffer fbf = new FastDoubleBuffer();

		Random rnd = new Random();
		for (int i = 0; i < 100_000; i++) {
			int n = rnd.nextInt();
			l.add((double)n);
			fbf.append((double)n);
		}

		for (int i = 0; i < l.size(); i++) {
			assertEquals(l.get(i).doubleValue(), fbf.get(i));
		}
	}


	protected double[] array(double... arr) {
		return arr;
	}
}
