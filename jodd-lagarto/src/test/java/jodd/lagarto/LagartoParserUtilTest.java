// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import junit.framework.TestCase;

public class LagartoParserUtilTest extends TestCase {

	public void testStartWith() {
		assertTrue(LagartoParserUtil.regionStartWith("123456", 1, 3, "2"));
		assertTrue(LagartoParserUtil.regionStartWith("123456", 0, 3, "1"));
		assertTrue(LagartoParserUtil.regionStartWith("123456", 0, 3, "12"));
		assertTrue(LagartoParserUtil.regionStartWith("123456", 0, 3, "123"));
		assertFalse(LagartoParserUtil.regionStartWith("123456", 0, 3, "1234"));
		assertFalse(LagartoParserUtil.regionStartWith("123", 0, 3, "1234"));
	}

	public void testIndexOfChar() {
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 3, 'x'));

		assertEquals(0, LagartoParserUtil.regionIndexOf("123", 0, 3, '1'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 1, 3, '1'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 2, 3, '1'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, '1'));

		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 0, 3, '2'));
		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 1, 3, '2'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 2, 3, '2'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, '2'));

		assertEquals(2, LagartoParserUtil.regionIndexOf("123", 0, 3, '3'));
		assertEquals(2, LagartoParserUtil.regionIndexOf("123", 1, 3, '3'));
		assertEquals(2, LagartoParserUtil.regionIndexOf("123", 2, 3, '3'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, '3'));

		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 2, '3'));
		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 0, 2, '2'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 1, '2'));
		assertEquals(0, LagartoParserUtil.regionIndexOf("123", 0, 1, '1'));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 0, '1'));
	}

	public void testIndexOfString() {
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 3, "x"));

		assertEquals(0, LagartoParserUtil.regionIndexOf("123", 0, 3, "1"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 1, 3, "1"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 2, 3, "1"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, "1"));

		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 0, 3, "2"));
		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 1, 3, "2"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 2, 3, "2"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, "2"));

		assertEquals(2, LagartoParserUtil.regionIndexOf("123", 0, 3, "3"));
		assertEquals(2, LagartoParserUtil.regionIndexOf("123", 1, 3, "3"));
		assertEquals(2, LagartoParserUtil.regionIndexOf("123", 2, 3, "3"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, "3"));

		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 2, "3"));
		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 0, 2, "2"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 1, "2"));
		assertEquals(0, LagartoParserUtil.regionIndexOf("123", 0, 1, "1"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 0, "1"));
	}

	public void testIndexOfString2() {
		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 0, 3, "23"));
		assertEquals(1, LagartoParserUtil.regionIndexOf("123", 1, 3, "23"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 2, 3, "23"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 3, 3, "23"));

		assertEquals("12", "123".subSequence(0,2).toString());
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 0, 2, "23"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 1, 2, "23"));
		assertEquals(-1, LagartoParserUtil.regionIndexOf("123", 2, 2, "23"));
	}
}
