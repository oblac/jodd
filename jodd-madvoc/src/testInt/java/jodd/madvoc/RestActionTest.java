// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testRestAction1() {
		HttpResponse response = HttpRequest.get("localhost:8080/re/view/123").send();
		assertEquals("123", response.bodyText().trim());
	}

	@Test
	public void testRestAction2() {
		HttpResponse response = HttpRequest.get("localhost:8080/re/view2/g-321.html").send();
		assertEquals(302, response.statusCode());

		response = HttpRequest.get(response.header("location")).send();
		assertEquals("321", response.bodyText().trim());
	}

	@Test
	public void testRestAction3() {
		HttpResponse response = HttpRequest.get("localhost:8080/re/view3/555").send();
		assertEquals("555", response.bodyText().trim());
	}

	@Test
	public void testRestAction3_nomatch() {
		HttpResponse response = HttpRequest.get("localhost:8080/re/view3/1x2").send();
		assertEquals(404, response.statusCode());
	}

}