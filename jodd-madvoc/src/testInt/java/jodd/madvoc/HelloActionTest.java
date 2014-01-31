// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HelloActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testHelloAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.html").send();
		assertEquals("hello", response.bodyText().trim());

		response = HttpRequest.get("localhost:8080/pac/hello.html").send();
		assertEquals("HELLO", response.bodyText().trim());
	}

	@Test
	public void testHelloWorldAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.world.html?name=Jupiter&data=3").send();
		assertEquals("Hello world planet Jupiter and Universe 3", response.bodyText().trim());
	}

	@Test
	public void testHelloBeanAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.bean.html?p.name=Jupiter&p.data=3").send();
		assertEquals("Person{name='Jupiter', data=3}", response.bodyText().trim());
	}

	@Test
	public void testHelloDirectAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.direct.html").send();
		assertEquals("Direct stream output", response.bodyText().trim());
	}

	@Test
	public void testHelloNoJspAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/nohello.nojsp.html").send();
		assertEquals(404, response.statusCode());
		assertTrue(response.bodyText().contains("/nohello.nojsp.html"));
	}

	@Test
	public void testChain() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.chain.html?chain=7").send();
		assertEquals("chain:9", response.bodyText().trim());
	}

	@Test
	public void testMany() {
		HttpResponse response = HttpRequest.get(
				"localhost:8080/hello.many.html?" +
				"ppp[0].name=Aaa&ppp[0].data=1&ppp[1].name=Bbb&ppp[1].data=2&ppp[2].name=Ccc&ppp[2].data=3").send();
		assertEquals(
				"0 Aaa-1\n" +
				"1 Bbb-2\n" +
				"2 Ccc-3\n" +
				"0 Aaa-1\n" +
				"1 Bbb-2\n" +
				"2 Ccc-3\n" +
				"{0=Person{name='Aaa', data=1}, 1=Person{name='Bbb', data=2}, 2=Person{name='Ccc', data=3}}", response.bodyText().trim());
	}

	@Test
	public void testBackBack() {
		HttpResponse response = HttpRequest.get("localhost:8080/hello.backback.html").send();
		assertEquals("default.big", response.bodyText().trim());
	}

}
