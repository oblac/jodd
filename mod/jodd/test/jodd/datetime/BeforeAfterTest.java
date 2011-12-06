// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import junit.framework.TestCase;

public class BeforeAfterTest extends TestCase {

	public void testBefore() {
		JDateTime now = new JDateTime();
		JDateTime future = now.clone();
		future.addSecond(1);
		JDateTime future2 = now.clone();
		future2.addDay(1);

		assertFalse(now.isBefore(now));
		assertEquals(now, now);
		assertTrue(now.equalsDate(now));
		assertTrue(now.equalsTime(now));

		assertTrue(now.isBefore(future));
		assertFalse(future.isBefore(now));
		assertFalse(now.equals(future));
		assertTrue(now.equalsDate(future));
		assertFalse(now.equalsTime(future));
		assertFalse(now.isBeforeDate(future));
		assertFalse(future.isBeforeDate(now));

		assertTrue(now.isBefore(future2));
		assertFalse(future2.isBefore(now));
		assertTrue(now.isBeforeDate(future2));
		assertFalse(future2.isBeforeDate(now));
		assertFalse(now.equals(future2));
		assertFalse(now.equalsDate(future2));
		assertTrue(now.equalsTime(future2));
	}

	public void testAfter() {
		JDateTime now = new JDateTime();
		JDateTime past = now.clone();
		past.subSecond(1);
		JDateTime past2 = now.clone();
		past2.subDay(1);

		assertFalse(now.isAfter(now));
		assertEquals(now, now);
		assertTrue(now.equalsDate(now));
		assertTrue(now.equalsTime(now));

		assertTrue(now.isAfter(past));
		assertFalse(past.isAfter(now));
		assertFalse(now.equals(past));
		assertTrue(now.equalsDate(past));
		assertFalse(now.equalsTime(past));
		assertFalse(now.isAfterDate(past));
		assertFalse(past.isAfterDate(now));

		assertTrue(now.isAfter(past2));
		assertFalse(past2.isAfter(now));
		assertTrue(now.isAfterDate(past2));
		assertFalse(past2.isAfterDate(now));
		assertFalse(now.equals(past2));
		assertFalse(now.equalsDate(past2));
		assertTrue(now.equalsTime(past2));
	}
}
