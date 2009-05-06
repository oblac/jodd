// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.TestCase;

/**
 * Test primitive arrays
 */
public class PrimitivearraysTest extends TestCase {

	public void testCommonOperations() {

		IntArrayList ilist = new IntArrayList();
		assertEquals(0, ilist.size());
		assertTrue(ilist.isEmpty());
		ilist.add(1);
		ilist.add(2);
		ilist.add(3);
		assertEquals(3, ilist.size());
		assertFalse(ilist.isEmpty());
		assertEquals(1, ilist.get(0));
		assertEquals(3, ilist.get(2));
		ilist.add(0, 4);
		assertEquals(1, ilist.get(1));
		assertEquals(4, ilist.get(0));

		ilist.addAll(new int[] {9, 8, 7});
		assertEquals(7, ilist.size());
		assertEquals(9, ilist.get(4));
		assertEquals(7, ilist.get(6));

		ilist.addAll(3, new int[] {-1, -2});
		assertEquals(9, ilist.size());
		assertEquals(2, ilist.get(2));
		assertEquals(-1, ilist.get(3));
		assertEquals(-2, ilist.get(4));
		assertEquals(3, ilist.get(5));
		assertEquals(9, ilist.get(6));
		assertEquals(7, ilist.get(8));

		ilist.set(3, -3);
		assertEquals(-3, ilist.get(3));

		ilist.remove(3);
		assertEquals(8, ilist.size());
		assertEquals(2, ilist.get(2));
		assertEquals(-2, ilist.get(3));
		assertEquals(3, ilist.get(4));
		assertEquals(9, ilist.get(5));
		assertEquals(7, ilist.get(7));

		ilist.removeRange(3, 3);
		assertEquals(8, ilist.size());
		ilist.removeRange(3, 5);
		assertEquals(6, ilist.size());
		assertEquals(2, ilist.get(2));
		assertEquals(9, ilist.get(3));
		assertEquals(7, ilist.get(5));

		ilist.clear();
		assertEquals(0, ilist.size());
	}


	public void testEquality() {
		IntArrayList ilist = new IntArrayList(new int[] {1,2,3,4,5,6});
		assertTrue(ilist.contains(4));
		assertFalse(ilist.contains(111));

		FloatArrayList flist = new FloatArrayList(new float[] {1f,2f, 3f, 4f, 5f});
		assertTrue(flist.contains(4f, 0.001f));
		assertFalse(flist.contains(111f, 0.001f));

	}

}
