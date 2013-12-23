// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SessionScopeTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testSessionScope() {
		HttpResponse response = HttpRequest.get("localhost:8080/item.html").send();
		String out1 = response.bodyText().trim();

		response = HttpRequest.get("localhost:8080/item.html").send();
		String out2 = response.bodyText().trim();

		assertFalse(out1.equals(out2));

		String jsessionid = out2.substring(out2.indexOf("sid:") + 4);

		response = HttpRequest.get("localhost:8080/item.html;jsessionid=" + jsessionid).send();
		String out3 = response.bodyText().trim();

		assertEquals(out2, out3);
	}

	@Test
	public void testSessionScopeWithScopedProxy() {
		HttpResponse response = HttpRequest.get("localhost:8080/item.global.html").send();
		String out1 = response.bodyText().trim();

		response = HttpRequest.get("localhost:8080/item.global.html").send();
		String out2 = response.bodyText().trim();

		assertFalse(out1.equals(out2));

		String jsessionid = out2.substring(out2.indexOf("sid:") + 4);

		response = HttpRequest.get("localhost:8080/item.global.html;jsessionid=" + jsessionid).send();
		String out3 = response.bodyText().trim();

		assertEquals(out2, out3);
	}
}