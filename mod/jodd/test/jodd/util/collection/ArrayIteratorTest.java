// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.TestCase;

import java.util.NoSuchElementException;

@SuppressWarnings("AutoBoxing")
public class ArrayIteratorTest extends TestCase {

	public void testArrayIteration() {
		Integer[] i = new Integer[] {1, 2, 3, 4, 5};

		ArrayIterator<Integer> ae = new ArrayIterator<Integer>(i);
		assertTrue(ae.hasNext());
		assertEquals("1", ae.next().toString());
		assertEquals("2", ae.next().toString());
		assertEquals("3", ae.next().toString());
		assertEquals("4", ae.next().toString());
		assertTrue(ae.hasNext());
		assertEquals("5", ae.next().toString());
		assertFalse(ae.hasNext());

		try {
			ae.next();
			fail();
		} catch (NoSuchElementException nseex) {
			// ignore
		}

	}

	public void testArrayIterationFrom() {
		Integer[] i = new Integer[] {1, 2, 3, 4, 5};

		ArrayIterator<Integer> ae = new ArrayIterator<Integer>(i, 2, 2);
		assertTrue(ae.hasNext());
		assertEquals("3", ae.next().toString());
		assertEquals("4", ae.next().toString());
		assertFalse(ae.hasNext());

		try {
			ae.next();
			fail();
		} catch (NoSuchElementException nseex) {
			// ignore
		}

	}
}

