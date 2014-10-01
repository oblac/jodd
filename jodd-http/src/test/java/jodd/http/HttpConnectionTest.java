// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.datetime.JDateTime;
import jodd.datetime.JStopWatch;
import jodd.io.FileUtil;
import jodd.util.RandomStringUtil;
import jodd.util.StringUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpConnectionTest {

	@Test
	public void testEcho() throws IOException {
		EchoTestServer echoTestServer = new EchoTestServer();

		HttpResponse response = HttpRequest.get("http://localhost:8081/hello?id=12").send();

		assertEquals(200, response.statusCode());
		assertEquals("OK", response.statusPhrase());

		assertEquals("GET", echoTestServer.method);
		assertEquals("/hello", echoTestServer.uri);
		assertEquals(1, echoTestServer.params.size());
		assertEquals("12", echoTestServer.params.get("id"));

		assertEquals("GET /hello", response.body());

		echoTestServer.stop();
	}

	@Test
	public void testUpload() throws IOException {
		EchoTestServer echoTestServer = new EchoTestServer();

		File file = FileUtil.createTempFile();
		file.deleteOnExit();

		FileUtil.writeString(file, "upload тест");
		assertEquals("upload тест", FileUtil.readString(file));

		HttpResponse response = HttpRequest
				.post("http://localhost:8081/hello")
				.form("id", "12")
				.form("file", file)
				.send();

		assertEquals(200, response.statusCode());
		assertEquals("OK", response.statusPhrase());

		assertEquals("POST", echoTestServer.method);
		assertEquals("12", echoTestServer.params.get("id"));
		File uploadedFile = new File(echoTestServer.files.get("file").toString());
		assertNotNull(uploadedFile);
		assertEquals("upload тест", FileUtil.readString(uploadedFile));

		assertEquals("POST /hello", response.body());

		echoTestServer.stop();
		file.delete();
	}

	@Test
	public void testUploadWithMonitor() throws IOException {
		EchoTestServer echoTestServer = new EchoTestServer();

		File file = FileUtil.createTempFile();
		file.deleteOnExit();

		FileUtil.writeString(file, StringUtil.repeat('A', 1024));

		final StringBuilder sb = new StringBuilder();

		HttpResponse response = HttpRequest
				.post("http://localhost:8081/hello")
				.form("id", "12")
				.form("file", file)
				.monitor(new HttpProgressListener() {
					@Override
					public void transferred(long len) {
						sb.append(":" + len);
					}
				})
				.send();

		assertEquals(200, response.statusCode());
		assertEquals("OK", response.statusPhrase());

		echoTestServer.stop();
		file.delete();

		assertEquals(":0:512:1024:1486", sb.toString());
	}

}
