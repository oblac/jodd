// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import org.junit.Test;

import static org.junit.Assert.*;

public class LagartoParserUtilTest {

	private static boolean regionStartWith(String string, int from, int to, String match) {
		return LagartoParserUtil.regionStartWith(string.toCharArray(), from, to, match.toCharArray());
	}

	private static int regionIndexOf(String string, int from, int to, char c) {
		return LagartoParserUtil.regionIndexOf(string.toCharArray(), from, to, c);
	}

	private static int regionIndexOf(String string, int from, int to, String match) {
		return LagartoParserUtil.regionIndexOf(string.toCharArray(), from, to, match.toCharArray());
	}

	@Test
	public void testStartWith() {
		assertTrue(regionStartWith("123456", 1, 3, "2"));
		assertTrue(regionStartWith("123456", 0, 3, "1"));
		assertTrue(regionStartWith("123456", 0, 3, "12"));
		assertTrue(regionStartWith("123456", 0, 3, "123"));
		assertFalse(regionStartWith("123456", 0, 3, "1234"));
		assertFalse(regionStartWith("123", 0, 3, "1234"));
	}

	@Test
	public void testIndexOfChar() {
		assertEquals(-1, regionIndexOf("123", 0, 3, 'x'));

		assertEquals(0, regionIndexOf("123", 0, 3, '1'));
		assertEquals(-1, regionIndexOf("123", 1, 3, '1'));
		assertEquals(-1, regionIndexOf("123", 2, 3, '1'));
		assertEquals(-1, regionIndexOf("123", 3, 3, '1'));

		assertEquals(1, regionIndexOf("123", 0, 3, '2'));
		assertEquals(1, regionIndexOf("123", 1, 3, '2'));
		assertEquals(-1, regionIndexOf("123", 2, 3, '2'));
		assertEquals(-1, regionIndexOf("123", 3, 3, '2'));

		assertEquals(2, regionIndexOf("123", 0, 3, '3'));
		assertEquals(2, regionIndexOf("123", 1, 3, '3'));
		assertEquals(2, regionIndexOf("123", 2, 3, '3'));
		assertEquals(-1, regionIndexOf("123", 3, 3, '3'));

		assertEquals(-1, regionIndexOf("123", 0, 2, '3'));
		assertEquals(1, regionIndexOf("123", 0, 2, '2'));
		assertEquals(-1, regionIndexOf("123", 0, 1, '2'));
		assertEquals(0, regionIndexOf("123", 0, 1, '1'));
		assertEquals(-1, regionIndexOf("123", 0, 0, '1'));
	}

	@Test
	public void testIndexOfString() {
		assertEquals(-1, regionIndexOf("123", 0, 3, "x"));

		assertEquals(0, regionIndexOf("123", 0, 3, "1"));
		assertEquals(-1, regionIndexOf("123", 1, 3, "1"));
		assertEquals(-1, regionIndexOf("123", 2, 3, "1"));
		assertEquals(-1, regionIndexOf("123", 3, 3, "1"));

		assertEquals(1, regionIndexOf("123", 0, 3, "2"));
		assertEquals(1, regionIndexOf("123", 1, 3, "2"));
		assertEquals(-1, regionIndexOf("123", 2, 3, "2"));
		assertEquals(-1, regionIndexOf("123", 3, 3, "2"));

		assertEquals(2, regionIndexOf("123", 0, 3, "3"));
		assertEquals(2, regionIndexOf("123", 1, 3, "3"));
		assertEquals(2, regionIndexOf("123", 2, 3, "3"));
		assertEquals(-1, regionIndexOf("123", 3, 3, "3"));

		assertEquals(-1, regionIndexOf("123", 0, 2, "3"));
		assertEquals(1, regionIndexOf("123", 0, 2, "2"));
		assertEquals(-1, regionIndexOf("123", 0, 1, "2"));
		assertEquals(0, regionIndexOf("123", 0, 1, "1"));
		assertEquals(-1, regionIndexOf("123", 0, 0, "1"));
	}

	@Test
	public void testIndexOfString2() {
		assertEquals(1, regionIndexOf("123", 0, 3, "23"));
		assertEquals(1, regionIndexOf("123", 1, 3, "23"));
		assertEquals(-1, regionIndexOf("123", 2, 3, "23"));
		assertEquals(-1, regionIndexOf("123", 3, 3, "23"));

		assertEquals("12", "123".subSequence(0, 2).toString());
		assertEquals(-1, regionIndexOf("123", 0, 2, "23"));
		assertEquals(-1, regionIndexOf("123", 1, 2, "23"));
		assertEquals(-1, regionIndexOf("123", 2, 2, "23"));
	}
}
