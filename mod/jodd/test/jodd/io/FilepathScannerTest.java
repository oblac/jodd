// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.io.findfile.FilepathScanner;
import jodd.mutable.MutableInteger;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

public class FilepathScannerTest extends TestCase {

	protected String dataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile().substring(1);
	}

	public void testFileScanner() {
		final MutableInteger count = new MutableInteger(0);
		FilepathScanner fs = new FilepathScanner() {
			@Override
			protected void onFile(File file) {
				count.value++;
				String path = file.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				boolean matched =
						path.equals(dataRoot + "/sb.data") ||
						path.equals(dataRoot + "/file/a.png") ||
						path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);
			}
		}.includeDirs(false).recursive(true).includeFiles(true);
		fs.scan(dataRoot);

		assertEquals(3, count.value);
	}

	public void testFolderScanner() {
		final MutableInteger count = new MutableInteger(0);
		FilepathScanner fs = new FilepathScanner() {
			@Override
			protected void onFile(File file) {
				count.value++;
				String path = file.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				boolean matched =
						path.equals(dataRoot + "/file");

				assertTrue(matched);
			}
		}.includeDirs(true).recursive(true).includeFiles(false);
		fs.scan(dataRoot);

		assertEquals(1, count.value);
	}

}
