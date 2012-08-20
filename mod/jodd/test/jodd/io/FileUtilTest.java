// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileUtilTest extends TestCase {

	protected String dataRoot;
	protected String utfdataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile();
		data = FileUtilTest.class.getResource("utf");
		utfdataRoot = data.getFile();
	}

	public void testFileManipulation() {
		try {
			FileUtil.copy(new File(dataRoot, "sb.data"), new File(dataRoot, "sb1.data"));
			assertFalse(FileUtil.isNewer(new File(dataRoot, "sb.data"), new File(dataRoot, "sb1.data")));
			assertFalse(FileUtil.isOlder(new File(dataRoot, "sb.data"), new File(dataRoot, "sb1.data")));
			FileUtil.delete(new File(dataRoot, "sb1.data"));
		} catch (Exception ex) {
			fail("FileUtil.copy " + ex.toString());
		}
	}

	public void testString() {
		String s = "This is a test file\nIt only has\nthree lines!!";

		try {
			FileUtil.writeString(new File(dataRoot, "test.txt"), s);
		} catch(Exception ex) {
			fail("FileUtil.writeString " + ex.toString());
		}

		String s2 = null;
		try {
			s2 = FileUtil.readString(dataRoot + "/test.txt");
		} catch(Exception ex) {
			fail("FileUtil.readString " + ex.toString());
		}
		assertEquals(s, s2);

		// test unicode chars (i.e. greater then 255)
		char buf[] = s.toCharArray();
		buf[0] = 256;
		s = new String(buf);

		try {
			FileUtil.writeString(dataRoot + "/test.txt", s);
		} catch(Exception ex) {
			fail("FileUtil.writeString " + ex.toString());
		}

		try {
			s2 = FileUtil.readString(dataRoot + "/test.txt");
		} catch(Exception ex) {
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

	public void testUnicodeString() {
		String s = "This is a test file\nIt only has\nthree lines!!";

		char buf[] = s.toCharArray();
		buf[0] = 256;
		s = new String(buf);

		try {
			FileUtil.writeString(dataRoot + "/test2.txt", s, "UTF-16");
		} catch(Exception ex) {
			fail("FileUtil.writeString " + ex.toString());
		}

		String s2 = null;
		try {
			s2 = FileUtil.readString(dataRoot + "/test2.txt", "UTF-16");
		} catch(Exception ex) {
			fail("FileUtil.readString " + ex.toString());
		}
		assertEquals(s, s2);

		try {
			FileUtil.delete(dataRoot + "/test2.txt");
		} catch (IOException ioex) {
			fail("FileUtil.delete" + ioex.toString());
		}

	}

	public void testFileManipulations() {
		String root = dataRoot + "/file/";
		String tmp = root + "tmp/";
		String tmp2 = root + "xxx/";
		String tmp3 = root + "zzz/";

		// copy
		try {
			FileUtil.copyFile(root + "a.txt", root + "w.txt");
			FileUtil.copyFile(root + "a.png", root + "w.png");
			FileUtil.copyFile(root + "a.txt", root + "w.txt");
		} catch(IOException ioex) {
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

		// don't overwrite
		try {
			FileUtil.copyFileToDir(root + "a.txt", tmp, FileUtil.params().overwrite(false));
			fail("copy file don't overwrite");
		} catch (IOException e) {
			// ignore
		}

		// move
		try {
			FileUtil.moveFile(root + "w.txt", tmp + "w.txt");
			FileUtil.moveFileToDir(root + "w.png", tmp);
		} catch (IOException ioex) {
			fail(ioex.toString());
		}

		try {
			FileUtil.moveFileToDir(root + "w.png", tmp, FileUtil.cloneParams().overwrite(false));
			fail("move file don't overwrite");
		} catch (IOException e) {
			// ignore
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

	public void testBytes() {
		try {
			byte[] bytes = FileUtil.readBytes(dataRoot + "/file/a.txt");

			assertEquals(11, bytes.length);
			assertEquals("test file\r\n", new String(bytes));

		} catch (IOException ioex) {
			fail(ioex.toString());
		}

	}

	public void testUTFReads() throws IOException {
		String content = FileUtil.readUTFString(new File(utfdataRoot, "utf-8.txt"));

		String content8 = FileUtil.readString(new File(utfdataRoot, "utf-8.txt"), "UTF-8");
		assertEquals(content, content8);

		String content1 = FileUtil.readUTFString(new File(utfdataRoot, "utf-16be.txt"));
		assertEquals(content, content1);

		String content16 = FileUtil.readString(new File(utfdataRoot, "utf-16be.txt"), "UTF-16BE");
		assertEquals(content, content16);

		String content162 = FileUtil.readString(new File(utfdataRoot, "utf-16be.txt"), "UTF-16");
		assertEquals(content, content162);

		String content2 = FileUtil.readUTFString(new File(utfdataRoot, "utf-16le.txt"));
		assertEquals(content, content2);

		String content163 = FileUtil.readString(new File(utfdataRoot, "utf-16le.txt"), "UTF-16LE");
		assertEquals(content, content163);
	}
}
