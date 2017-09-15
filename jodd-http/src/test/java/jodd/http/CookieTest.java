// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	public void test395() {
		Cookie cookie = new Cookie("name=value;");

		assertEquals("name", cookie.getName());
		assertEquals("value", cookie.getValue());

		cookie = new Cookie("name=value;       ");

		assertEquals("name", cookie.getName());
		assertEquals("value", cookie.getValue());

		cookie = new Cookie("p_skey=UIJeeZgODkPQgiVcwHJBhq9mYrZC9JdpYF6SCZ3fNfY_; PATH=/; DOMAIN=mail.qq.com; ;");

		assertEquals("p_skey", cookie.getName());
		assertEquals("UIJeeZgODkPQgiVcwHJBhq9mYrZC9JdpYF6SCZ3fNfY_", cookie.getValue());
	}

	@Test
	public void testSpecialCookieValues() {
		Cookie cookie = new Cookie("name=value");

		assertEquals("name", cookie.getName());
		assertEquals("value", cookie.getValue());

		cookie = new Cookie("name=value;");

		assertEquals("name", cookie.getName());
		assertEquals("value", cookie.getValue());

		// duplicated value

		cookie = new Cookie("name=value;a=b;");

		assertEquals("name", cookie.getName());
		assertEquals("value", cookie.getValue());

		// empty value

		cookie = new Cookie("name=");

		assertEquals("name", cookie.getName());
		assertEquals("", cookie.getValue());

		// empty name

		cookie = new Cookie("=value");

		assertEquals(null, cookie.getName());
		assertEquals(null, cookie.getValue());
	}
}
