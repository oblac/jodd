// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;
import jodd.util.collection.SimpleQueue;
import jodd.util.collection.SimpleStack;

public class StackQueueTest extends TestCase {

	public void testQueue() {
		SimpleQueue sq = new SimpleQueue();
		assertEquals(0, sq.size());
		sq.put("Hallo!");					// first in
		assertEquals(1, sq.size());
		sq.put("Tschuss!");
		assertEquals(2, sq.size());
		sq.put("End");
		assertEquals(3, sq.size());
		assertEquals("Hallo!", sq.get());	// first out
		assertEquals(2, sq.size());
		assertEquals("Tschuss!", sq.peek());
		assertEquals(2, sq.size());
		assertEquals("Tschuss!", sq.get());
		assertEquals(1, sq.size());
		assertFalse(sq.isEmpty());
		assertEquals("End", sq.get());
		assertEquals(0, sq.size());
		assertTrue(sq.isEmpty());
	}

	public void testStack() {
		SimpleStack ss = new SimpleStack();
		assertEquals(0, ss.size());
		ss.push("Start");
		assertEquals(1, ss.size());
		ss.push("Hallo!");
		assertEquals(2, ss.size());
		ss.push("Tschuss!");				// last in
		assertEquals(3, ss.size());
		assertEquals("Tschuss!", ss.pop());	// first out
		assertEquals(2, ss.size());
		assertEquals("Hallo!", ss.peek());
		assertEquals(2, ss.size());
		assertEquals("Hallo!", ss.pop());
		assertEquals(1, ss.size());
		assertFalse(ss.isEmpty());
		assertEquals("Start", ss.pop());
		assertEquals(0, ss.size());
		assertTrue(ss.isEmpty());
	}

}
