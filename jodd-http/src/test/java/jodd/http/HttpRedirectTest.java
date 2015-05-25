// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpRedirectTest {

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
	public void testRedirect() {
		HttpRequest httpRequest = HttpRequest.get("localhost:8173/redirect");

		HttpResponse httpResponse = httpRequest.send();

		assertEquals(302, httpResponse.statusCode);

		HttpBrowser httpBrowser = new HttpBrowser();

		httpBrowser.sendRequest(
				HttpRequest.get("localhost:8173/redirect"));

		httpResponse = httpBrowser.getHttpResponse();

		assertNotNull(httpResponse);
		assertEquals("target!", httpResponse.body());
	}

}