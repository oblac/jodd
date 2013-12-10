// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testOneAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/foo/hello").send();
		assertEquals("/foo/hello.ok.jsp", response.bodyText().trim());
	}

	@Test
	public void testTwoAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/foo/boo.zoo/two.exec.html").send();
		assertEquals("/foo/boo.zoo/two.exec.jsp", response.bodyText().trim());
	}

	@Test
	public void testUrlRewrite() {
		HttpResponse response = HttpRequest.get("localhost:8080/f__o_o/h_e_l_l_o").send();
		assertEquals("/foo/hello.ok.jsp", response.bodyText().trim());
	}
}