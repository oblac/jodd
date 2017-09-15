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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeepAliveTest {

	private static final String[] RESPONSES = new String[] {
			"HTTP/1.1 200 OK\r\n" +
			"Content-Type: text/html; charset=utf-8\r\n" +
			"Content-Length: 13\r\n" +
			"Connection: Keep-Alive\r\n" +
			"Keep-Alive: timeout=100, max=2\r\n" +
			"\r\n" +
			"<html></html>",

			"HTTP/1.1 200 OK\r\n" +
			"Content-Type: text/html; charset=utf-8\r\n" +
			"Content-Length: 13\r\n" +
			"Connection: Keep-Alive\r\n" +
			"Keep-Alive: timeout=100, max=1\r\n" +
			"\r\n" +
			"<html></html>",

			"HTTP/1.1 200 OK\r\n" +
			"Content-Type: text/html; charset=utf-8\r\n" +
			"Content-Length: 13\r\n" +
			"Connection: Close\r\n" +
			"\r\n" +
			"<html></html>"
	};

	private static int currentResponse;

	HttpConnectionProvider httpConnectionProvider = new HttpConnectionProvider() {
		public void useProxy(ProxyInfo proxyInfo) {
		}

		public HttpConnection createHttpConnection(HttpRequest httpRequest) throws IOException {
			return new HttpConnection() {
				@Override
				public void init() throws IOException {
					// ignore
				}

				public OutputStream getOutputStream() throws IOException {
					return new ByteArrayOutputStream();
				}

				public InputStream getInputStream() throws IOException {
					return new ByteArrayInputStream(RESPONSES[currentResponse].getBytes());
				}

				public void setTimeout(int milliseconds) {
					// ignore
				}

				public void close() {
				}
			};
		}
	};

	@Test
	public void testKeepAlive() {
		currentResponse = 0;

		// ->
		HttpRequest request = HttpRequest.get("http://jodd.org");
		assertEquals("Close", request.header("Connection"));
		request.connectionKeepAlive(true);
		assertTrue(request.isConnectionPersistent());

		// <-
		HttpResponse response = request.open(httpConnectionProvider).send();
		HttpConnection connection = request.connection();

		assertTrue(request.isConnectionPersistent());
		assertTrue(response.isConnectionPersistent());
		assertNotNull(request.connection());

		currentResponse = 1;

		// ->
		request = HttpRequest.get("http://jodd.org");
		response = request.keepAlive(response, true).send();

		// <-
		assertSame(connection, request.connection());
		assertTrue(request.isConnectionPersistent());
		assertTrue(response.isConnectionPersistent());
		assertNotNull(request.connection());

		currentResponse = 2;

		// -> LAST request
		request = HttpRequest.get("http://jodd.org");
		response = request.keepAlive(response, true).send();

		// <-
		assertNull(request.connection()); // connection is closed
		assertTrue(request.isConnectionPersistent());
		assertFalse(response.isConnectionPersistent());

		currentResponse = 0;

		// -> AFTER THE LAST, STARTS EVERYTHING AGAIN

		request = HttpRequest.get("http://jodd.org");
		response = request.keepAlive(response, true).send();		// should be false for the last connection, but ok.

		// <-
		assertTrue(request.isConnectionPersistent());
		assertTrue(response.isConnectionPersistent());
		assertNotNull(request.connection());

		// CLOSE

		response.close();
		assertNull(request.connection());	// connection closed
	}

	@Test
	public void testKeepAliveBrowser() {
		HttpBrowser browser = new HttpBrowser();
		browser.setKeepAlive(true);
		browser.setHttpConnectionProvider(httpConnectionProvider);

		currentResponse = 0;

		// ->
		HttpRequest request = HttpRequest.get("http://jodd.org");
		browser.sendRequest(request);

		// <-
		HttpResponse response = browser.getHttpResponse();
		HttpConnection connection = request.connection();

		assertTrue(request.isConnectionPersistent());
		assertTrue(response.isConnectionPersistent());
		assertNotNull(request.connection());

		currentResponse = 1;

		// ->
		request = HttpRequest.get("http://jodd.org");
		response = browser.sendRequest(request);

		// <-
		assertSame(connection, request.connection());
		assertTrue(request.isConnectionPersistent());
		assertTrue(response.isConnectionPersistent());
		assertNotNull(request.connection());

		currentResponse = 2;

		// -> LAST request
		request = HttpRequest.get("http://jodd.org");
		response = browser.sendRequest(request);

		// <-
		assertNull(request.connection()); // connection is closed
		assertTrue(request.isConnectionPersistent());
		assertFalse(response.isConnectionPersistent());

		currentResponse = 0;

		// -> AFTER THE LAST, STARTS EVERYTHING AGAIN

		request = HttpRequest.get("http://jodd.org");
		response = browser.sendRequest(request);

		// <-
		assertTrue(request.isConnectionPersistent());
		assertTrue(response.isConnectionPersistent());
		assertNotNull(request.connection());

		// CLOSE

		browser.close();
		assertNull(request.connection());	// connection closed
	}
}
