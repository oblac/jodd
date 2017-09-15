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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpBrowserTest {
	
	static TestServer testServer;

	@BeforeAll
	public static void startServer() throws Exception {
		testServer = new TomcatServer();
		testServer.start();
	}

	@AfterAll
	public static void stopServer() throws Exception {
		testServer.stop();
	}
	
	@BeforeEach
	public void setUp() {
		EchoServlet.testinit();
	}

	@Test
	public void testBrowser() {
		HttpBrowser httpBrowser = new HttpBrowser();

		httpBrowser.sendRequest(
			HttpRequest
				.get("localhost:8173/echo?id=17")
				.cookies(new Cookie("waffle", "jam"))
				.bodyText("hello"));

		HttpResponse httpResponse = httpBrowser.getHttpResponse();

		assertNotNull(httpResponse);
		assertEquals("hello", httpResponse.body());

		Cookie[] cookies = httpResponse.cookies();
		assertEquals(1, cookies.length);

		assertEquals("waffle", cookies[0].getName());
		assertEquals("jam!", cookies[0].getValue());
	}

	@Test
	public void testBrowserRedirect() {
		HttpBrowser httpBrowser = new HttpBrowser();

		httpBrowser.sendRequest(HttpRequest.get("localhost:8173/redirect"));

		HttpResponse httpResponse = httpBrowser.getHttpResponse();

		assertEquals(200, httpResponse.statusCode());
		assertEquals("target!", httpResponse.body());

	}
	
}
