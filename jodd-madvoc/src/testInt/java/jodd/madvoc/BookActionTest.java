// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
		response = HttpRequest.get("localhost:8173/book/123").send();

		assertEquals("MyBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPost() {
		HttpResponse response;
		response = HttpRequest.post("localhost:8173/book/123").send();

		assertEquals("NewBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPut() {
		HttpResponse response;
		response = HttpRequest.put("localhost:8173/book/123").send();

		assertEquals("OldBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPartial() {
		HttpResponse response = HttpRequest.put("localhost:8173/bookPartial.hello.html")
				.query("book.iban", "123123123")
				.query("book.foo", "not used")
				.send();

		assertEquals("Hi123123123", response.bodyText().trim());
	}
}