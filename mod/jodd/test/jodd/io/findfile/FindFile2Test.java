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

	public void testTwoRoots() {

		FindFile ff = new WildcardFindFile("**");
		ff.setIncludeDirs(true);
		ff.setIncludeFiles(true);
		ff.setRecursive(true);
		ff.setWalking(true);
		ff.searchPath(dataRoot + "/beta");
		ff.searchPath(dataRoot + "/sumo");

		int count = 0;
		while (ff.nextFile() != null) {
			count++;
		}

		assertEquals(5, count);

		ff.reset();
		ff.setIncludeDirs(false);

		count = 0;
		while (ff.nextFile() != null) {
			count++;
		}

		assertEquals(3, count);

	}

	public void testTwoRootsAndWildcardMatchTypes() {

		WildcardFindFile wff = new WildcardFindFile();
		wff.setIncludeDirs(true);
		wff.setIncludeFiles(true);
		wff.setRecursive(true);
		wff.setWalking(true);
		wff.searchPath(dataRoot + "/beta");
		wff.searchPath(dataRoot + "/sumo");

		wff.setMatchType(WildcardFindFile.Match.FULL_PATH);
		wff.setPattern("**/sumo/*");
		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.FULL_PATH);
		wff.setPattern("**/sumo/**");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(2, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.NAME);
		wff.setPattern("*.txt");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(3, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.RELATIVE_PATH);
		wff.setPattern("/gamma/*");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.RELATIVE_PATH);
		wff.setPattern("/*a*.txt");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);
	}

	public void testNonExisting() {
		FindFile wff = new FindFile();

		wff.setIncludeDirs(true);
		wff.setIncludeFiles(true);
		wff.setRecursive(true);
		wff.setWalking(true);
		wff.searchPath(dataRoot + "/void");

		wff.setMatchType(WildcardFindFile.Match.FULL_PATH);
		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(0, count);
	}

	public void testNotFound() {
		WildcardFindFile wff = new WildcardFindFile();
		wff.setIncludeDirs(true);
		wff.setIncludeFiles(true);
		wff.setRecursive(true);
		wff.setWalking(true);
		wff.searchPath(dataRoot + "/beta");
		wff.searchPath(dataRoot + "/sumo");

		wff.setMatchType(WildcardFindFile.Match.FULL_PATH);
		wff.setPattern("**/xxxxxxx/*");
		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(0, count);

	}


	public void testSort() {
		final StringBuilder str = new StringBuilder();

		FindFile ff = new FindFile();
		ff.setIncludeDirs(true);
		ff.setIncludeFiles(true);
		ff.setRecursive(false);
		ff.sortByName();
		ff.searchPath(dataRoot);

		File f;
		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("alpha.txt | beta | jodd1.txt | jodd10.txt | sumo | zero.txt | ", str.toString());

		ff.reset();
		ff.sortNone();
		ff.sortByNameDesc();
		str.setLength(0);

		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("zero.txt | sumo | jodd10.txt | jodd1.txt | beta | alpha.txt | ", str.toString());

		ff.reset();
		ff.sortNone();
		ff.sortFoldersFirst();
		ff.sortByName();
		str.setLength(0);

		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("beta | sumo | alpha.txt | jodd1.txt | jodd10.txt | zero.txt | ", str.toString());

		ff.reset();
		ff.sortNone();
		ff.sortFoldersLast();
		ff.sortByName();
		str.setLength(0);

		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("alpha.txt | jodd1.txt | jodd10.txt | zero.txt | beta | sumo | ", str.toString());

	}


}
