// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class LoopIteratorTest extends TestCase {

	public void testUp() {
		LoopIterator ls = new LoopIterator(1, 5);

		assertTrue(ls.next());	// jump to 1
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertTrue(ls.next());	// 2
		assertTrue(ls.next());	// 3
		assertTrue(ls.next());	// 4
		assertTrue(ls.next());	// jump to 5

		assertFalse(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(5, ls.getValue());
		assertEquals(5, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertFalse(ls.next());

		ls.reset();

		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());
	}

	public void testDown() {
		LoopIterator ls = new LoopIterator(5, 1, -1);

		assertTrue(ls.next());	// jump to 5
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(5, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertTrue(ls.next());
		assertTrue(ls.next());
		assertTrue(ls.next());
		assertTrue(ls.next());	// jump to 1

		assertFalse(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(5, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertFalse(ls.next());

		ls.reset();

		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(5, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());
	}

	public void testSingle() {
		LoopIterator ls = new LoopIterator(1, 5, 20, 10);
		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertFalse(ls.next());

		ls = new LoopIterator(1, 1);
		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertFalse(ls.next());

		ls = new LoopIterator(1, 1, -1);
		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
	}

	public void testNone() {
		LoopIterator ls = new LoopIterator(2, 1);
		assertFalse(ls.next());

		ls = new LoopIterator(1, 3, -1);
		assertFalse(ls.next());
	}
}

