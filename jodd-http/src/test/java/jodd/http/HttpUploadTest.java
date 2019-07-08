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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpUploadTest {

	static TestServer testServer;

	@BeforeAll
	static void startServer() throws Exception {
		testServer = new TomcatServer();
		testServer.start();
	}

	@AfterAll
	static void stopServer() throws Exception {
		testServer.stop();
	}


	@Test
	void uploadTest() throws IOException {
		File temp1 = FileUtil.createTempFile();
		FileUtil.writeString(temp1, "Temp1 content");
		File temp2 = FileUtil.createTempFile();
		FileUtil.writeString(temp2, "Temp2 content");

		temp1.deleteOnExit();
		temp2.deleteOnExit();

		HttpRequest httpRequest = HttpRequest.post("localhost:8173/echo")
			.form(
				"title", "test",
				"description", "Upload test",
				"file1", temp1,
				"file2", temp2
			);


		HttpResponse httpResponse = httpRequest.send();
		String body = httpResponse.bodyText();

		assertTrue(body.contains("Temp1 content"));
		assertTrue(body.contains("Temp2 content"));
	}
}
