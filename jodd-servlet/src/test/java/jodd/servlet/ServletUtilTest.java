// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServletUtilTest {

	@Test
	public void testPrepareParameters() {
		String[] p = new String[]{"one", "", " three ", null, "five"};
		p = ServletUtil.prepareParameters(p, true, false, false);
		assertEquals("three", p[2]);
		assertNotNull(p[1]);
		assertNull(p[3]);
		p = ServletUtil.prepareParameters(p, true, true, false);
		assertNull(p[1]);
		assertNull(p[3]);

		p = ServletUtil.prepareParameters(new String[]{"", null, "   "}, true, true, true);
		assertNull(p);
	}
}
