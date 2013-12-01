// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

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

}
