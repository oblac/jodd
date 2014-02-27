// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import jodd.io.StringInputStream;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

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
		String fileContent = FileUtil.readString(data.getFile());

		HttpResponse httpResponse = HttpResponse.readFrom(new StringInputStream(fileContent, StringInputStream.Mode.ASCII));

		try {
			httpResponse.bodyText();
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}

}