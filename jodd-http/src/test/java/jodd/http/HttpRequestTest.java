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

import jodd.io.FileUtil;
import jodd.upload.FileUpload;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {

	@Test
	public void testQueryParameters() {
		HttpRequest httpRequest = new HttpRequest();

		httpRequest.path("");
		assertEquals("/", httpRequest.path());

		httpRequest.path("jodd");
		assertEquals("/jodd", httpRequest.path());
		assertNotNull(httpRequest.query());
		assertEquals(0, httpRequest.query().size());

		httpRequest.queryString("one=two");
		assertEquals("/jodd", httpRequest.path());

		HttpMultiMap<String> params = httpRequest.query();
		assertEquals(1, params.size());
		assertEquals("two", params.get("one"));

		httpRequest.queryString("one");
		assertEquals("one", httpRequest.queryString());
		params = httpRequest.query();
		assertEquals(1, params.size());
		assertNull(params.get("one"));

		httpRequest.queryString("one=");
		assertEquals("one=", httpRequest.queryString());
		params = httpRequest.query();
		assertEquals(1, params.size());
		assertEquals("", params.get("one"));

		httpRequest.queryString("one=aaa&two=bbb");
		assertEquals("one=aaa&two=bbb", httpRequest.queryString());
		params = httpRequest.query();
		assertEquals(2, params.size());
		assertEquals("aaa", params.get("one"));
		assertEquals("bbb", params.get("two"));

		httpRequest.queryString("one=&two=aaa");
		assertEquals("one=&two=aaa", httpRequest.queryString());
		params = httpRequest.query();
		assertEquals(2, params.size());
		assertEquals("", params.get("one"));
		assertEquals("aaa", params.get("two"));

		httpRequest.clearQueries();
		httpRequest.queryString("one=Супер");
		assertEquals("one=%D0%A1%D1%83%D0%BF%D0%B5%D1%80", httpRequest.queryString());
		params = httpRequest.query();
		assertEquals(1, params.size());
		assertEquals("Супер", params.get("one"));

		httpRequest.queryString("one=Sуp");
		assertEquals("one=S%D1%83p", httpRequest.queryString());

		httpRequest.queryString("one=1&one=2");
		assertEquals("one=1&one=2", httpRequest.queryString());
		params = httpRequest.query();
		assertEquals(1, params.size());
		assertEquals("1", params.getAll("one").get(0));
		assertEquals("2", params.getAll("one").get(1));

		httpRequest.query("one", Integer.valueOf(3));
		assertEquals("one=1&one=2&one=3", httpRequest.queryString());
	}

	@Test
	public void testFormParamsObjects() {
		Map<String, Object> params = new HashMap<>();
		params.put("state", 1);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.form(params);

		assertEquals(1, httpRequest.form().size());
	}

	@Test
	public void testSet() {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.set("GET http://jodd.org:173/index.html?light=true");

		assertEquals("GET", httpRequest.method());
		assertEquals("http", httpRequest.protocol());
		assertEquals("jodd.org", httpRequest.host());
		assertEquals(173, httpRequest.port());
		assertEquals("/index.html", httpRequest.path());
		assertEquals("true", httpRequest.query().get("light"));


		httpRequest = new HttpRequest();
		httpRequest.set("http://jodd.org:173/index.html?light=true");

		assertEquals("GET", httpRequest.method());
		assertEquals("http", httpRequest.protocol());
		assertEquals("jodd.org", httpRequest.host());
		assertEquals(173, httpRequest.port());
		assertEquals("/index.html", httpRequest.path());
		assertEquals("true", httpRequest.query().get("light"));


		httpRequest = new HttpRequest();
		httpRequest.set("jodd.org:173/index.html?light=true");

		assertEquals("GET", httpRequest.method());
		assertEquals("http", httpRequest.protocol());
		assertEquals("jodd.org", httpRequest.host());
		assertEquals(173, httpRequest.port());
		assertEquals("/index.html", httpRequest.path());
		assertEquals("true", httpRequest.query().get("light"));


		httpRequest = new HttpRequest();
		httpRequest.set("jodd.org/index.html?light=true");

		assertEquals("GET", httpRequest.method());
		assertEquals("http", httpRequest.protocol());
		assertEquals("jodd.org", httpRequest.host());
		assertEquals(80, httpRequest.port());
		assertEquals("/index.html", httpRequest.path());
		assertEquals("true", httpRequest.query().get("light"));


		httpRequest = new HttpRequest();
		httpRequest.set("/index.html?light=true");

		assertEquals("GET", httpRequest.method());
		assertEquals("http", httpRequest.protocol());
		assertEquals("localhost", httpRequest.host());
		assertEquals(80, httpRequest.port());
		assertEquals("/index.html", httpRequest.path());
		assertEquals("true", httpRequest.query().get("light"));


		httpRequest = new HttpRequest();
		httpRequest.set("http://jodd.org");

		assertEquals("GET", httpRequest.method());
		assertEquals("http", httpRequest.protocol());
		assertEquals("jodd.org", httpRequest.host());
		assertEquals(80, httpRequest.port());
		assertEquals("/", httpRequest.path());
	}


	@Test
	public void testInOutForm() {
		HttpRequest request = HttpRequest.get("http://jodd.org/?id=173");
		request.header("User-Agent", "Scaly");
		request.form("one", "funny");

		byte[] bytes = request.toByteArray();

		// read
		HttpRequest request2 = HttpRequest.readFrom(new ByteArrayInputStream(bytes));

		assertEquals(request.method(), request2.method());
		assertEquals(request.path(), request2.path());
		assertEquals(request.queryString(), request2.queryString());

		assertEquals(request.header("User-Agent"), request2.header("User-Agent"));
		assertEquals(request.header("Content-Type"), request2.header("content-type"));
		assertEquals(request.header("Content-Length"), request2.header("content-length"));

		HttpMultiMap<?> params1 = request.form();
		HttpMultiMap<?> params2 = request2.form();
		assertEquals(params1.size(), params2.size());
		assertEquals(params2.get("one"), params2.get("one"));
	}

	@Test
	public void testNegativeContentLength() {
		HttpRequest request = HttpRequest.get("http://jodd.org/?id=173");
		request.contentLength(-123);

		byte[] bytes = request.toByteArray();
		try {
			HttpRequest request2 = HttpRequest.readFrom(new ByteArrayInputStream(bytes));
			assertEquals("", request2.body());
		} catch (Exception ex) {
			fail(ex.toString());
		}

		// the same test but with missing content length

		request = HttpRequest.get("http://jodd.org/?id=173");

		bytes = request.toByteArray();
		try {
			HttpRequest request2 = HttpRequest.readFrom(new ByteArrayInputStream(bytes));
			assertEquals("", request2.body());
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testFileUpload() throws IOException {
		HttpRequest request = HttpRequest.get("http://jodd.org/?id=173");

		request.header("User-Agent", "Scaly").form("one", "funny");

		File tempFile = FileUtil.createTempFile();
		tempFile.deleteOnExit();
		FileUtil.writeString(tempFile, "qwerty");
		request.form("two", tempFile);

		byte[] bytes = request.toByteArray();


		// read
		HttpRequest request2 = HttpRequest.readFrom(new ByteArrayInputStream(bytes));
		HttpMultiMap<?> httpParams2 = request2.form();

		assertEquals(request.method(), request2.method());
		assertEquals(request.path(), request2.path());
		assertEquals(request.queryString(), request2.queryString());

		assertEquals(request.header("User-Agent"), request2.header("User-Agent"));
		assertEquals(request.header("Content-Type"), request2.header("content-type"));
		assertEquals(request.header("Content-Length"), request2.header("content-length"));

		HttpMultiMap<?> params1 = request.form();
		HttpMultiMap<?> params2 = request2.form();
		assertEquals(params1.size(), params2.size());
		assertEquals(params2.get("one"), params2.get("one"));

		FileUpload fu = (FileUpload) httpParams2.get("two");
		assertEquals(6, fu.getSize());

		String str = new String(fu.getFileContent());
		assertEquals("qwerty", str);

		tempFile.delete();
	}

	@Test
	public void testUrl() {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.set("GET http://jodd.org:173/index.html?light=true");

		assertEquals("http://jodd.org:173/index.html?light=true", httpRequest.url());
		assertEquals("http://jodd.org:173", httpRequest.hostUrl());

		httpRequest = HttpRequest.get("foo.com/");

		assertEquals("http://foo.com", httpRequest.hostUrl());
	}

	@Test
	public void testBasicAuthorizationCanBeSetToNullAndIsIgnoredSilently() {
		HttpRequest httpRequest = new HttpRequest();
		String[][] input = new String[][]{
				{"non-null", null},
				{null, "non-null"},
				{null, null},
		};

		try {

			for(String[] pair :input) {
				httpRequest.basicAuthentication(pair[0], pair[1]);
				assertNull(httpRequest.headers.get("Authorization"));
			}

		} catch (RuntimeException e) {
			fail("No exception should be thrown for null authorization basic header args!");
		}
	}

	@Test
	public void test394() {
		HttpRequest request = HttpRequest.get("https://jodd.org/random link");
		assertEquals("GET", request.method());
		assertEquals("https://jodd.org/random link", request.url());

		request = HttpRequest.get("https://jodd.org/random link?q=1");
		assertEquals("1", request.query().get("q"));

		String badUrl = "httpsjodd.org/random link?q=1:// GET";
		try {				
			HttpRequest.get(badUrl).send();
			fail("error");
		}
		catch (HttpException he) {
			assertTrue(he.getMessage().contains(badUrl));
		}

	}
	
	@Test
	public void testCapitalizeHeaders() {

		// true

		HttpRequest request = HttpRequest.get("")
			.capitalizeHeaderKeys(true)
			.header("key-tEST2", "value2");
		assertTrue(request.toString(false).contains("Key-Test2: value2"), "Header key should have been modified");
		assertEquals("value2", request.headers("KEY-TEST2").get(0));
		assertEquals("value2", request.headers("key-test2").get(0));

		request.header("key-test2", "value3");
		assertTrue(request.toString(false).contains("Key-Test2: value2, value3"), "Header key should have been modified");
		assertEquals(2, request.headers("KEY-TEST2").size());
		assertEquals(2 + 2, request.headerNames().size());		// 2 default and 2 added

		request.removeHeader("key-test2");
		assertFalse(request.headers.contains("key-test2"));
		assertFalse(request.headers.contains("key-tEST2"));


		// false

		request = HttpRequest.get("")
			.capitalizeHeaderKeys(false)
			.header("KEY-TEST1", "VALUE1");

		assertTrue(request.toString(false).contains("KEY-TEST1: VALUE1"), "Header key should not have been modified");
		assertEquals("VALUE1", request.headers("KEY-TEST1").get(0));
		assertEquals("VALUE1", request.headers("key-test1").get(0));

		request.header("key-test1", "value4");
		assertTrue(request.toString(false).contains("key-test1: VALUE1, value4"), "Header key should not have been modified");
		assertEquals(2, request.headers("KEY-TEST1").size());
		assertEquals(2 + 2, request.headerNames().size());		// 2 default and 2 added

		request.removeHeader("key-test1");
		assertFalse(request.headers.contains("key-test1"));
		assertFalse(request.headers.contains("KEY-TEST1"));
	}
}
