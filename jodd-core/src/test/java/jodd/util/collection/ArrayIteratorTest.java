// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

@SuppressWarnings("AutoBoxing")
public class ArrayIteratorTest {

	@Test
	public void testArrayIteration() {
		Integer[] i = new Integer[]{1, 2, 3, 4, 5};

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
		
		try {
			ae.remove();
			fail();
		} catch (UnsupportedOperationException nseex) {
			// ignore
		}

	}

	@Test
	public void testArrayIterationFrom() {
		Integer[] i = new Integer[]{1, 2, 3, 4, 5};

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