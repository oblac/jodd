// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RouterTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocTwoSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocTwoSuite.stopTomcat();
	}

	@Test
	public void testRouterFile() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.html").send();
		assertEquals("hello", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/helloWorld.html?name=Jupiter&data=3").send();
		assertEquals("Hello world planet Jupiter and Universe 3", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/re/view/234").send();
		assertEquals("234", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/alpha.ciao.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());
	}
}