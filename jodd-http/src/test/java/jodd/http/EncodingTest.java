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

import jodd.http.up.ByteArrayUploadable;
import jodd.util.MimeTypes;
import jodd.util.StringPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EncodingTest {

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
	public void testContentTypeHeader() {
		HttpRequest req = HttpRequest.get("localhost/hello");

		assertNull(req.contentType());

		req.contentType("text/plain;charset=UTF-8");

		assertEquals("text/plain", req.mediaType());
		assertEquals("UTF-8", req.charset());
		assertEquals("text/plain;charset=UTF-8", req.contentType());

		req.mediaType("text/html");
		assertEquals("text/html;charset=UTF-8", req.contentType());
		req.mediaType(null);
		assertEquals("text/html;charset=UTF-8", req.contentType());
		req.charset("ASCII");
		assertEquals("text/html;charset=ASCII", req.contentType());
		req.charset(null);
		assertEquals("text/html", req.contentType());

		req.contentType("text/plain;charset=UTF-8;boundary=123");
		assertEquals("text/plain", req.mediaType());
		assertEquals("UTF-8", req.charset());
		assertEquals("text/plain;charset=UTF-8;boundary=123", req.contentType());
	}

	@Test
	public void testRequestEncoding1() throws IOException {
		testRequestEncoding(1);
	}
	@Test
	public void testRequestEncoding2() throws IOException {
		testRequestEncoding(2);
	}
	@Test
	public void testRequestEncoding3() throws IOException {
		testRequestEncoding(3);
	}
	@Test
	public void testRequestEncoding4() throws IOException {
		testRequestEncoding(4);
	}
	private void testRequestEncoding(int i) throws IOException {
		HttpRequest request =
				(i == 1 || i == 2) ?
				HttpRequest.get("http://localhost:8173/echo?id=12"):
				HttpRequest.post("http://localhost:8173/echo?id=12");

		String utf8String = (i == 1 || i == 3) ? "Hello!" : "хелло!";
		byte[] utf8Bytes = utf8String.getBytes(StringPool.UTF_8);
		int utf8StringRealLen = utf8Bytes.length;

		request.bodyText(utf8String);

		String rawBody = request.body();
		assertEquals(utf8StringRealLen, rawBody.length());
		assertArrayEquals(utf8Bytes, request.bodyBytes());

		HttpResponse response = request.send();
		assertEquals(200, response.statusCode());

		// servlet

		if (i < 3) {
			assertTrue(EchoServlet.ref.get);
			assertFalse(EchoServlet.ref.post);
		} else {
			assertFalse(EchoServlet.ref.get);
			assertTrue(EchoServlet.ref.post);
		}

		assertEquals(String.valueOf(utf8StringRealLen), EchoServlet.ref.header.get("content-length"));
		assertEquals("text/html;charset=UTF-8", EchoServlet.ref.header.get("content-type"));
		assertEquals(utf8String, EchoServlet.ref.body);

		// response

		assertEquals(String.valueOf(utf8StringRealLen), response.contentLength());
		assertEquals("text/html;charset=UTF-8", response.contentType());
		assertEquals(utf8String, response.bodyText());
		assertEquals(new String(utf8Bytes, StringPool.ISO_8859_1), response.body());
	}

	@Test
	public void testFormParams1() {
		testFormParams(1);
	}
	@Test
	public void testFormParams2() {
		testFormParams(2);
	}
	@Test
	public void testFormParams3() {
		testFormParams(3);
	}
	private void testFormParams(int i) {
		String encoding = i == 1 ?  "UTF-8" : "CP1251";

		HttpRequest request = HttpRequest.post("http://localhost:8173/echo2");
		request.formEncoding(encoding);

		if (i == 3) {
			request.charset("UTF-8");
		}

		String value1 = "value";
		String value2 = "валуе";

		request.form("one", value1);
		request.form("two", value2);
		if (i != 3) {
			request.form("enc", encoding);
		}

		HttpResponse httpResponse = request.send();

		assertEquals("application/x-www-form-urlencoded", request.mediaType());
		if (i == 3) {
			assertEquals("UTF-8", request.charset());
			assertEquals("CP1251", request.formEncoding);
		} else {
			assertNull(request.charset());
		}

		assertFalse(EchoServlet.ref.get);
		assertTrue(EchoServlet.ref.post);

		assertEquals(i == 3 ? 2 : 3, EchoServlet.ref.params.size());
		assertEquals(value1, EchoServlet.ref.params.get("one"));
		assertEquals(value2, EchoServlet.ref.params.get("two"));
	}

	@Test
	public void testQueryParams1() throws IOException {
		testQueryParams(1);
	}
	@Test
	public void testQueryParams2() throws IOException {
		testQueryParams(2);
	}
	private void testQueryParams(int i) throws IOException {
		String encoding = i == 1 ?  "UTF-8" : "CP1251";

		HttpRequest request = HttpRequest.get("http://localhost:8173/echo2");
		request.queryEncoding(encoding);

		String value1 = "value";
		String value2 = "валуе";

		request.query("one", value1);
		request.query("two", value2);
		request.query("enc", encoding);

		HttpResponse httpResponse = request.send();

		assertTrue(EchoServlet.ref.get);
		assertFalse(EchoServlet.ref.post);

		assertEquals(3, EchoServlet.ref.params.size());
		assertEquals(value1, EchoServlet.ref.params.get("one"));
		assertEquals(value2, EchoServlet.ref.params.get("two"));
	}

	@Test
	public void testMultipart() {
		HttpRequest request = HttpRequest.post("http://localhost:8173/echo2");
		request
			.formEncoding("UTF-8")		// optional
			.multipart(true);

		String value1 = "value";
		String value2 = "валуе";

		request.form("one", value1);
		request.form("two", value2);

		HttpResponse httpResponse = request.send();

		assertEquals("multipart/form-data", request.mediaType());

		assertFalse(EchoServlet.ref.get);
		assertTrue(EchoServlet.ref.post);

		assertEquals(value1, EchoServlet.ref.parts.get("one"));
		assertEquals(value2, EchoServlet.ref.parts.get("two"));
	}

	@Test
	public void testUploadWithUploadable() throws IOException {
		HttpResponse response = HttpRequest
				.post("http://localhost:8173/echo2")
				.multipart(true)
				.form("id", "12")
				.form("file", new ByteArrayUploadable(
					"upload тест".getBytes(StringPool.UTF_8), "d ст", MimeTypes.MIME_TEXT_PLAIN))
				.send();

		assertEquals(200, response.statusCode());
		assertEquals("OK", response.statusPhrase());

		assertEquals("12", Echo2Servlet.ref.params.get("id"));
		assertEquals("upload тест", Echo2Servlet.ref.parts.get("file"));
		assertEquals("d ст", Echo2Servlet.ref.fileNames.get("file"));
	}


}
