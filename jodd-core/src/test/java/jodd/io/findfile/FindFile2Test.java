// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class FindFile2Test {

	protected String dataRoot;

	@Before
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = FindFile2Test.class.getResource("data");
		dataRoot = data.getFile();
	}

	@Test
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
		ff.sortByName();
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

	@Test
	public void testTwoRoots() {

		FindFile ff =
			new WildcardFindFile()
				.setIncludeDirs(true)
				.setIncludeFiles(true)
				.setRecursive(true)
				.setWalking(true)
				.searchPath(dataRoot + "/beta")
				.searchPath(dataRoot + "/sumo")
				.include("**");

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

	@Test
	public void testTwoRootsAndWildcardMatchTypes() {

		WildcardFindFile wff = new WildcardFindFile();
		wff.setIncludeDirs(true);
		wff.setIncludeFiles(true);
		wff.setRecursive(true);
		wff.setWalking(true);
		wff.searchPath(dataRoot + "/beta");
		wff.searchPath(dataRoot + "/sumo");

		wff.setMatchType(WildcardFindFile.Match.FULL_PATH);
		wff.include("**/sumo/*");
		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);

		wff.reset();

		wff
			.setMatchType(WildcardFindFile.Match.FULL_PATH)
			.include("**/sumo/**");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(2, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.NAME);
		wff.include("*.txt");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(3, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.RELATIVE_PATH);
		wff.include("/gamma/*");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);

		wff.reset();
		wff.setMatchType(WildcardFindFile.Match.RELATIVE_PATH);
		wff.include("/*a*.txt");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);
	}

	@Test
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

	@Test
	public void testNotFound() {
		WildcardFindFile wff = new WildcardFindFile();
		wff.setIncludeDirs(true);
		wff.setIncludeFiles(true);
		wff.setRecursive(true);
		wff.setWalking(true);
		wff.searchPath(dataRoot + "/beta");
		wff.searchPath(dataRoot + "/sumo");

		wff.setMatchType(WildcardFindFile.Match.FULL_PATH);
		wff.include("**/xxxxxxx/*");

		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(0, count);
	}


	@Test
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

		ff.reset();
		ff.sortNone();
		ff.sortFoldersLast();
		ff.sortByExtension();
		ff.sortByName();
		str.setLength(0);

		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("alpha.txt | jodd1.txt | jodd10.txt | zero.txt | beta | sumo | ", str.toString());

	}

	@Test
	public void testJustFoldersAndFiles() {
		FindFile ff = new FindFile();
		ff.setIncludeDirs(false);
		ff.setIncludeFiles(true);
		ff.setRecursive(false);
		ff.sortByName();
		ff.searchPath(dataRoot);

		File f;
		StringBuilder str = new StringBuilder();
		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("alpha.txt | jodd1.txt | jodd10.txt | zero.txt | ", str.toString());

		ff = new FindFile();
		ff.setIncludeDirs(true);
		ff.setIncludeFiles(false);
		ff.setRecursive(false);
		ff.sortByName();
		ff.searchPath(dataRoot);

		str = new StringBuilder();
		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("beta | sumo | ", str.toString());

	}


}
