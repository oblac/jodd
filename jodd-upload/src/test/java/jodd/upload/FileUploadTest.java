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

package jodd.upload;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class FileUploadTest {

	@Test
	public void testFileNames() throws IOException {
		URL data = FileUploadTest.class.getResource("upload.txt");
		String file = data.getFile();

		MultipartStreamParser msp = new MultipartStreamParser();
		msp.parseRequestStream(new FileInputStream(new File(file)), "ISO-8859-1");

		FileUpload fu = msp.getFile("avatar");
		assertEquals("smiley-cool.png", fu.getHeader().getFileName());

		fu = msp.getFile("attach1");
		assertEquals("file1.txt", fu.getHeader().getFileName());

		fu = msp.getFile("attach2");
		assertEquals("file2.txt", fu.getHeader().getFileName());
	}
}