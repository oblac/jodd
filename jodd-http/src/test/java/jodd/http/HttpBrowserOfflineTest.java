// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpBrowserOfflineTest {

	@Test
	public void testDefaultParameters() {
		HttpBrowser httpBrowser = new HttpBrowser();
		httpBrowser.setDefaultHeader("aaa", "123");

		HttpRequest request = HttpRequest.get("foo.com");
		request.header("bbb", "987");

		httpBrowser.addDefaultHeaders(request);

		assertEquals(3, request.headers().size());
		assertEquals("123", request.header("aaa"));
		assertEquals("987", request.header("bbb"));
	}

	@Test
	public void testDefaultParametersOverwrite() {
		HttpBrowser httpBrowser = new HttpBrowser();
		httpBrowser.setDefaultHeader("aaa", "123");

		HttpRequest request = HttpRequest.get("foo.com");
		request.header("aaa", "987");

		httpBrowser.addDefaultHeaders(request);

		assertEquals(2, request.headers().size());
		assertEquals("987", request.header("aaa"));
	}
}