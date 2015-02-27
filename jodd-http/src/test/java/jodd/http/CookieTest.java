// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CookieTest {

	@Test
	public void testCookieParsing() {
		Cookie cookie = new Cookie("name=value");

		assertEquals("name", cookie.getName());
		assertEquals("value", cookie.getValue());
		assertEquals(null, cookie.getExpires());

		cookie = new Cookie("name2=value2; Expires=Wed, 09 Jun 2021 10:18:14 GMT");

		assertEquals("name2", cookie.getName());
		assertEquals("value2", cookie.getValue());
		assertEquals("Wed, 09 Jun 2021 10:18:14 GMT", cookie.getExpires());

		cookie = new Cookie("LSID=DQAAAEaem_vYg; Path=/accounts; Secure; Expires=Wed, 13 Jan 2021 22:23:01 GMT; HttpOnly");

		assertEquals("LSID", cookie.getName());
		assertEquals("DQAAAEaem_vYg", cookie.getValue());
		assertEquals("/accounts", cookie.getPath());
		assertTrue(cookie.isSecure());
		assertTrue(cookie.isHttpOnly());
	}
}