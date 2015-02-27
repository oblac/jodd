// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GoogleMapsTest {

	@Test
	public void testNoBody() throws IOException {
		/*HttpResponse httpResponse = HttpRequest.get("http://maps.googleapis.com/maps/api/geocode/json")
		                .query("address", "14621")
		                .query("sensor", "false")
		                .send();
		 */
		URL data = RawTest.class.getResource("2-response.txt");
		byte[] fileContent = FileUtil.readBytes(data.getFile());

		HttpResponse httpResponse = HttpResponse.readFrom(new ByteArrayInputStream(fileContent));

		try {
			httpResponse.bodyText();
		} catch (Exception ex) {
			fail(ex.toString());
		}

		assertEquals("", httpResponse.bodyText());
	}

	@Test
	public void testNoContentLength() throws IOException {
		URL data = RawTest.class.getResource("3-response.txt");
		byte[] fileContent = FileUtil.readBytes(data.getFile());

		HttpResponse httpResponse = HttpResponse.readFrom(new ByteArrayInputStream(fileContent));

		assertEquals("Body!", httpResponse.bodyText());
	}

}