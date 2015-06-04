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

package jodd.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZipUtilTest {

	protected String dataRoot;

	@Before
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile();
	}

	@Test
	public void testGzip() throws IOException {
		ZipUtil.gzip(new File(dataRoot, "sb.data"));
		File gzipFile = new File(dataRoot, "sb.data.gz");
		assertTrue(gzipFile.exists());

		FileUtil.move(gzipFile, new File(dataRoot, "sb2.data.gz"));
		ZipUtil.ungzip(new File(dataRoot, "sb2.data.gz"));
		File data = new File(dataRoot, "sb2.data");
		assertTrue(data.exists());

		byte[] data2Bytes = FileUtil.readBytes(data);
		byte[] data1Bytes = FileUtil.readBytes(new File(dataRoot, "sb.data"));

		assertTrue(Arrays.equals(data1Bytes, data2Bytes));

		// cleanup
		FileUtil.delete(new File(dataRoot, "sb2.data"));
		FileUtil.delete(new File(dataRoot, "sb2.data.gz"));
	}

	@Test
	public void testZlib() throws IOException {
		ZipUtil.zlib(new File(dataRoot, "sb.data"));
		File zlibFile = new File(dataRoot, "sb.data.zlib");
		assertTrue(zlibFile.exists());

		// cleanup
		FileUtil.delete(zlibFile);
	}

	@Test
	public void testZip() throws IOException {
		ZipUtil.zip(new File(dataRoot, "sb.data"));
		File zipFile = new File(dataRoot, "sb.data.zip");
		assertTrue(zipFile.exists());

		// cleanup
		FileUtil.delete(zipFile);

		ZipUtil.zip(new File(dataRoot, "file"));
		zipFile = new File(dataRoot, "file.zip");
		assertTrue(zipFile.exists());

		// cleanup
		FileUtil.delete(zipFile);
	}

	@Test
	public void testZipDir() throws IOException {
		ZipUtil.zip(new File(dataRoot));
		File zipFile = new File(dataRoot + ".zip");
		assertTrue(zipFile.exists());

		int directoryCount = 0;

		ZipFile zipfile = new ZipFile(zipFile);
		try {
			for (Enumeration<? extends ZipEntry> entries = zipfile.entries(); entries.hasMoreElements(); ) {
				ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.isDirectory()) {
					directoryCount++;

					assertTrue(zipEntry.getName().equals("data/") || zipEntry.getName().equals("data/file/"));
				}
			}
		} finally {
			zipfile.close();
		}

		assertEquals(2, directoryCount);

		// cleanup
		FileUtil.delete(zipFile);
	}

	@Test
	public void testZipStreams() throws IOException {
		File zipFile = new File(dataRoot, "test.zip");

		ZipOutputStream zos = ZipUtil.createZip(zipFile);

		ZipUtil.addToZip(zos).file(dataRoot, "sb.data").path("sbdata").comment("This is sb data file").add();

		ZipUtil.addToZip(zos).file(dataRoot, "file").path("folder").comment("This is a folder and all its files").add();

		StreamUtil.close(zos);

		assertTrue(zipFile.exists());

		ZipUtil.unzip(zipFile, new File(dataRoot), "sbda*");

		assertTrue(new File(dataRoot, "sbdata").exists());
		assertFalse(new File(dataRoot, "folder").exists());

		ZipUtil.unzip(zipFile, new File(dataRoot));

		assertTrue(new File(dataRoot, "sbdata").exists());
		assertTrue(new File(dataRoot, "folder").exists());
		assertTrue(new File(new File(dataRoot, "folder"), "a.png").exists());

		// cleanup
		FileUtil.delete(new File(dataRoot, "sbdata"));
		FileUtil.deleteDir(new File(dataRoot, "folder"));
		FileUtil.delete(zipFile);
	}

}
