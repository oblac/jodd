// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class ArraysUtilTest extends TestCase {

	int[] x;
	int[] y;

	Long[] xx;
	Long[] yy;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
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

	protected void assertEquals(int[] expected, int[] provided) {
		assertEquals(expected.length, provided.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], provided[i]);
		}
	}

	protected void assertEquals(long[] expected, long[] provided) {
		assertEquals(expected.length, provided.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], provided[i]);
		}
	}

	public void testJoin() {
		assertEquals(new int[] {1,2,3,4,5,11,12,13}, ArraysUtil.join(x, y));
		assertEquals(new long[] {1,2,3,4,5,11,12,13}, ArraysUtil.values(ArraysUtil.join(xx, yy)));
	}

	public void testMerge() {
		assertEquals(new int[] {1,2,3,4,5,11,12,13}, ArraysUtil.merge(x, y));
		assertEquals(new long[] {1,2,3,4,5,11,12,13}, ArraysUtil.values(ArraysUtil.merge(xx, yy)));
		assertEquals(new int[] {1,2,3,4,5,11,12,13,11,12,13}, ArraysUtil.merge(x, y, y));
		assertEquals(new long[] {1,2,3,4,5,11,12,13,11,12,13}, ArraysUtil.values(ArraysUtil.merge(xx, yy, yy)));
	}

	public void testAppend() {
		assertEquals(new int[] {1,2,3,4,5,100}, ArraysUtil.append(x, 100));
		assertEquals(new long[] {1,2,3,4,5,100}, ArraysUtil.values(ArraysUtil.append(xx, Long.valueOf(100))));
	}

	public void testResize() {
		assertEquals(new int[] {1,2,3}, ArraysUtil.resize(x, 3));
		assertEquals(new long[] {1,2,3}, ArraysUtil.values(ArraysUtil.resize(xx, 3)));
		assertEquals(new int[] {1,2,3,4,5,0,0}, ArraysUtil.resize(x, 7));
		assertEquals(new long[] {1,2,3,4,5,0,0}, ArraysUtil.values(ArraysUtil.resize(xx, 7)));
		assertEquals(new int[] {}, ArraysUtil.resize(x, 0));
	}

	public void testSub() {
		assertEquals(new int[] {2,3,4}, ArraysUtil.subarray(x, 1, 3));
		assertEquals(new long[] {2,3,4}, ArraysUtil.values(ArraysUtil.subarray(xx, 1, 3)));
	}

	public void testInsert() {
		assertEquals(new int[] {1,2,3,11,12,13,4,5}, ArraysUtil.insert(x, y, 3));
		assertEquals(new int[] {11,12,13,1,2,3,4,5}, ArraysUtil.insert(x, y, 0));
		assertEquals(new int[] {1,2,3,4,5,11,12,13}, ArraysUtil.insert(x, y, 5));
	}

	public void testInsertAt() {
		assertEquals(new int[] {1,2,3,11,12,13,5}, ArraysUtil.insertAt(x, y, 3));
		assertEquals(new int[] {11,12,13,2,3,4,5}, ArraysUtil.insertAt(x, y, 0));
		assertEquals(new int[] {1,2,3,4,11,12,13}, ArraysUtil.insertAt(x, y, 4));
	}


	public void testIndexOf() {
		assertEquals(0, ArraysUtil.indexOf(x, 1));
		assertEquals(1, ArraysUtil.indexOf(x, 2));
		assertEquals(4, ArraysUtil.indexOf(x, 5));
		assertEquals(-1, ArraysUtil.indexOf(x, 6));
		assertEquals(1, ArraysUtil.indexOf(xx, Long.valueOf(2)));
		assertEquals(-1, ArraysUtil.indexOf(xx, Long.valueOf(12)));
		assertEquals(1, ArraysUtil.indexOf(yy, Long.valueOf(12)));
		assertEquals(-1, ArraysUtil.indexOf(yy, Long.valueOf(12), 2));
	}

	public void testIndexOf2() {
		assertEquals(0, ArraysUtil.indexOf(x, new int[] {}));
		assertEquals(0, ArraysUtil.indexOf(x, new int[] {1,2,3}));
		assertEquals(-1, ArraysUtil.indexOf(x, new int[] {1,2,3,7}));

		assertEquals(1, ArraysUtil.indexOf(x, new int[] {2,3}));
		assertEquals(4, ArraysUtil.indexOf(x, new int[] {5}));
	}

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
	public void testContains2() {
		assertTrue(ArraysUtil.contains(x, new int[] {}));
		assertTrue(ArraysUtil.contains(x, new int[] {1,2,3}));
		assertFalse(ArraysUtil.contains(x, new int[] {1,2,3,7}));

		assertTrue(ArraysUtil.contains(x, new int[] {2,3}));
		assertTrue(ArraysUtil.contains(x, new int[] {5}));
	}


	public void testConvert() {
		Integer[] src = new Integer[] {Integer.valueOf(1), null, Integer.valueOf(3)};
		int[] dest = ArraysUtil.values(src);
		assertEquals(3, dest.length);
		assertEquals(1, dest[0]);
		assertEquals(0, dest[1]);
		assertEquals(3, dest[2]);

		src = ArraysUtil.valuesOf(dest);
		assertEquals(3, src.length);
		assertEquals(1, src[0].intValue());
		assertEquals(0, src[1].intValue());
		assertEquals(3, src[2].intValue());

	}

	public void testToString() {
		assertEquals("1, 2, 3", ArraysUtil.toString(new int[] {1,2,3}));
		assertEquals("1, null, 3.1", ArraysUtil.toString(new Object[] {1,null,3.1}));
		assertEquals("null", ArraysUtil.toString((long[]) null));
	}

}
