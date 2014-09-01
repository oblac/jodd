// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testBookGet() {
		HttpResponse response;
		response = HttpRequest.get("localhost:8080/book/123").send();

		assertEquals("MyBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPost() {
		HttpResponse response;
		response = HttpRequest.post("localhost:8080/book/123").send();

		assertEquals("NewBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}
}