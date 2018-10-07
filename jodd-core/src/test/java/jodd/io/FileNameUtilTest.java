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

import jodd.system.SystemInfo;
import jodd.system.SystemUtil;
import jodd.util.RandomString;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"SimplifiableJUnitAssertion"})
class FileNameUtilTest {

	@Test
	void testPrefixLength() {
		assertEquals(0, FileNameUtil.getPrefixLength("a\\b\\c.txt"));
		assertEquals(1, FileNameUtil.getPrefixLength("\\a\\b\\c.txt"));
		assertEquals(2, FileNameUtil.getPrefixLength("C:a\\b\\c.txt"));
		assertEquals(3, FileNameUtil.getPrefixLength("C:\\a\\b\\c.txt"));
		assertEquals(9, FileNameUtil.getPrefixLength("\\\\server\\a\\b\\c.txt"));

		assertEquals(0, FileNameUtil.getPrefixLength("a/b/c.txt"));
		assertEquals(1, FileNameUtil.getPrefixLength("/a/b/c.txt"));
		assertEquals(2, FileNameUtil.getPrefixLength("~/a/b/c.txt"));
		assertEquals(2, FileNameUtil.getPrefixLength("~"));
		assertEquals(6, FileNameUtil.getPrefixLength("~user/a/b/c.txt"));
		assertEquals(6, FileNameUtil.getPrefixLength("~user"));

		assertEquals(9, FileNameUtil.getPrefixLength("//server/a/b/c.txt"));
		assertEquals(9, FileNameUtil.getPrefixLength("//server//a/b/c.txt"));
		assertEquals(9, FileNameUtil.getPrefixLength("//server//./bar"));
	}

	@Test
	void testNormalizeProblem() {
		assertEquals("//foo/bar", FileNameUtil.normalize("//foo/.///bar", true));
		assertEquals("/bar", FileNameUtil.normalize("/./bar", true));
		assertEquals("//foo//", FileNameUtil.normalize("//foo//", true));
		assertEquals("//foo//bar", FileNameUtil.normalize("//foo//./bar", true));
		assertEquals("/foo/bar", FileNameUtil.normalize("/foo//./bar", true));
	}

	@Test
	void testNormalize() {
		assertEquals("/foo/", FileNameUtil.normalize("/foo//", true));
		assertEquals("/foo/", FileNameUtil.normalize("/foo/./", true));
		assertEquals("/bar", FileNameUtil.normalize("/foo/../bar", true));
		assertEquals("/bar/", FileNameUtil.normalize("/foo/../bar/", true));
		assertEquals("/baz", FileNameUtil.normalize("/foo/../bar/../baz", true));
//		assertEquals("/foo/bar", FileNameUtil.normalize("//foo//./bar", true));
		assertEquals("/foo/bar", FileNameUtil.normalize("/foo//./bar", true));
		assertEquals(null, FileNameUtil.normalize("/../", true));
		assertEquals(null, FileNameUtil.normalize("../foo", true));
		assertEquals("foo/", FileNameUtil.normalize("foo/bar/..", true));
		assertEquals(null, FileNameUtil.normalize("foo/../../bar", true));
		assertEquals("bar", FileNameUtil.normalize("foo/../bar", true));
		assertEquals("//server/bar", FileNameUtil.normalize("//server/foo/../bar", true));
		assertEquals(null, FileNameUtil.normalize("//server/../bar", true));
		assertEquals("C:\\bar", FileNameUtil.normalize("C:\\foo\\..\\bar", false));
		assertEquals(null, FileNameUtil.normalize("C:\\..\\bar", false));
		assertEquals("~/bar/", FileNameUtil.normalize("~/foo/../bar/", true));
		assertEquals(null, FileNameUtil.normalize("~/../bar", true));

	}

	@Test
	void testNormalizeNoEndSeparator() {
		assertEquals("/foo", FileNameUtil.normalizeNoEndSeparator("/foo//", true));
		assertEquals("/foo", FileNameUtil.normalizeNoEndSeparator("/foo/./", true));
		assertEquals("/bar", FileNameUtil.normalizeNoEndSeparator("/foo/../bar", true));
		assertEquals("/bar", FileNameUtil.normalizeNoEndSeparator("/foo/../bar/", true));
		assertEquals("/baz", FileNameUtil.normalizeNoEndSeparator("/foo/../bar/../baz", true));
//		assertEquals("/foo/bar", FileNameUtil.normalizeNoEndSeparator("//foo//./bar", true));
		assertEquals("/foo/bar", FileNameUtil.normalizeNoEndSeparator("/foo//./bar", true));
		assertEquals(null, FileNameUtil.normalizeNoEndSeparator("/../", true));
		assertEquals(null, FileNameUtil.normalizeNoEndSeparator("../foo", true));
		assertEquals("foo", FileNameUtil.normalizeNoEndSeparator("foo/bar/..", true));
		assertEquals(null, FileNameUtil.normalizeNoEndSeparator("foo/../../bar", true));
		assertEquals("bar", FileNameUtil.normalizeNoEndSeparator("foo/../bar", true));
		assertEquals("//server/bar", FileNameUtil.normalizeNoEndSeparator("//server/foo/../bar", true));
		assertEquals(null, FileNameUtil.normalizeNoEndSeparator("//server/../bar", true));
		assertEquals("C:\\bar", FileNameUtil.normalizeNoEndSeparator("C:\\foo\\..\\bar", false));
		assertEquals(null, FileNameUtil.normalizeNoEndSeparator("C:\\..\\bar", false));
		assertEquals("~/bar", FileNameUtil.normalizeNoEndSeparator("~/foo/../bar/", true));
		assertEquals(null, FileNameUtil.normalizeNoEndSeparator("~/../bar", true));
	}

