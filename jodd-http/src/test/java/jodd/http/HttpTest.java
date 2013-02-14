// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import jodd.upload.FileUpload;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HttpTest {

	@Test
	public void testQueryParameters() {
		HttpTransfer ht = new HttpTransfer();

		ht.setPath("");
		assertEquals("/", ht.getPath());

		ht.setPath("jodd");
		assertEquals("/jodd", ht.getPath());
		assertNotNull(ht.getQueryParameters());
		assertEquals(0, ht.getQueryParameters().getParamsCount());

		ht.setQueryParameters(new HttpParams("one=two"));
		assertEquals("/jodd?one=two", ht.getPath());
		HttpParams params = ht.getQueryParameters();
		assertEquals(1, params.getParamsCount());
		assertEquals("two", params.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one"));
		assertEquals("/jodd?one", ht.getPath());
		params = ht.getQueryParameters();
		assertEquals(1, params.getParamsCount());
		assertNull(params.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one="));
		assertEquals("/jodd?one=", ht.getPath());
		params = ht.getQueryParameters();
		assertEquals(1, params.getParamsCount());
		assertEquals("", params.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one=aaa&two=bbb"));
		assertEquals("/jodd?one=aaa&two=bbb", ht.getPath());
		params = ht.getQueryParameters();
		assertEquals(2, params.getParamsCount());
		assertEquals("aaa", params.getParameter("one"));
		assertEquals("bbb", params.getParameter("two"));

		ht.setQueryParameters(new HttpParams("one=&two=aaa"));
		assertEquals("/jodd?one=&two=aaa", ht.getPath());
		params = ht.getQueryParameters();
		assertEquals(2, params.getParamsCount());
		assertEquals("", params.getParameter("one"));
		assertEquals("aaa", params.getParameter("two"));

		ht.setQueryParameters(new HttpParams("one=Супер"));
		assertEquals("/jodd?one=%D0%A1%D1%83%D0%BF%D0%B5%D1%80", ht.getPath());
		params = ht.getQueryParameters();
		assertEquals(1, params.getParamsCount());
		assertEquals("Супер", params.getParameter("one"));

		ht.setQueryParameters(new HttpParams("one=Sуp"));
		assertEquals("/jodd?one=S%D1%83p", ht.getPath());

		ht.setQueryParameters(new HttpParams("one=1&one=2"));
		assertEquals("/jodd?one=1&one=2", ht.getPath());
		params = ht.getQueryParameters();
		assertEquals(1, params.getParamsCount());
		assertEquals("1", ((String[]) params.getParameter("one"))[0]);
		assertEquals("2", ((String[]) params.getParameter("one"))[1]);

		params.addParameter("two", "xxx");
		ht.setQueryParameters(params);
		assertEquals("/jodd?one=1&one=2&two=xxx", ht.getPath());
	}

	@Test
	public void testQueryParamsInstances() {
		HttpTransfer httpTransfer = new HttpTransfer();

		httpTransfer.setPath("/jodd?one=1&two=2");
		HttpParams queryParams = httpTransfer.getQueryParameters();

		assertNotNull(queryParams);
		assertEquals(2, queryParams.getParamsCount());

		queryParams.setParameter("one", "173");

		HttpParams queryParams2 = httpTransfer.getQueryParameters();
		assertSame(queryParams, queryParams2);

		assertEquals(2, queryParams.getParamsCount());

		assertTrue(queryParams.modified);

		assertEquals("/jodd?one=173&two=2", httpTransfer.getPath());

		assertFalse(queryParams.modified);

		queryParams.addParameter("three", "3");

		assertTrue(queryParams.modified);
	}

	@Test
	public void testInOut() throws IOException {
		HttpTransfer request = Http.createRequest("GET", "http://jodd.org/?id=173");
		request.addHeader("User-Agent", "Scaly");

		HttpParams httpParams = new HttpParams();
		httpParams.addParameter("one", "funny");
		request.setRequestParameters(httpParams);

		byte[] bytes = request.toArray();


		// read
		HttpTransfer request2 = Http.readRequest(new ByteArrayInputStream(bytes));
		HttpParams httpParams2 = request2.getRequestParameters();

		assertEquals(request.getMethod(), request2.getMethod());
		assertEquals(request.getPath(), request2.getPath());

		assertEquals(request.getHeader("User-Agent"), request2.getHeader("User-Agent"));
		assertEquals(request.getHeader("Content-Type"), request2.getHeader("content-type"));
		assertEquals(request.getHeader("Content-Length"), request2.getHeader("content-length"));

		assertEquals(httpParams.getParamsCount(), httpParams2.getParamsCount());
		assertEquals(httpParams.getParameter("one"), httpParams2.getParameter("one"));
	}

	@Test
	public void testFileUpload() throws IOException {
		HttpTransfer request = Http.createRequest("GET", "http://jodd.org/?id=173");
		request.addHeader("User-Agent", "Scaly");

		HttpParams httpParams = new HttpParams();
		httpParams.addParameter("one", "funny");

		File tmp = FileUtil.createTempFile();
		FileUtil.writeString(tmp, "http");
		httpParams.addParameter("two", tmp);

		request.setRequestParameters(httpParams);

		byte[] bytes = request.toArray();


		// read
		HttpTransfer request2 = Http.readRequest(new ByteArrayInputStream(bytes));
		HttpParams httpParams2 = request2.getRequestParameters();

		assertEquals(request.getMethod(), request2.getMethod());
		assertEquals(request.getPath(), request2.getPath());

		assertEquals(request.getHeader("User-Agent"), request2.getHeader("User-Agent"));
		assertEquals(request.getHeader("Content-Type"), request2.getHeader("content-type"));
		assertEquals(request.getHeader("Content-Length"), request2.getHeader("content-length"));

		assertEquals(httpParams.getParamsCount(), httpParams2.getParamsCount());
		assertEquals(httpParams.getParameter("one"), httpParams2.getParameter("one"));

		FileUpload fu = (FileUpload) httpParams2.getParameter("two");
		assertEquals(4, fu.getSize());

		String str = new String(fu.getFileContent());
		assertEquals("http", str);
	}

}
