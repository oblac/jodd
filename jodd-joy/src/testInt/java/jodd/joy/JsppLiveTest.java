// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsppLiveTest {

	@BeforeClass
	public static void beforeClass() {
		JoySuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		JoySuite.stopTomcat();
	}

	@Test
	public void testSimpleJspp() {
		HttpResponse response = HttpRequest.get("http://localhost:8080/hello.html").send();

		assertEquals("Hello world Jupiter! zap!", response.bodyText());

		// send it again

		response = HttpRequest.get("http://localhost:8080/hello.html").send();

		assertEquals("Hello world Jupiter! zap!", response.bodyText());
	}

}