// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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