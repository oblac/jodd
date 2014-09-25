// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
		HttpResponse response = HttpRequest.get("localhost:8173/oneRedirect.html").send();
		assertEquals("", response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [333]", response.bodyText());
	}

	@Test
	public void testOneMoveAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneMove.html").send();
		assertEquals("", response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [777]", response.bodyText());
	}

	@Test
	public void testOneMoveGoAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneMove.go.html").send();
		assertEquals("", response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [888]", response.bodyText());
	}

	@Test
	public void testOneRedirectPermanentAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneRedirect.perm.html").send();
		assertEquals("", response.bodyText());
		assertEquals(301, response.statusCode());

		String redirectLocation = response.header("location");
		assertEquals("/two.html?value=444", redirectLocation);
	}

}