// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntcptActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testIn1Action() {
		HttpResponse response = HttpRequest.get("localhost:8080/cpt.in1.html?foo=173").send();
		assertEquals("param:  = 173", response.bodyText().trim());
	}

	@Test
	public void testIn2Action() {
		HttpResponse response = HttpRequest.get("localhost:8080/cpt.in2.html?foo=173&foo2=173").send();
		assertEquals("param: 173 = 173", response.bodyText().trim());
	}

	@Test
	public void testAppendingAction() {
		HttpResponse response = HttpRequest.get("localhost:8080/cpt.inap.html").send();
		assertEquals("value=appending<jodd>", response.bodyText().trim());
	}

	@Test
	public void testAppending2Action() {
		HttpResponse response = HttpRequest.get("localhost:8080/cpt.inap2.html").send();
		assertEquals("value=appending2<heyp>", response.bodyText().trim());
	}

	@Test
	public void testAppending3Action() {
		HttpResponse response = HttpRequest.get("localhost:8080/cpt.inap3.html").send();
		assertEquals("value=appending3<jodd>", response.bodyText().trim());
	}


}