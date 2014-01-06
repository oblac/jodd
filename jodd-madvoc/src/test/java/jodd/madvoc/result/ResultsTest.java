// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.madvoc.MadvocTomcatServer;
import jodd.madvoc.TestServer;
import jodd.util.MimeTypes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ResultsTest {

	static TestServer testServer;

	@BeforeClass
	public static void startServer() throws Exception {
		testServer = new MadvocTomcatServer();
		testServer.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		testServer.stop();
	}

	// ---------------------------------------------------------------- raw

	@Test
	public void testRawResult() {
		HttpResponse httpResponse = HttpRequest.get("localhost:8080/madvocRawImage").send();
		assertEquals(200, httpResponse.statusCode());
		assertEquals("image/gif", httpResponse.contentType());
		byte[] bytes = httpResponse.bodyBytes();
		assertArrayEquals(SMALLEST_GIF, bytes);
	}

	public RawData madvocRawImage() {
		return new RawData(SMALLEST_GIF, MimeTypes.lookupMimeType("gif"));
	}

	public static final byte[] SMALLEST_GIF = new byte[] {
		0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00,
		0x01, 0x00, 0x00, 0x00, 0x00, 0x2c, 0x00, 0x00,
		0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02,
		0x02, 0x4c, 0x01, 0x00, 0x3b
	};

	// ---------------------------------------------------------------- text

	@Test
	public void testEncoding() {
		HttpResponse httpResponse = HttpRequest.get("localhost:8080/textResultEncoding").send();
		assertEquals(200, httpResponse.statusCode());
		assertEquals("text/plain;charset=UTF-8", httpResponse.contentType());
		assertEquals("this text contents chinese chars 中文", httpResponse.bodyText());
	}

	public String madvocEncoding() {
		return "text:this text contents chinese chars 中文";
	}

}