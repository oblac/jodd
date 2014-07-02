// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.up.ByteArrayUploadable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testMoveWithFiles() {
		HttpResponse response;
		response = HttpRequest
				.post("localhost:8080/mv/upload.html")
				.form("uploadFiles[0]", new ByteArrayUploadable(new byte[] {65, 66, 67}, "hello.txt"))
				.form("uploadFiles[1]", new byte[] {75, 77, 78})
				.form("uploadFileNames[0]", "a1")
				.form("uploadFileNames[1]", "a2")
				.send();

		assertEquals(302, response.statusCode());

		String location = response.header("location");

		response = HttpRequest.get(location).send();

		assertEquals("33hello.txt 33uploadFiles[1] a1 a2 ", response.bodyText());
	}

}