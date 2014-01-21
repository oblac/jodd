// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpUtilTest {

	@Test
	public void testNiceHeaderNames() {
		assertEquals("Content-Type", HttpUtil.prepareHeaderParameterName("conTent-tyPe"));
		assertEquals("ETag", HttpUtil.prepareHeaderParameterName("etag"));
	}

	@Test
	public void testMediaTypeAndParameters() {
		String contentType = "text/html";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals(null, HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html; charset=ISO-8859-4";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html;charset=ISO-8859-4";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html; pre=foo; charset=ISO-8859-4";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html; pre=foo; charset=ISO-8859-4; post=bar";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));
		assertEquals("foo", HttpUtil.extractHeaderParameter(contentType, "pre", ';'));
		assertEquals(null, HttpUtil.extractHeaderParameter(contentType, "na", ';'));
	}

}