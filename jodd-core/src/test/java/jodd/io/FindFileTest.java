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

import jodd.io.findfile.FindFile;
import jodd.io.findfile.RegExpFindFile;
import jodd.io.findfile.WildcardFindFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FindFileTest {

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
	public void testTwoAccept() {
		FindFile ff = new WildcardFindFile()
						.include("**/*file/a.png")
						.include("**/*file/a.txt")
						.setRecursive(true)
						.setIncludeDirs(true)
						.searchPath(dataRoot);

		int countFiles = 0;
		int countDirs = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				if (path.startsWith("/") == false) {
					path = '/' + path;
				}
				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
								path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);
	}

	@Test
	public void testWildcardFile() {
		FindFile ff = new WildcardFindFile()
				.include("**/*file/a*")
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath(dataRoot);

		int countDirs = 0;
		int countFiles = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				if (path.startsWith("/") == false) {
					path = '/' + path;
				}
				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
								path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);

		ff.searchPath(dataRoot);

		countDirs = 0;
		countFiles = 0;

		Iterator<File> iterator = ff.iterator();

		while (iterator.hasNext()) {
			f = iterator.next();

			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				if (path.startsWith("/") == false) {
					path = '/' + path;
				}

				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
								path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);
			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);

	}


	@Test
	public void testWildcardPath() {
		FindFile ff = new WildcardFindFile()
				.include("**/file/*")
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath(dataRoot);

		int countDirs = 0;
		int countFiles = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				if (path.startsWith("/") == false) {
					path = '/' + path;
				}

				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
								path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);
	}

	@Test
	public void testRegexp() {
		FindFile ff = new RegExpFindFile()
				.include(".*/a[.].*")
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath(dataRoot);

		int countDirs = 0;
		int countFiles = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				if (path.startsWith("/") == false) {
					path = '/' + path;
				}

				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
								path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);
	}

}
