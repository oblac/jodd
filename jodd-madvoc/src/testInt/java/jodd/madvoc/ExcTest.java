// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExcTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testException() {
		HttpBrowser httpBrowser = new HttpBrowser();
		HttpResponse response = httpBrowser.sendRequest(HttpRequest.get("localhost:8173/exc.html"));

		assertEquals("500!", response.bodyText().trim());
	}

	@Test
	public void testRedirect500() {
		HttpBrowser httpBrowser = new HttpBrowser();
		HttpResponse response = httpBrowser.sendRequest(HttpRequest.get("localhost:8173/exc.red.html"));

		assertEquals("500!", response.bodyText().trim());
	}
}