	@Test
	void testConcat() {
		assertEquals("/foo/bar", FileNameUtil.concat("/foo/", "bar", true));
		assertEquals("\\foo\\bar", FileNameUtil.concat("/foo/", "bar", false));
		assertEquals("/foo/bar", FileNameUtil.concat("/foo", "bar", true));
		assertEquals("/bar", FileNameUtil.concat("/foo", "/bar", true));
		assertEquals("C:/bar", FileNameUtil.concat("/foo", "C:/bar", true));
		assertEquals("C:bar", FileNameUtil.concat("/foo", "C:bar", true));
		assertEquals("/foo/bar", FileNameUtil.concat("/foo/a", "../bar", true));
		assertEquals(null, FileNameUtil.concat("/foo/", "../../bar", true));
		assertEquals("/bar", FileNameUtil.concat("/foo/", "/bar", true));
		assertEquals("/bar", FileNameUtil.concat("/foo/..", "/bar", true));
		assertEquals("/foo/bar/c.txt", FileNameUtil.concat("/foo", "bar/c.txt", true));
		assertEquals("/foo/c.txt/bar", FileNameUtil.concat("/foo/c.txt", "bar", true));
	}

	@Test
	void testGetPathNoEndSeparator() {
		assertEquals("", FileNameUtil.getPathNoEndSeparator("/hello.world.html"));
		assertEquals("foo", FileNameUtil.getPathNoEndSeparator("/foo/hello.world.html"));
		assertEquals("foo/bar", FileNameUtil.getPathNoEndSeparator("/foo/bar/hello.world.html"));
	}

	@Test
	void testExtension() {
		assertEquals("foo", FileNameUtil.getExtension("/a/b/c.foo"));
		assertEquals("doo", FileNameUtil.getExtension("/a/b/c.foo.doo"));
		assertEquals("", FileNameUtil.getExtension("/a/b/c"));

		assertTrue(FileNameUtil.hasExtension("/a/b/c.foo"));
		assertTrue(FileNameUtil.hasExtension("/a/b/c.foo.doo"));
		assertFalse(FileNameUtil.hasExtension("/a/b/c"));
	}

	@Test
	void testResolveHome() {
		assertEquals("qwe", FileNameUtil.resolveHome("qwe"));
		assertEquals("", FileNameUtil.resolveHome(""));
		assertEquals(SystemUtil.info().getHomeDir(), FileNameUtil.resolveHome("~"));
		assertEquals(fixpath(SystemUtil.info().getHomeDir() + "/"), FileNameUtil.resolveHome(fixpath("~/")));
		assertEquals(fixpath(SystemUtil.info().getHomeDir() + "/foo"), FileNameUtil.resolveHome(fixpath("~/foo")));
	}

	@Test
	void testGetRelativePaths() {
		assertEquals(fixpath("../../b/c"), FileNameUtil.relativePath("/a/b/c", "/a/x/y/"));
		assertEquals(fixpath("../../b/c"), FileNameUtil.relativePath("/m/n/o/a/b/c", "/m/n/o/a/x/y/"));
		assertEquals(fixpath("stuff/xyz.dat"), FileNameUtil.relativePath("/var/data/stuff/xyz.dat", "/var/data/"));
		assertEquals(fixpath("../../../a/b/c"), FileNameUtil.relativePath("/a/b/c", "/m/n/o"));
	}

	@Test
	void testSplit() {
		final String dir = new SystemInfo().getTempDir();
		final String extension = ".tmp";
		final String filename = "jodd_" + RandomString.get().random(8, "abcdefkhutidnmpo1234567890") + extension;

		final String[] actual = FileNameUtil.split(new File(dir, filename).getAbsolutePath());

		// asserts
		assertNotNull(actual);
		assertEquals(4, actual.length);
		int indexOf = dir.indexOf(File.separator) + 1;
		assertEquals(dir.substring(0, indexOf), actual[0]);
		assertEquals(dir.substring(indexOf), actual[1]);
		assertEquals(filename.substring(0, filename.length() - extension.length()), actual[2]);
		assertEquals("tmp", actual[3]);

	}

	@ParameterizedTest
	@MethodSource("createTestData_testEqualsOnSystem")
	void testEqualsOnSystem(final boolean expected, final String filename1, final String filename2) {
		assertEquals(expected, FileNameUtil.equalsOnSystem(filename1, filename2));
	}

	private static Stream<Arguments> createTestData_testEqualsOnSystem() {
		return Stream.of(
				Arguments.of(true, "jodd_makes_fun.git", "jodd_MAKES_fUn.GiT"),
				Arguments.of(false, "jodd.tmp", "j0dd.tmp"),
				Arguments.of(true, null, null),
				Arguments.of(false, "jodd.tmp", null)
		);
	}

	private static String fixpath(String path) {
		return StringUtil.replace(path, "/", File.separator);
	}
}
