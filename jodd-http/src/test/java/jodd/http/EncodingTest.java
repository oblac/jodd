// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.util.StringPool;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

public class EncodingTest {

	static WinstoneServer winstoneServer;

	@BeforeClass
	public static void startServer() throws IOException {
		winstoneServer = new WinstoneServer();
		winstoneServer.start();
	}

	@AfterClass
	public static void stopServer() {
		winstoneServer.stop();
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
	public void testRequestEncoding() throws IOException {
		for (int i = 1; i <= 4; i++) {
			System.out.println(i);

			HttpRequest request =
					(i == 1 || i == 2) ?
					HttpRequest.get("http://localhost:8080/echo?id=12"):
					HttpRequest.post("http://localhost:8080/echo?id=12");

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

			assertEquals(String.valueOf(utf8StringRealLen), EchoServlet.ref.header.get("Content-Length"));
			assertEquals("text/html;charset=UTF-8", EchoServlet.ref.header.get("Content-Type"));
			assertEquals(utf8String, EchoServlet.ref.body);

			// response

			assertEquals(String.valueOf(utf8StringRealLen), response.contentLength());
			assertEquals("text/html;charset=UTF-8", response.contentType());
			assertEquals(utf8String, response.bodyText());
			assertEquals(new String(utf8Bytes, StringPool.ISO_8859_1), response.body());
		}
	}

	@Test
	public void testFormParams() throws IOException {
		for (int i = 1; i <= 3; i++) {
			System.out.println(i);

			String encoding = i == 1 ?  "UTF-8" : "CP1251";

			HttpRequest request = HttpRequest.post("http://localhost:8080/echo2");
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
	}

	@Test
	public void testQueryParams() throws IOException {
		for (int i = 1; i <= 2; i++) {
			String encoding = i == 1 ?  "UTF-8" : "CP1251";

			HttpRequest request = HttpRequest.get("http://localhost:8080/echo2");
			request.queryEncoding(encoding);

			String value1 = "value";
			String value2 = "валуе";

			request.query("one", value1);
			request.query("two", value2);
			request.query("enc", encoding);

			HttpResponse httpResponse = request.send();

			System.out.println(request);

			assertTrue(EchoServlet.ref.get);
			assertFalse(EchoServlet.ref.post);

			assertEquals(3, EchoServlet.ref.params.size());
			assertEquals(value1, EchoServlet.ref.params.get("one"));
			assertEquals(value2, EchoServlet.ref.params.get("two"));
		}
	}

}