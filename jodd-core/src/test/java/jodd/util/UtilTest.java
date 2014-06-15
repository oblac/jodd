// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilTest {

	@Test
	public void testEquals() {
		Object a = new Integer(173);
		Object b = new Integer(1);
		Object c = new Integer(173);

		assertTrue(Util.equals(a, a));
		assertTrue(Util.equals(a, c));
		assertTrue(Util.equals(c, a));
		assertFalse(Util.equals(a, b));
		assertFalse(Util.equals(b, a));

		assertFalse(Util.equals(a, null));
		assertFalse(Util.equals(null, a));

		assertTrue(Util.equals(null, null));
	}

}
