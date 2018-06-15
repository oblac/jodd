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

import jodd.http.fixture.Data;
import jodd.http.up.ByteArrayUploadable;
import jodd.util.StringPool;
import jodd.net.MimeTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncodingTest {

	static TestServer testServer;

	@BeforeAll
	static void startServer() throws Exception {
		testServer = new TomcatServer();
		testServer.start();
	}

	@AfterAll
	static void stopServer() throws Exception {
		testServer.stop();
	}

	@Test
	void testContentTypeHeader() {
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
	void testRequestEncoding1() throws IOException {
		testRequestEncoding(1);
	}
	@Test
	void testRequestEncoding2() throws IOException {
		testRequestEncoding(2);
	}
	@Test
	void testRequestEncoding3() throws IOException {
		testRequestEncoding(3);
	}
	@Test
	void testRequestEncoding4() throws IOException {
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
			assertTrue(Data.ref.get);
			assertFalse(Data.ref.post);
		} else {
			assertFalse(Data.ref.get);
			assertTrue(Data.ref.post);
		}

		assertEquals(String.valueOf(utf8StringRealLen), Data.ref.header.get("content-length"));
		assertEquals("text/html;charset=UTF-8", Data.ref.header.get("content-type"));
		assertEquals(utf8String, Data.ref.body);

		// response

		assertEquals(String.valueOf(utf8StringRealLen), response.contentLength());
		assertEquals("text/html;charset=UTF-8", response.contentType());
		assertEquals(utf8String, response.bodyText());
		assertEquals(new String(utf8Bytes, StringPool.ISO_8859_1), response.body());
	}

	@Test
	void testFormParams1() {
		testFormParams(1);
	}
	@Test
	void testFormParams2() {
		testFormParams(2);
	}
	@Test
	void testFormParams3() {
		testFormParams(3);
	}
	private void testFormParams(int i) {
		String encoding = i == 1 ?  "UTF-8" : "CP1251";

		HttpRequest request = HttpRequest.post("http://localhost:8173/echo3");
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

		assertFalse(Data.ref.get);
		assertTrue(Data.ref.post);

		assertEquals(i == 3 ? 2 : 3, Data.ref.params.size());
		assertEquals(value1, Data.ref.params.get("one"));
		assertEquals(value2, Data.ref.params.get("two"));
	}

	@Test
	void testQueryParams1() throws IOException {
		testQueryParams(1);
	}

	@Test
	@Disabled("Ignored until we figure out how to enable org.apache.catalina.STRICT_SERVLET_COMPLIANCE")
	void testQueryParams2() throws IOException {
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

		assertTrue(Data.ref.get);
		assertFalse(Data.ref.post);

		assertEquals(3, Data.ref.params.size());
		assertEquals(value1, Data.ref.params.get("one"));
		assertEquals(value2, Data.ref.params.get("two"));
	}

	@Test
	void testMultipart() {
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

		assertFalse(Data.ref.get);
		assertTrue(Data.ref.post);

		assertEquals(value1, Data.ref.parts.get("one"));
		assertEquals(value2, Data.ref.parts.get("two"));
	}

	@Test
	void testUploadWithUploadable() throws IOException {
		HttpResponse response = HttpRequest
				.post("http://localhost:8173/echo2")
				.multipart(true)
				.form("id", "12")
				.form("file", new ByteArrayUploadable(
					"upload тест".getBytes(StringPool.UTF_8), "d ст", MimeTypes.MIME_TEXT_PLAIN))
				.send();

		assertEquals(200, response.statusCode());
		assertEquals("OK", response.statusPhrase());

		assertEquals("12", Data.ref.params.get("id"));
		assertEquals("upload тест", Data.ref.parts.get("file"));
		assertEquals("d ст", Data.ref.fileNames.get("file"));
	}


}
