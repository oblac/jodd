// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RuntimeUtilTest {

	@Test
	public void testJoddLocation() {
		String loc = RuntimeUtil.joddLocation();
		assertTrue(loc.contains("jodd"));
	}
}