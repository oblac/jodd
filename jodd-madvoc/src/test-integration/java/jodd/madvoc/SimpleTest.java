// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleTest {

	@BeforeClass
	public static void beforeClass() {
		TomcatTestServer.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		TomcatTestServer.stopTomcat();
	}

	// ---------------------------------------------------------------- tests

	@Test
	public void testHelloAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.html").send();
		assertEquals("hello", response.bodyText().trim());
	}

}
