// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.http;

import jodd.io.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
