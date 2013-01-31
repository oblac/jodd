// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectUtilTest {

	@Test
	public void testEquals() {
		Object a = new Integer(173);
		Object b = new Integer(1);
		Object c = new Integer(173);

		assertTrue(ObjectUtil.equals(a, a));
		assertTrue(ObjectUtil.equals(a, c));
		assertTrue(ObjectUtil.equals(c, a));
		assertFalse(ObjectUtil.equals(a, b));
		assertFalse(ObjectUtil.equals(b, a));

		assertFalse(ObjectUtil.equals(a, null));
		assertFalse(ObjectUtil.equals(null, a));

		assertTrue(ObjectUtil.equalsEx(null, null));
	}
}
