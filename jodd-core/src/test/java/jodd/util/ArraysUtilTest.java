// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ArraysUtilTest {

	int[] x;
	int[] y;

	Long[] xx;
	Long[] yy;

	@Before
	public void setUp() throws Exception {
		x = new int[5];
		xx = new Long[5];
		for (int i = 0; i < x.length; i++) {
			x[i] = i + 1;
			xx[i] = Long.valueOf(x[i]);
		}
		y = new int[3];
		yy = new Long[3];
		for (int i = 0; i < y.length; i++) {
			y[i] = 11 + i;
			yy[i] = Long.valueOf(y[i]);
		}
	}

	@Test
	public void testJoin() {
		assertArrayEquals(new int[] {1, 2, 3, 4, 5, 11, 12, 13}, ArraysUtil.join(x, y));
		assertArrayEquals(new long[]{1, 2, 3, 4, 5, 11, 12, 13}, ArraysUtil.values(ArraysUtil.join(xx, yy)));
	}

	@Test
	public void testMerge() {
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 11, 12, 13}, ArraysUtil.merge(x, y));
		assertArrayEquals(new long[]{1, 2, 3, 4, 5, 11, 12, 13}, ArraysUtil.values(ArraysUtil.merge(xx, yy)));
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 11, 12, 13, 11, 12, 13}, ArraysUtil.merge(x, y, y));
		assertArrayEquals(new long[]{1, 2, 3, 4, 5, 11, 12, 13, 11, 12, 13}, ArraysUtil.values(ArraysUtil.merge(xx, yy, yy)));
	}

	@Test
	public void testAppend() {
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 100}, ArraysUtil.append(x, 100));
		assertArrayEquals(new long[]{1, 2, 3, 4, 5, 100}, ArraysUtil.values(ArraysUtil.append(xx, Long.valueOf(100))));
	}

	@Test
	public void testResize() {
		assertArrayEquals(new int[]{1, 2, 3}, ArraysUtil.resize(x, 3));
		assertArrayEquals(new long[]{1, 2, 3}, ArraysUtil.values(ArraysUtil.resize(xx, 3)));
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 0, 0}, ArraysUtil.resize(x, 7));
		assertArrayEquals(new long[]{1, 2, 3, 4, 5, 0, 0}, ArraysUtil.values(ArraysUtil.resize(xx, 7)));
		assertArrayEquals(new int[]{}, ArraysUtil.resize(x, 0));
	}

	@Test
	public void testSub() {
		assertArrayEquals(new int[]{2, 3, 4}, ArraysUtil.subarray(x, 1, 3));
		assertArrayEquals(new long[]{2, 3, 4}, ArraysUtil.values(ArraysUtil.subarray(xx, 1, 3)));
	}

	@Test
	public void testInsert() {
		assertArrayEquals(new int[]{1, 2, 3, 11, 12, 13, 4, 5}, ArraysUtil.insert(x, y, 3));
		assertArrayEquals(new int[]{11, 12, 13, 1, 2, 3, 4, 5}, ArraysUtil.insert(x, y, 0));
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 11, 12, 13}, ArraysUtil.insert(x, y, 5));

		assertArrayEquals(new int[]{1, 2, 3, 173, 4, 5}, ArraysUtil.insert(x, 173, 3));
		assertArrayEquals(new int[]{173, 1, 2, 3, 4, 5}, ArraysUtil.insert(x, 173, 0));
		assertArrayEquals(new int[]{1, 2, 3, 4, 5, 173}, ArraysUtil.insert(x, 173, 5));
	}

	@Test
	public void testInsertAt() {
		assertArrayEquals(new int[]{1, 2, 3, 11, 12, 13, 5}, ArraysUtil.insertAt(x, y, 3));
		assertArrayEquals(new int[]{11, 12, 13, 2, 3, 4, 5}, ArraysUtil.insertAt(x, y, 0));
		assertArrayEquals(new int[]{1, 2, 3, 4, 11, 12, 13}, ArraysUtil.insertAt(x, y, 4));
	}


	@Test
	public void testIndexOf() {
		Assert.assertEquals(0, ArraysUtil.indexOf(x, 1));
		Assert.assertEquals(1, ArraysUtil.indexOf(x, 2));
		Assert.assertEquals(4, ArraysUtil.indexOf(x, 5));
		Assert.assertEquals(-1, ArraysUtil.indexOf(x, 6));
		Assert.assertEquals(1, ArraysUtil.indexOf(xx, Long.valueOf(2)));
		Assert.assertEquals(-1, ArraysUtil.indexOf(xx, Long.valueOf(12)));
		Assert.assertEquals(1, ArraysUtil.indexOf(yy, Long.valueOf(12)));
		Assert.assertEquals(-1, ArraysUtil.indexOf(yy, Long.valueOf(12), 2));
	}

	@Test
	public void testIndexOf2() {
		Assert.assertEquals(0, ArraysUtil.indexOf(x, new int[]{}));
		Assert.assertEquals(0, ArraysUtil.indexOf(x, new int[]{1, 2, 3}));
		Assert.assertEquals(-1, ArraysUtil.indexOf(x, new int[]{1, 2, 3, 7}));

		Assert.assertEquals(1, ArraysUtil.indexOf(x, new int[]{2, 3}));
		Assert.assertEquals(4, ArraysUtil.indexOf(x, new int[]{5}));
	}

	@Test
	public void testContains() {
		assertTrue(ArraysUtil.contains(x, 1));
		assertTrue(ArraysUtil.contains(x, 2));
		assertTrue(ArraysUtil.contains(x, 5));
		assertFalse(ArraysUtil.contains(x, 6));
		assertTrue(ArraysUtil.contains(xx, Long.valueOf(3)));
		assertFalse(ArraysUtil.contains(xx, Long.valueOf(13)));
		assertTrue(ArraysUtil.contains(yy, Long.valueOf(13)));
		assertFalse(ArraysUtil.contains(yy, Long.valueOf(13), 3));
	}

	@Test
	public void testContains2() {
		assertTrue(ArraysUtil.contains(x, new int[]{}));
		assertTrue(ArraysUtil.contains(x, new int[]{1, 2, 3}));
		assertFalse(ArraysUtil.contains(x, new int[]{1, 2, 3, 7}));

		assertTrue(ArraysUtil.contains(x, new int[]{2, 3}));
		assertTrue(ArraysUtil.contains(x, new int[]{5}));
	}


	@Test
	public void testConvert() {
		Integer[] src = new Integer[]{Integer.valueOf(1), null, Integer.valueOf(3)};
		int[] dest = ArraysUtil.values(src);
		Assert.assertEquals(3, dest.length);
		Assert.assertEquals(1, dest[0]);
		Assert.assertEquals(0, dest[1]);
		Assert.assertEquals(3, dest[2]);

		src = ArraysUtil.valuesOf(dest);
		Assert.assertEquals(3, src.length);
		Assert.assertEquals(1, src[0].intValue());
		Assert.assertEquals(0, src[1].intValue());
		Assert.assertEquals(3, src[2].intValue());

	}

	@Test
	public void testToString() {
		Assert.assertEquals("1, 2, 3", ArraysUtil.toString(new int[]{1, 2, 3}));
		Assert.assertEquals("1, null, 3.1", ArraysUtil.toString(new Object[]{1, null, 3.1}));
		Assert.assertEquals("null", ArraysUtil.toString((long[]) null));
	}

	@Test
	public void testRemove() {
		assertArrayEquals(new int[]{1, 2, 5}, ArraysUtil.remove(x, 2, 2));
		assertArrayEquals(new int[]{1}, ArraysUtil.remove(x, 1, 4));
		assertArrayEquals(new long[]{1, 3, 4, 5}, ArraysUtil.values(ArraysUtil.remove(xx, 1, 1)));
	}

}
