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

package jodd.util.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FastShortBufferTest  extends FastBufferTestBase {

	@Test
	public void testAppend() {
		FastShortBuffer buff = new FastShortBuffer(3);

		buff.append(buff);
		buff.append((short) 173);
		buff.append(array((short)8,(short)98));

		assertArrayEquals(array((short)173, (short)8, (short)98), buff.toArray());

		buff.append(buff);

		assertArrayEquals(array((short)173, (short)8, (short)98, (short)173, (short)8, (short)98), buff.toArray());

		buff.append(array((short)173, (short)5, (short)3), 1, 1);

		assertArrayEquals(array((short)173, (short)8, (short)98, (short)173, (short)8, (short)98, (short)5), buff.toArray());

		FastShortBuffer buff2 = new FastShortBuffer(3);
		buff2.append(buff);

		assertEquals(7, buff2.toArray().length);
	}

	@Test
	public void testChunks() {
		FastShortBuffer buff = new FastShortBuffer();

		assertEquals(-1, buff.index());
		assertEquals(0, buff.offset());

		buff.append((short)1);

		assertEquals(0, buff.index());
		assertEquals(1, buff.offset());

		buff.append((short)2);

		assertEquals(2, buff.offset());

		for (int i = 3; i <= SIZE; i++) {
			buff.append((short)i);
		}

		assertEquals(0, buff.index());
		assertEquals(SIZE, buff.offset());

		buff.append((short)(SIZE + 1));
		assertEquals(1, buff.index());
		assertEquals(1, buff.offset());

		short[] a = buff.array(0);

		for (int i = 1; i <= SIZE; i++) {
			assertEquals(i, a[i - 1]);
		}
	}

	@Test
	public void testChunksOverflow() {
		FastShortBuffer buff = new FastShortBuffer();

		assertEquals(-1, buff.index());
		assertEquals(0, buff.offset());

		int sum = 0;

		for (int j = 0; j < COUNT; j++) {
			for (int i = 1; i <= SIZE; i++) {
				buff.append((short)i);
				sum += i;
			}
		}

		assertEquals(15, buff.index());
		assertEquals(1024, buff.offset());

		buff.append((short)-1);
		sum--;
		assertEquals(16, buff.index());
		assertEquals(1, buff.offset());

		int sum2 = 0;

		for (int i = 0; i < buff.size(); i++) {
			sum2 += buff.get(i);
		}

		assertEquals(sum, sum2);
	}

	@Test
	public void testClear() {
		FastShortBuffer buff = new FastShortBuffer();

		assertTrue(buff.isEmpty());

		buff.append((short)1);

		assertFalse(buff.isEmpty());

		buff.clear();

		assertTrue(buff.isEmpty());

		try {
			buff.get(0);
			fail("error");
		} catch (IndexOutOfBoundsException ignore) {
		}

		short[] arr = buff.toArray();

		assertEquals(0, arr.length);
	}

	@Test
	public void testToArray() {
		FastShortBuffer buff = new FastShortBuffer();

		int sum = 0;

		for (int j = 0; j < COUNT; j++) {
			for (int i = 1; i <= SIZE; i++) {
				buff.append((short)i);
				sum += i;
			}
		}

		buff.append((short)173);
		sum += 173;

		short[] array = buff.toArray();
		int sum2 = 0;
		for (short l : array) {
			sum2 += l;
		}

		assertEquals(sum, sum2);


		array = buff.toArray(1, buff.size() - 2);
		sum2 = 0;
		for (short l : array) {
			sum2 += l;
		}

		assertEquals(sum - 1 - 173, sum2);
	}

	@Test
	public void testToSubArray() {
		FastShortBuffer buff = new FastShortBuffer();

		int total = SIZE + (SIZE/2);

		for (int i = 1; i <= total; i++) {
			buff.append((short)i);
		}

		short[] array = buff.toArray(SIZE + 1, total - SIZE  - 1);

		assertEquals(total - SIZE - 1, array.length);
		assertEquals(SIZE + 2, array[0]);
	}


	protected short[] array(short... arr) {
		return arr;
	}

}
