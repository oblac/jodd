// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TagActionTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testDisableTag() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/tag/disable/123")
				.send();
		assertEquals("disable-Tag{123:jodd}", response.bodyText().trim());
	}

	@Test
	public void testDeleteTag() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/tag/delete/123")
				.send();
		assertEquals("delete-Tag{123:jodd}", response.bodyText().trim());
	}

	@Test
	public void testEditTag() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/tag/edit/123")
				.query("tag.name", "ddoj")
				.send();
		assertEquals("edit-Tag{123:ddoj}", response.bodyText().trim());
	}

	@Test
	public void testSaveTag() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/tag/save/123")
				.query("tag.name", "JODD")
				.send();
		assertEquals("save-Tag{123:JODD}", response.bodyText().trim());
	}


}