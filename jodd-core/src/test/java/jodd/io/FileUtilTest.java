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

import jodd.system.SystemUtil;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FileUtilTest {

	protected String dataRoot;
	protected String utfdataRoot;

	@BeforeEach
	void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile();
		data = FileUtilTest.class.getResource("utf");
		utfdataRoot = data.getFile();
	}

	@Test
	void testFileManipulation() throws IOException {
		FileUtil.copy(new File(dataRoot, "sb.data"), new File(dataRoot, "sb1.data"));
		assertFalse(FileUtil.isNewer(new File(dataRoot, "sb.data"), new File(dataRoot, "sb1.data")));
		assertFalse(FileUtil.isOlder(new File(dataRoot, "sb.data"), new File(dataRoot, "sb1.data")));
		FileUtil.delete(new File(dataRoot, "sb1.data"));
	}

	@Test
	void testString() {
		String s = "This is a test file\nIt only has\nthree lines!!";

		try {
			FileUtil.writeString(new File(dataRoot, "test.txt"), s);
		} catch (Exception ex) {
			fail("FileUtil.writeString " + ex.toString());
		}

		String s2 = null;
		try {
			s2 = FileUtil.readString(dataRoot + "/test.txt");
		} catch (Exception ex) {
			fail("FileUtil.readString " + ex.toString());
		}
		assertEquals(s, s2);

		// test unicode chars (i.e. greater then 255)
		char[] buf = s.toCharArray();
		buf[0] = 256;
		s = new String(buf);

		try {
			FileUtil.writeString(dataRoot + "/test.txt", s);
		} catch (Exception ex) {
			fail("FileUtil.writeString " + ex.toString());
		}

		try {
			s2 = FileUtil.readString(dataRoot + "/test.txt");
		} catch (Exception ex) {
			fail("FileUtil.readString " + ex.toString());
		}

		assertEquals(s.substring(1), s2.substring(1));
		assertEquals(s.charAt(0), s2.charAt(0));

		try {
			FileUtil.delete(dataRoot + "/test.txt");
		} catch (IOException ioex) {
			fail("FileUtil.delete" + ioex.toString());
		}
	}

	@Test
	void testUnicodeString() {
		String s = "This is a test file\nIt only has\nthree lines!!";

		char[] buf = s.toCharArray();
		buf[0] = 256;
		s = new String(buf);

		try {
			FileUtil.writeString(dataRoot + "/test2.txt", s, "UTF-16");
		} catch (Exception ex) {
			fail("FileUtil.writeString " + ex.toString());
		}

		String s2 = null;
		try {
			s2 = FileUtil.readString(dataRoot + "/test2.txt", "UTF-16");
		} catch (Exception ex) {
			fail("FileUtil.readString " + ex.toString());
		}
		assertEquals(s, s2);

		try {
			FileUtil.delete(dataRoot + "/test2.txt");
		} catch (IOException ioex) {
			fail("FileUtil.delete" + ioex.toString());
		}

	}

	@Test
	void testFileManipulations() {
		String root = dataRoot + "/file/";
		String tmp = root + "tmp/";
		String tmp2 = root + "xxx/";
		String tmp3 = root + "zzz/";

		// copy
		try {
			FileUtil.copyFile(root + "a.txt", root + "w.txt");
			FileUtil.copyFile(root + "a.png", root + "w.png");
			FileUtil.copyFile(root + "a.txt", root + "w.txt");
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		// mkdirs
		try {
			FileUtil.mkdir(tmp);
			FileUtil.mkdirs(tmp + "x/");
			FileUtil.copyFileToDir(root + "a.txt", tmp);
			FileUtil.copyFileToDir(root + "a.png", tmp);
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		// move
		try {
			FileUtil.moveFile(root + "w.txt", tmp + "w.txt");
			FileUtil.moveFileToDir(root + "w.png", tmp);
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		// delete

		try {
			FileUtil.deleteFile(tmp + "a.txt");
			FileUtil.deleteFile(tmp + "a.png");
			FileUtil.deleteFile(tmp + "w.txt");
			FileUtil.deleteFile(tmp + "w.png");
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		try {
			FileUtil.deleteFile(tmp + "a.txt");
			fail("delete file strict delete");
		} catch (IOException e) {
			// ignore
		}

		// movedir
		try {
			FileUtil.moveDir(tmp, tmp2);
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		// copyDir
		try {
			FileUtil.copyDir(tmp2, tmp3);
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		// deleteDir
		try {
			FileUtil.deleteDir(tmp2);
			FileUtil.deleteDir(tmp3);
		} catch (IOException ioex) {
			fail(ioex.toString());
		}
	}

	@Test
	void testBytes() {
		try {
			File file = new File(dataRoot + "/file/a.txt");
			byte[] bytes = FileUtil.readBytes(dataRoot + "/file/a.txt");

			assertEquals(file.length(), bytes.length);
			String content = new String(bytes);
			content = StringUtil.remove(content, '\r');
			assertEquals("test file\n", content);

		} catch (IOException ioex) {
			fail(ioex.toString());
		}

	}

	@Test
	void testUTFReads() throws IOException {
		String content = FileUtil.readUTFString(new File(utfdataRoot, "utf-8.txt"));
		content = content.replace("\r\n", "\n");

		String content8 = FileUtil.readString(new File(utfdataRoot, "utf-8.txt"), "UTF-8");
		content8 = content8.replace("\r\n", "\n");
		assertEquals(content, content8);

		String content1 = FileUtil.readUTFString(new File(utfdataRoot, "utf-16be.txt"));
		content1 = content1.replace("\r\n", "\n");
		assertEquals(content, content1);

		String content16 = FileUtil.readString(new File(utfdataRoot, "utf-16be.txt"), "UTF-16BE");
		content16 = content16.replace("\r\n", "\n");
		assertEquals(content, content16);

		String content162 = FileUtil.readString(new File(utfdataRoot, "utf-16be.txt"), "UTF-16");
		content162 = content162.replace("\r\n", "\n");
		assertEquals(content, content162);

		String content2 = FileUtil.readUTFString(new File(utfdataRoot, "utf-16le.txt"));
		content2 = content2.replace("\r\n", "\n");
		assertEquals(content, content2);

		String content163 = FileUtil.readString(new File(utfdataRoot, "utf-16le.txt"), "UTF-16LE");
		content163 = content163.replace("\r\n", "\n");
		assertEquals(content, content163);
	}

	@Test
	void testIsAncestor() {
		File folder = new File("/foo/bar");
		File file = new File(folder, "foo.txt");

		assertTrue(FileUtil.isAncestor(folder, folder, false));
		assertFalse(FileUtil.isAncestor(folder, folder, true));

		assertTrue(FileUtil.isAncestor(folder, file, false));
		assertTrue(FileUtil.isAncestor(folder, file, true));

		file = new File(folder, "../foo.txt");

		assertFalse(FileUtil.isAncestor(folder, file, false));
		assertFalse(FileUtil.isAncestor(folder, file, true));

		file = new File(folder, "bar/../../../foo.txt");

		assertFalse(FileUtil.isAncestor(folder, file, false));
		assertFalse(FileUtil.isAncestor(folder, file, true));

		file = new File(folder, "bar/car/../foo.txt");

		assertTrue(FileUtil.isAncestor(folder, file, false));
		assertTrue(FileUtil.isAncestor(folder, file, true));
	}

	@ParameterizedTest (name = "{index} : FileUtil#{0}")
	@CsvSource(
			{
				"md5, 529a2cfd3346c7fe17b845b6ec90fcfd",
//				"sha1, 7687b985b2eeff4a981480cead1787bd3f26929c",
				"sha256, b2a3dec0059df342e9b33721957fd54221ab7fb7daa99d9f35af729dc2568e51",
//				"sha384, 1eb67f4b35ae69bbd815dbceee9584c9a65b82e8a209b0a3ab9e6def0a74cf5915228ce32f6154ba5c9ee6dfc66f6414",
				"sha512, 4b53d8ca344fc63dd0a69b2ef4c5275279b4c31a834d5e0501a0ab646d1cc56f15e45a019e3f46597be288924b8b6fba19e4ebad1552f5007d56e7f12c3cb1d2"
			}
	)
	@DisplayName(value = "tests for digest-algorithms")
	void testDigestAlgorithms(final String method, final String expected) throws Exception {
		Method declaredMethod = FileUtil.class.getMethod(method, File.class);
		File file = new File(FileUtilTest.class.getResource("data/file/a.png").toURI());
		
		final String actual = (String) declaredMethod.invoke(null, file);

		// asserts
		assertEquals(expected, actual.toLowerCase());
	}

	@Nested
	@DisplayName("tests for FileUtil#isBinary")
	class IsBinary {

		@Test
		void check_against_binary_file() throws Exception {
			final File input = new File(FileUtilTest.class.getResource("data/file/a.png").toURI());

			final boolean actual = FileUtil.isBinary(input);
			
			// asserts
			assertEquals(true, actual);
		}

		@Test
		void check_against_text_file() throws Exception {
			final File input = new File(FileUtilTest.class.getResource("data/file/a.txt").toURI());

			final boolean actual = FileUtil.isBinary(input);

			// asserts
			assertEquals(false, actual);
		}

		@Test
		void check_against_created_text_file() throws Exception {
			final File input = FileUtil.createTempFile();
			FileUtil.writeString(input, "jodd makes fun!");

			final boolean actual = FileUtil.isBinary(input);

			// asserts
			assertEquals(false, actual);
		}

		@Test
		void check_against_created_binary_file() throws Exception {
			final File input = FileUtil.createTempFile();
			// first bytes of a zip / jar file
			FileUtil.writeBytes(input, new byte[] {0x50, 0x4b, 0x03, 0x04, 0x14, 0x20, 0x08, 0x08, 0x08, 0x20, 0x09,
					0x76, 0x19, 0x45, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 });

			final boolean actual = FileUtil.isBinary(input);

			// asserts
			assertEquals(true, actual);
		}

	}

	@Nested
	@DisplayName("tests for FileUtil#isExistingFile")
	class IsExistingFile {

		@Test
		void file_exists() throws Exception {
			final File input = new File(FileUtilTest.class.getResource("data/file/a.txt").toURI());

			final boolean actual = FileUtil.isExistingFile(input);

			// asserts
			assertEquals(true, actual);
		}

		@Test
		void file_not_exists() throws Exception {
			final File input = FileUtil.createTempFile("hello", ".jodd", new File(SystemUtil.info().getTempDir()), false);

			final boolean actual = FileUtil.isExistingFile(input);

			// asserts
			assertEquals(false, actual);
		}

		@Test
		void file_is_null() throws Exception {
			final boolean actual = FileUtil.isExistingFile(null);

			// asserts
			assertEquals(false, actual);
		}
	}

	@Nested
	@DisplayName("tests for FileUtil#isExistingFolder")
	class IsExistingFolder {

		@Test
		void folder_exists() throws Exception {
			final File input = new File(FileUtilTest.class.getResource("data/file/a.txt").toURI()).getParentFile();

			final boolean actual = FileUtil.isExistingFolder(input);

			// asserts
			assertEquals(true, actual);
		}

		@Test
		void folder_not_exists() throws Exception {
			final File input = new File(SystemUtil.info().getTempDir(), "/folder-does-not-exists");

			final boolean actual = FileUtil.isExistingFolder(input);

			// asserts
			assertEquals(false, actual);
		}

		@Test
		void folder_is_null() throws Exception {
			final boolean actual = FileUtil.isExistingFolder(null);

			// asserts
			assertEquals(false, actual);
		}
	}


}
