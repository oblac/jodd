// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class MathUtilTest extends TestCase {

	public void testOddEven() {
		assertTrue(MathUtil.isEven(0));
		assertTrue(MathUtil.isOdd(1));
		assertTrue(MathUtil.isOdd(-1));
		assertTrue(MathUtil.isEven(2));
		assertTrue(MathUtil.isEven(-2));
	}
}
