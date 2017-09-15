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

import io.netty.handler.codec.http.HttpHeaders;
import jodd.http.net.SocketHttpConnectionProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

public class ProxyTest {

	private ClientAndProxy proxy;
	private ClientAndServer mockServer;

	@BeforeEach
	public void startProxy() {
		mockServer = startClientAndServer(1080);
		proxy = startClientAndProxy(1090);
		setupMockServer();
	}

	@AfterEach
	public void stopProxy() {
		proxy.stop();
		mockServer.stop();
	}

	@Test
	public void testDirect() {
		HttpResponse response = HttpRequest.get("http://localhost:1080/get_books").send();
		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("Tatum"));
		proxy.verify(request().withPath("/get_books"), exactly(0));
	}

	@Test
	public void testDirectHttps() {
		HttpResponse response = HttpRequest.get("https://localhost:1080/get_books").trustAllCerts(true).send();
		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("Tatum"));
		proxy.verify(request().withPath("/get_books"), exactly(0));
	}

	@Test
	@Disabled
	public void testHttpProxy() {
		SocketHttpConnectionProvider s = new SocketHttpConnectionProvider();
		s.useProxy(ProxyInfo.httpProxy("localhost", 1090, null, null));

		HttpResponse response = HttpRequest.get("http://localhost:1080/get_books")
			.withConnectionProvider(s)
			.send();
		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("Tatum"));
	}

	@Test
	public void testSocks5Proxy() {
		SocketHttpConnectionProvider s = new SocketHttpConnectionProvider();
		s.useProxy(ProxyInfo.socks5Proxy("localhost", 1090, null, null));

		HttpResponse response = HttpRequest.get("http://localhost:1080/get_books")
			.withConnectionProvider(s)
			.send();
		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("Tatum"));
		proxy.verify(request().withPath("/get_books"), exactly(1));
	}

	@Test
	public void testSocks5ProxyWithHttps() {
		SocketHttpConnectionProvider s = new SocketHttpConnectionProvider();
		s.useProxy(ProxyInfo.socks5Proxy("localhost", 1090, null, null));

		HttpResponse response = HttpRequest.get("https://localhost:1080/get_books")
			.withConnectionProvider(s)
			.trustAllCerts(true)
			.send();
		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("Tatum"));
		proxy.verify(request().withPath("/get_books"), exactly(1));
	}

	private void setupMockServer() {
		mockServer
			.when(
				request()
					.withPath("/get_books")
			)
			.respond(
				response()
					.withHeaders(
						new Header(HttpHeaders.Names.CONTENT_TYPE,"application/json")
					)
					.withBody("" +
						"[\n" +
						"    {\n" +
						"        \"id\": \"1\",\n" +
						"        \"title\": \"Xenophon's imperial fiction : on the education of Cyrus\",\n" +
						"        \"author\": \"James Tatum\",\n" +
						"        \"isbn\": \"0691067570\",\n" +
						"        \"publicationDate\": \"1989\"\n" +
						"    },\n" +
						"    {\n" +
						"        \"id\": \"2\",\n" +
						"        \"title\": \"You are here : personal geographies and other maps of the imagination\",\n" +
						"        \"author\": \"Katharine A. Harmon\",\n" +
						"        \"isbn\": \"1568984308\",\n" +
						"        \"publicationDate\": \"2004\"\n" +
						"    },\n" +
						"    {\n" +
						"        \"id\": \"3\",\n" +
						"        \"title\": \"You just don't understand : women and men in conversation\",\n" +
						"        \"author\": \"Deborah Tannen\",\n" +
						"        \"isbn\": \"0345372050\",\n" +
						"        \"publicationDate\": \"1990\"\n" +
						"    }" +
						"]")
			);
	}
}
