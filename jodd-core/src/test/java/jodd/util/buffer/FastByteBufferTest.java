// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.buffer;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FastByteBufferTest extends FastBufferTestBase {

	@Test
	public void testAppend() {
		FastByteBuffer buff = new FastByteBuffer(3);

		buff.append(buff);
		buff.append((byte)173);
		buff.append(array((byte)8,(byte)98));

		assertArrayEquals(array((byte)173, (byte)8, (byte)98), buff.toArray());

		buff.append(buff);

		assertArrayEquals(array((byte)173, (byte)8, (byte)98, (byte)173, (byte)8, (byte)98), buff.toArray());

		buff.append(array((byte)173, (byte)5, (byte)3), 1, 1);

		assertArrayEquals(array((byte)173, (byte)8, (byte)98, (byte)173, (byte)8, (byte)98, (byte)5), buff.toArray());

		FastByteBuffer buff2 = new FastByteBuffer(3);
		buff2.append(buff);

		assertEquals(7, buff2.toArray().length);
	}

	@Test
	public void testChunks() {
		FastByteBuffer buff = new FastByteBuffer();

		assertEquals(-1, buff.index());
		assertEquals(0, buff.offset());

		buff.append((byte)1);

		assertEquals(0, buff.index());
		assertEquals(1, buff.offset());

		buff.append((byte)2);

		assertEquals(2, buff.offset());

		for (int i = 3; i <= SIZE; i++) {
			buff.append((byte)i);
		}

		assertEquals(0, buff.index());
		assertEquals(SIZE, buff.offset());

		buff.append((byte)(SIZE + 1));
		assertEquals(1, buff.index());
		assertEquals(1, buff.offset());

		byte[] a = buff.array(0);

		for (int i = 1; i <= SIZE; i++) {
			assertEquals((byte)i, a[i - 1]);
		}
	}

	@Test
	public void testChunksOverflow() {
		FastByteBuffer buff = new FastByteBuffer();

		assertEquals(-1, buff.index());
		assertEquals(0, buff.offset());

		byte sum = 0;

		for (int j = 0; j < COUNT; j++) {
			for (int i = 1; i <= SIZE; i++) {
				buff.append((byte)i);
				sum += i;
			}
		}

		assertEquals(15, buff.index());
		assertEquals(1024, buff.offset());

		buff.append((byte)-1);
		sum--;
		assertEquals(16, buff.index());
		assertEquals(1, buff.offset());

		byte sum2 = 0;

		for (int i = 0; i < buff.size(); i++) {
			sum2 += buff.get(i);
		}

		assertEquals(sum, sum2);
	}

	@Test
	public void testClear() {
		FastByteBuffer buff = new FastByteBuffer();

		assertTrue(buff.isEmpty());

		buff.append((byte)1);

		assertFalse(buff.isEmpty());

		buff.clear();

		assertTrue(buff.isEmpty());

		try {
			buff.get(0);
			fail();
		} catch (IndexOutOfBoundsException ignore) {
		}

		byte[] arr = buff.toArray();

		assertEquals(0, arr.length);
	}

	@Test
	public void testToArray() {
		FastByteBuffer buff = new FastByteBuffer();

		byte sum = 0;

		for (int j = 0; j < COUNT; j++) {
			for (int i = 1; i <= SIZE; i++) {
				buff.append((byte)i);
				sum += i;
			}
		}

		buff.append((byte)173);
		sum += 173;

		byte[] array = buff.toArray();
		byte sum2 = 0;
		for (byte l : array) {
			sum2 += l;
		}

		assertEquals(sum, sum2);


		array = buff.toArray(1, buff.size() - 2);
		sum2 = 0;
		for (byte l : array) {
			sum2 += l;
		}

		assertEquals(sum - (byte)1 - (byte)173, sum2);
	}

	@Test
	public void testToSubArray() {
		FastByteBuffer buff = new FastByteBuffer();

		int total = SIZE + (SIZE/2);

		for (int i = 1; i <= total; i++) {
			buff.append((byte)i);
		}

		byte[] array = buff.toArray(SIZE + 1, total - SIZE  - 1);

		assertEquals(total - SIZE - 1, array.length);
		assertEquals((byte)(SIZE + 2), array[0]);
	}


	protected byte[] array(byte... arr) {
		return arr;
	}

}