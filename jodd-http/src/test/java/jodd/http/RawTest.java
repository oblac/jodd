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
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class RawTest {

	@Test
	public void testRawResponse1() throws IOException {
		URL data = RawTest.class.getResource("1-response.txt");

		String fileContent = FileUtil.readString(data.getFile());

		fileContent = StringUtil.replace(fileContent, "\r\n", "\n");

		HttpResponse response = HttpResponse.readFrom(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));

		HttpMultiMap<String> headers = response.headers;
		assertEquals(7, headers.size());

		assertEquals("no-cache", headers.get("pragma"));
		assertEquals("Sat, 23 Mar 2013 23:34:18 GMT", headers.get("date"));
		assertEquals("max-age=0, must-revalidate, no-cache, no-store, private, post-check=0, pre-check=0",
				headers.get("cache-control"));
		assertEquals("no-cache", headers.get("pragma"));
		assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", headers.get("expires"));
		assertEquals("text/html;charset=UTF-8", headers.get("content-type"));
		assertEquals("close", headers.get("connection"));
		assertEquals("102", headers.get("content-length"));

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

	@Test
	public void testRawResponse6() throws IOException {
		URL data = RawTest.class.getResource("6-response.txt");

		String fileContent = FileUtil.readString(data.getFile());

		fileContent = StringUtil.replace(fileContent, "\n", "\r\n");
		fileContent = StringUtil.replace(fileContent, "\r\r\n", "\r\n");

		HttpResponse response = HttpResponse.readFrom(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));

		assertEquals(200, response.statusCode());
		assertEquals("", response.statusPhrase);

		String body = response.bodyText();

		assertEquals(
				"Wikipedia in\n" +
				"\n" +
				"chunks.", body.replace("\r\n", "\n"));

		assertEquals("TheData", response.header("SomeAfterHeader"));
	}


}
