// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.madvoc.action.RawResultAction;
import jodd.madvoc.result.RawData;
import jodd.util.MimeTypes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ResultsTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	// ---------------------------------------------------------------- raw

	@Test
	public void testRawResult() {
		HttpResponse httpResponse = HttpRequest.get("localhost:8173/madvocRawImage").send();
		assertEquals(200, httpResponse.statusCode());
		assertEquals("image/gif", httpResponse.contentType());
		byte[] bytes = httpResponse.bodyBytes();
		assertArrayEquals(RawResultAction.SMALLEST_GIF, bytes);
	}

	// ---------------------------------------------------------------- text

	@Test
	public void testEncoding() {
		HttpResponse httpResponse = HttpRequest.get("localhost:8173/madvocEncoding").send();
		assertEquals(200, httpResponse.statusCode());
		assertEquals("text/plain;charset=UTF-8", httpResponse.contentType());
		assertEquals("this text contents chinese chars 中文", httpResponse.bodyText());
	}

}
