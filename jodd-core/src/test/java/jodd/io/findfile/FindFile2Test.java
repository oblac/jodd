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

package jodd.io.findfile;

import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class FindFile2Test {

	protected String dataRoot;

	@BeforeEach
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
		ff.includeDirs(true);
		ff.includeFiles(true);
		ff.recursive(true);
		ff.sortByName();
		ff.walking(true);
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
		ff.walking(false);

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
				.includeDirs(true)
				.includeFiles(true)
				.recursive(true)
				.walking(true)
				.searchPath(dataRoot + "/beta")
				.searchPath(dataRoot + "/sumo")
				.include("**");

		int count = 0;
		while (ff.nextFile() != null) {
			count++;
		}

		assertEquals(5, count);

		ff.reset();
		ff.includeDirs(false);

		count = 0;
		while (ff.nextFile() != null) {
			count++;
		}

		assertEquals(3, count);

	}

	@Test
	public void testTwoRootsAndWildcardMatchTypes() {

		WildcardFindFile wff = new WildcardFindFile();
		wff.includeDirs(true);
		wff.includeFiles(true);
		wff.recursive(true);
		wff.walking(true);
		wff.searchPath(dataRoot + "/beta");
		wff.searchPath(dataRoot + "/sumo");

		wff.matchType(WildcardFindFile.Match.FULL_PATH);
		wff.include("**/sumo/*");
		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);

		wff.reset();

		wff
			.matchType(WildcardFindFile.Match.FULL_PATH)
			.include("**/sumo/**");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(2, count);

		wff.reset();
		wff.matchType(WildcardFindFile.Match.NAME);
		wff.include("*.txt");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(3, count);

		wff.reset();
		wff.matchType(WildcardFindFile.Match.RELATIVE_PATH);
		wff.include("/gamma/*");
		count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(1, count);

		wff.reset();
		wff.matchType(WildcardFindFile.Match.RELATIVE_PATH);
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

		wff.includeDirs(true);
		wff.includeFiles(true);
		wff.recursive(true);
		wff.walking(true);
		wff.searchPath(dataRoot + "/void");

		wff.matchType(WildcardFindFile.Match.FULL_PATH);
		int count = 0;
		while (wff.nextFile() != null) {
			count++;
		}
		assertEquals(0, count);
	}

	@Test
	public void testNotFound() {
		WildcardFindFile wff = new WildcardFindFile();
		wff.includeDirs(true);
		wff.includeFiles(true);
		wff.recursive(true);
		wff.walking(true);
		wff.searchPath(dataRoot + "/beta");
		wff.searchPath(dataRoot + "/sumo");

		wff.matchType(WildcardFindFile.Match.FULL_PATH);
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
		ff.includeDirs(true);
		ff.includeFiles(true);
		ff.recursive(false);
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
		ff.includeDirs(false);
		ff.includeFiles(true);
		ff.recursive(false);
		ff.sortByName();
		ff.searchPath(dataRoot);

		File f;
		StringBuilder str = new StringBuilder();
		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("alpha.txt | jodd1.txt | jodd10.txt | zero.txt | ", str.toString());

		ff = new FindFile();
		ff.includeDirs(true);
		ff.includeFiles(false);
		ff.recursive(false);
		ff.sortByName();
		ff.searchPath(dataRoot);

		str = new StringBuilder();
		while ((f = ff.nextFile()) != null) {
			str.append(f.getName()).append(" | ");
		}

		assertEquals("beta | sumo | ", str.toString());
	}


}
