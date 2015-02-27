// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.buffer;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
			fail();
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