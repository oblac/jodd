// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import junit.framework.TestCase;

@SuppressWarnings( {"SimplifiableJUnitAssertion"})
public class FileNameUtilTest extends TestCase {

	public void testPrefixLength() {
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

	public void testNormalizeProblem() {
		assertEquals("//foo/bar", FileNameUtil.normalize("//foo/.///bar", true));
		assertEquals("/bar", FileNameUtil.normalize("/./bar", true));
		assertEquals("//foo//", FileNameUtil.normalize("//foo//", true));
		assertEquals("//foo//bar", FileNameUtil.normalize("//foo//./bar", true));
		assertEquals("/foo/bar", FileNameUtil.normalize("/foo//./bar", true));
	}

	public void testNormalize() {
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

	public void testNormalizeNoEndSeparator() {
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

	public void testConcat() {
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
}
