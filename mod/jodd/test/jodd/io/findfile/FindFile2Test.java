// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

public class FindFile2Test extends TestCase {

	protected String dataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dataRoot != null) {
			return;
		}
		URL data = FindFile2Test.class.getResource("data");
		dataRoot = data.getFile().substring(1);
	}

	public void testAcceptAndWalk() {
		final StringBuilder s1 = new StringBuilder();
		final StringBuilder s2 = new StringBuilder();

		FindFile ff = new FindFile() {
			@Override
			protected boolean acceptFile(File file) {
				s1.append(file.getName());
				s1.append(" | ");
				return true;
			}
		};
		ff.setIncludeDirs(true);
		ff.setIncludeFiles(true);
		ff.setRecursive(true);
		ff.setWalking(true);
		ff.searchPath(dataRoot);

		assertNull(ff.lastFile());

		File f;
		while ((f = ff.nextFile()) != null) {
			s2.append(f.getName());
			s2.append(" | ");
		}

		assertNotNull(ff.lastFile());

		assertEquals(s1.toString(), s2.toString());
		assertEquals(11, StringUtil.count(s1.toString(), '|'));
		assertTrue(s2.indexOf("| sumo | theta |") != -1);

		s1.setLength(0);
		s2.setLength(0);

		ff.reset();
		ff.setWalking(false);

		while ((f = ff.nextFile()) != null) {
			s2.append(f.getName());
			s2.append(" | ");
		}

		assertEquals(s1.toString(), s2.toString());
		assertEquals(11, StringUtil.count(s1.toString(), '|'));
		assertTrue(s2.indexOf("| sumo | gamma |") != -1);
	}
}
