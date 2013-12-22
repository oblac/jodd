// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpBrowserTest {
	
	static TestServer testServer;

	@BeforeClass
	public static void startServer() throws Exception {
		testServer = new TomcatServer();
		testServer.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		testServer.stop();
	}
	
	@Before
	public void setUp() {
		EchoServlet.testinit();
	}

	@Test
	public void testBrowser() {
		HttpBrowser httpBrowser = new HttpBrowser();

		httpBrowser.sendRequest(
				HttpRequest.get("localhost:8080/echo?id=17").bodyText("hello"));
		HttpResponse httpResponse = httpBrowser.getHttpResponse();

		assertNotNull(httpResponse);
		assertEquals("hello", httpResponse.body());
	}
	
}