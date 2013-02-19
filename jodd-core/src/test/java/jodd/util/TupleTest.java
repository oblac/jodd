// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TupleTest {

	@Test
	public void testTuple2() {
		Tuple2<String, Integer> tuple2 = Tuple2.tuple("hello", Integer.valueOf(123));

		assertEquals("hello", tuple2.v1());
		assertEquals(Integer.valueOf(123), tuple2.v2());
	}

	@Test
	public void testTuple3() {
		Tuple3<String, Integer, Void> tuple3 = Tuple3.tuple("hello", Integer.valueOf(123), null);

		assertEquals("hello", tuple3.v1());
		assertEquals(Integer.valueOf(123), tuple3.v2());
		assertNull(tuple3.v3());
	}
}