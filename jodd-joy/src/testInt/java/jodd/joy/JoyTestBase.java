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

package jodd.joy;

import jodd.http.Cookie;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.json.JsonObject;
import jodd.json.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class JoyTestBase {

	protected int port;
	private String localhost() {return "localhost:" + port;}

	@Test
	void testHello() {
		HttpResponse httpResponse =
			HttpRequest
				.post(localhost() + "/hello")
				.form("username", "jodd")
				.send();

		assertEquals(200, httpResponse.statusCode());
		assertEquals("{\"username\":\"jodd\"}", httpResponse.bodyText());
	}

	// ---------------------------------------------------------------- auth

	@Test
	void testLogin_params_wrongUserPass() {
		HttpResponse httpResponse =
			HttpRequest
				.post(localhost() + "/j_login")
				.form("j_username", "jodd")
				.form("j_password", "wrong")
				.send();

		assertEquals(401, httpResponse.statusCode());

		httpResponse =
			HttpRequest
				.get(localhost() + "/hello/secret")
				.send();

		assertEquals(404, httpResponse.statusCode());
	}

	@Test
	void testLogin_params_okUserPass_token() {
		HttpResponse httpResponse =
			HttpRequest
				.post("localhost:" + port + "/j_login")
				.form("j_username", "jodd")
				.form("j_password", "jodd!")
				.send();

		final JsonObject payload = JsonParser.create().parseAsJsonObject(httpResponse.bodyText());
		final String token = payload.getString("token");

		assertNotNull(token);

		assertEquals(200, httpResponse.statusCode());

		httpResponse =
			HttpRequest
				.get(localhost() + "/hello/secret")
				.tokenAuthentication(token)
				.send();

		assertEquals(200, httpResponse.statusCode());

		final String newToken = httpResponse.tokenAuthentication();

		assertNotEquals(token, newToken);
	}

	@Test
	void testLogin_basic_okUserPass_cookies() {
		HttpResponse httpResponse =
			HttpRequest
				.post("localhost:" + port + "/j_login")
				.basicAuthentication("jodd", "jodd!")
				.send();

		final Cookie[] cookies = httpResponse.cookies();

		assertNotNull(cookies);

		assertEquals(200, httpResponse.statusCode());

		httpResponse =
			HttpRequest
				.get(localhost() + "/hello/secret")
				.cookies(cookies)
				.send();

		assertEquals(200, httpResponse.statusCode());
	}


}
