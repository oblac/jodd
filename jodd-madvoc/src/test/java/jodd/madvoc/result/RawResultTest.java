package jodd.madvoc.result;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.madvoc.MadvocWinstoneServer;
import jodd.util.MimeTypes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RawResultTest {

	static MadvocWinstoneServer winstoneServer;

	@BeforeClass
	public static void startServer() throws IOException {
		winstoneServer = new MadvocWinstoneServer();
		winstoneServer.start();
	}

	@AfterClass
	public static void stopServer() {
		winstoneServer.stop();
	}

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

}