// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

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

}