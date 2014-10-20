// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RawTest {

	@Test
	public void testRawResponse1() throws IOException {
		URL data = RawTest.class.getResource("1-response.txt");

		String fileContent = FileUtil.readString(data.getFile());

		fileContent = StringUtil.replace(fileContent, "\r\n", "\n");

		HttpResponse response = HttpResponse.readFrom(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));

		Map<String, String[]> headers = response.headers();
		assertEquals(7, headers.size());

		assertEquals("no-cache", headers.get("pragma")[0]);
		assertEquals("Sat, 23 Mar 2013 23:34:18 GMT", headers.get("date")[0]);
		assertEquals("max-age=0, must-revalidate, no-cache, no-store, private, post-check=0, pre-check=0",
				headers.get("cache-control")[0]);
		assertEquals("no-cache", headers.get("pragma")[0]);
		assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", headers.get("expires")[0]);
		assertEquals("text/html;charset=UTF-8", headers.get("content-type")[0]);
		assertEquals("close", headers.get("connection")[0]);
		assertEquals("102", headers.get("content-length")[0]);

		assertEquals("no-cache", response.header("Pragma"));
		assertEquals("text/html;charset=UTF-8" , response.contentType());
		assertEquals("text/html" , response.mediaType());
		assertEquals("UTF-8" , response.charset());

		assertNotNull(response.contentLength());

		String rawBody = response.body();
		String textBody = response.bodyText();

		assertTrue(rawBody.startsWith("<html>"));
		assertTrue(rawBody.endsWith("</html>"));

		assertTrue(rawBody.contains("This is UTF8 Encoding."));
		assertFalse(rawBody.contains("Тхис ис УТФ8 Енцодинг."));
		assertTrue(textBody.contains("Тхис ис УТФ8 Енцодинг."));

		assertEquals(77, textBody.length());

		int len = textBody.getBytes("UTF-8").length;

		assertEquals(94, rawBody.length());
		assertEquals(len, rawBody.length());
	}

	@Test
	public void testRawResponse4() throws IOException {
		URL data = RawTest.class.getResource("4-response.txt");

		String fileContent = FileUtil.readString(data.getFile());

		fileContent = StringUtil.replace(fileContent, "\n", "\r\n");
		fileContent = StringUtil.replace(fileContent, "\r\r\n", "\r\n");

		HttpResponse response = HttpResponse.readFrom(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));

		String body = response.bodyText();

		assertEquals(
				"Wikipedia in\n" +
				"\n" +
				"chunks.", body.replace("\r\n", "\n"));
	}


	@Test
	public void testRawResponse5() throws IOException {
		URL data = RawTest.class.getResource("5-response.txt");

		String fileContent = FileUtil.readString(data.getFile());

		fileContent = StringUtil.replace(fileContent, "\n", "\r\n");
		fileContent = StringUtil.replace(fileContent, "\r\r\n", "\r\n");

		HttpResponse response = HttpResponse.readFrom(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));

		String body = response.bodyText();

		assertEquals(
				"Wikipedia in\n" +
				"\n" +
				"chunks.", body.replace("\r\n", "\n"));

		assertEquals("TheData", response.header("SomeAfterHeader"));
	}


}