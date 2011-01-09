// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.TestCase;

import java.util.NoSuchElementException;

public class ArrayEnumerationTest extends TestCase {

	public void testEnumeration() {
		Integer[] i = new Integer[] {1,2,3,4,5};

		ArrayEnumeration ae = new ArrayEnumeration(i);
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

	public void testEnumerationFrom() {
		Integer[] i = new Integer[] {1,2,3,4,5};

		ArrayEnumeration ae = new ArrayEnumeration(i, 2, 4);
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
