// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlphaTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testForwardTo() {
		HttpResponse response;

		response = HttpRequest.get("localhost:8080/alpha.html").send();
		assertEquals("alpha.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.hello.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.ciao.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.ciao2.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.hola.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.holahoopa.html").send();
		assertEquals("alpha.hola.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.home.html").send();
		assertEquals("hello", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.home2.html").send();
		assertEquals("Hello world", response.bodyText().trim());
	}

	@Test
	public void testRedirectTo() {
		HttpResponse response;
		HttpBrowser browser = new HttpBrowser();

		response = browser.sendRequest(HttpRequest.get("localhost:8080/alpha.red1.html"));
		assertEquals("alpha.jsp", response.bodyText().trim());

		response = browser.sendRequest(HttpRequest.get("localhost:8080/alpha.red2.html"));
		assertEquals("hello", response.bodyText().trim());

		response = browser.sendRequest(HttpRequest.get("localhost:8080/alpha.world.html"));
		assertEquals("Hello world planet Mars and Universe 173", response.bodyText().trim());

		response = browser.sendRequest(HttpRequest.get("localhost:8080/alpha.postme.html"));
		assertEquals("alpha.hello.jsp", response.bodyText().trim());
	}

	@Test
	public void testText() {
		HttpResponse response;

		response = HttpRequest.get("localhost:8080/alpha.txt.html").send();
		assertEquals("some text", response.bodyText().trim());
	}

	@Test
	public void testChain() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.chain.html?chain=7").send();
		assertEquals("chain:9", response.bodyText().trim());
	}

}