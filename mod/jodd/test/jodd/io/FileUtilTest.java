// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileUtilTest extends TestCase {

	protected String dataRoot;

	private static final String SB = "sb.data";
	private static final String SB_NEW = "sb1.data";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile();
	}

	public void testFileManipulation() {
		try {
			FileUtil.copy(new File(dataRoot, SB), new File(dataRoot, SB_NEW));
			assertFalse(FileUtil.isNewer(new File(dataRoot, SB), new File(dataRoot, SB_NEW)));
			assertFalse(FileUtil.isOlder(new File(dataRoot, SB), new File(dataRoot, SB_NEW)));
			FileUtil.delete(new File(dataRoot, SB_NEW));
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

		// test unicode chars (i.e. greaer then 255)
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
}
