// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testUserActionGet() {
		HttpBrowser httpBrowser = new HttpBrowser();
		HttpResponse response = httpBrowser.sendRequest(
				HttpRequest.get("localhost:8080/sys/user/123"));

		assertEquals("Huh 123.", response.bodyText().trim());
	}

	@Test
	public void testUserActionPost() {
		HttpBrowser httpBrowser = new HttpBrowser();
		HttpResponse response = httpBrowser.sendRequest(
				HttpRequest.post("localhost:8080/sys/user/123"));

		assertEquals("Post 123.", response.bodyText().trim());
	}

}