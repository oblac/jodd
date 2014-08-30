// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.buffer;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FastDoubleBufferTest extends FastBufferTestBase {

	@Test
	public void testAppend() {
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
	public void testChunks() {
		FastDoubleBuffer buff = new FastDoubleBuffer();

		assertEquals(-1, buff.index());
		assertEquals(0, buff.offset());

		buff.append(1);

		assertEquals(0, buff.index());
		assertEquals(1, buff.offset());

		buff.append(2);

		assertEquals(2, buff.offset());

		for (int i = 3; i <= SIZE; i++) {
			buff.append(i);
		}

		assertEquals(0, buff.index());
		assertEquals(SIZE, buff.offset());

		buff.append(SIZE + 1);
		assertEquals(1, buff.index());
		assertEquals(1, buff.offset());

		double[] a = buff.array(0);

		for (int i = 1; i <= SIZE; i++) {
			assertEquals(i, a[i - 1], 0.1);
		}
	}

	@Test
	public void testChunksOverflow() {
		FastDoubleBuffer buff = new FastDoubleBuffer();

		assertEquals(-1, buff.index());
		assertEquals(0, buff.offset());

		int sum = 0;

		for (int j = 0; j < COUNT; j++) {
			for (int i = 1; i <= SIZE; i++) {
				buff.append(i);
				sum += i;
			}
		}

		assertEquals(15, buff.index());
		assertEquals(1024, buff.offset());

		buff.append(-1);
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
		FastDoubleBuffer buff = new FastDoubleBuffer();

		assertTrue(buff.isEmpty());

		buff.append(1);

		assertFalse(buff.isEmpty());

		buff.clear();

		assertTrue(buff.isEmpty());

		try {
			buff.get(0);
			fail();
		} catch (IndexOutOfBoundsException ignore) {
		}

		double[] arr = buff.toArray();

		assertEquals(0, arr.length);
	}

	@Test
	public void testToArray() {
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
	public void testToSubArray() {
		FastDoubleBuffer buff = new FastDoubleBuffer();

		int total = SIZE + (SIZE / 2);

		for (int i = 1; i <= total; i++) {
			buff.append(i);
		}

		double[] array = buff.toArray(SIZE + 1, total - SIZE - 1);

		assertEquals(total - SIZE - 1, array.length);
		assertEquals(SIZE + 2, array[0], 0.1);
	}


	protected double[] array(double... arr) {
		return arr;
	}
}