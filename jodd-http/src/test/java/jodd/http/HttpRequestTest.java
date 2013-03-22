// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import jodd.upload.FileUpload;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

		Map<String, Object> params = httpRequest.query();
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
		assertEquals("1", ((String[]) params.get("one"))[0]);
		assertEquals("2", ((String[]) params.get("one"))[1]);

		httpRequest.query("one", 3);
		assertEquals("one=1&one=2&one=3", httpRequest.queryString());

		params.put("two", "xxx");
		assertEquals("one=1&one=2&one=3&two=xxx", httpRequest.queryString());
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

		Map params1 = request.form();
		Map params2 = request2.form();
		assertEquals(params1.size(), params2.size());
		assertEquals(params2.get("one"), params2.get("one"));
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
		Map<String, Object> httpParams2 = request2.form();

		assertEquals(request.method(), request2.method());
		assertEquals(request.path(), request2.path());
		assertEquals(request.queryString(), request2.queryString());

		assertEquals(request.header("User-Agent"), request2.header("User-Agent"));
		assertEquals(request.header("Content-Type"), request2.header("content-type"));
		assertEquals(request.header("Content-Length"), request2.header("content-length"));

		Map params1 = request.form();
		Map params2 = request2.form();
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
	}

}