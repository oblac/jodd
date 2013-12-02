// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OneTwoActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testOneRedirectAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/oneRedirect.html").send();
		assertNull(response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [333]", response.bodyText());
	}

	@Test
	public void testOneMoveAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/oneMove.html").send();
		assertNull(response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [777]", response.bodyText());
	}

}