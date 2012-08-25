// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;

public class BeanUtilUtilTest extends TestCase {

	public void testIndexOfDot() {
		BeanUtilUtil buu = new BeanUtilUtil();

		assertEquals(3, buu.indexOfDot("aaa.ccc"));
		assertEquals(-1, buu.indexOfDot("aaaccc"));
		assertEquals(3, buu.indexOfDot("aaa.ccc.ddd"));
		assertEquals(9, buu.indexOfDot("aa[a.c]cc.ddd"));
		assertEquals(-1, buu.indexOfDot("aa[a.ccc.d]dd"));

	}
}
