// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ArrayEnumerationTest {

	@Test
	public void testEnumeration() {
		Integer[] i = new Integer[]{1, 2, 3, 4, 5};

		ArrayEnumeration<Integer> ae = new ArrayEnumeration<Integer>(i);
		assertTrue(ae.hasMoreElements());
		assertEquals("1", ae.nextElement().toString());
		assertEquals("2", ae.nextElement().toString());
		assertEquals("3", ae.nextElement().toString());
		assertEquals("4", ae.nextElement().toString());
		assertTrue(ae.hasMoreElements());
		assertEquals("5", ae.nextElement().toString());
		assertFalse(ae.hasMoreElements());

		try {
			ae.nextElement();
			fail();
		} catch (NoSuchElementException nseex) {
			// ignore
		}

	}

	@Test
	public void testEnumerationFrom() {
		Integer[] i = new Integer[]{1, 2, 3, 4, 5};

		ArrayEnumeration<Integer> ae = new ArrayEnumeration<Integer>(i, 2, 2);
		assertTrue(ae.hasMoreElements());
		assertEquals("3", ae.nextElement().toString());
		assertEquals("4", ae.nextElement().toString());
		assertFalse(ae.hasMoreElements());

		try {
			ae.nextElement();
			fail();
		} catch (NoSuchElementException nseex) {
			// ignore
		}

	}
}
