// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import java.util.Arrays;

public class MadvocUtilTest extends MadvocTestCase {


	public void testLasNdx2() {

		String s = "/xxx.zzzz.wwww";
		int i = MadvocUtil.lastIndexOfSlashDot(s);
		assertEquals(9, i);
		s = s.substring(0, i);
		assertEquals("/xxx.zzzz", s);

		i = MadvocUtil.lastIndexOfSlashDot(s);
		assertEquals(4, i);
		s = s.substring(0, i);
		assertEquals("/xxx", s);

		i = MadvocUtil.lastIndexOfSlashDot(s);
		assertEquals(1, i);
		s = s.substring(0, i);
		assertEquals("/", s);

		i = MadvocUtil.lastIndexOfSlashDot(s);
		assertEquals(-1, i);

		assertEquals(-1, MadvocUtil.lastIndexOfSlashDot(""));
		assertEquals(-1, MadvocUtil.lastIndexOfSlashDot("xxx"));
		assertEquals(0, MadvocUtil.lastIndexOfSlashDot(".xxx"));
		assertEquals(3, MadvocUtil.lastIndexOfSlashDot("xxx."));

		assertEquals(1, MadvocUtil.lastIndexOfSlashDot("/xxx"));
		assertEquals(1, MadvocUtil.lastIndexOfSlashDot("/.xxx"));
		assertEquals(4, MadvocUtil.lastIndexOfSlashDot("/xxx."));

		assertEquals(-1, MadvocUtil.lastIndexOfSlashDot("/xxx/"));
		assertEquals(-1, MadvocUtil.lastIndexOfSlashDot("/.xxx/"));
		assertEquals(-1, MadvocUtil.lastIndexOfSlashDot("/xxx./"));

		assertEquals(5, MadvocUtil.lastIndexOfSlashDot("/xxx/xxx"));
		assertEquals(6, MadvocUtil.lastIndexOfSlashDot("/.xxx/.xxx"));
		assertEquals(9, MadvocUtil.lastIndexOfSlashDot("/xxx./xxx."));
		assertEquals(6, MadvocUtil.lastIndexOfSlashDot("/xxx./xxx"));
	}


	public void testLasNdx() {
		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash(""));
		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash("xxx"));
		assertEquals(0, MadvocUtil.lastIndexOfDotAfterSlash(".xxx"));
		assertEquals(3, MadvocUtil.lastIndexOfDotAfterSlash("xxx."));

		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash("/xxx"));
		assertEquals(1, MadvocUtil.lastIndexOfDotAfterSlash("/.xxx"));
		assertEquals(4, MadvocUtil.lastIndexOfDotAfterSlash("/xxx."));

		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash("/xxx/"));
		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash("/.xxx/"));
		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash("/xxx./"));

		assertEquals(-1, MadvocUtil.lastIndexOfDotAfterSlash("/xxx/xxx"));
		assertEquals(6, MadvocUtil.lastIndexOfDotAfterSlash("/.xxx/.xxx"));
		assertEquals(9, MadvocUtil.lastIndexOfDotAfterSlash("/xxx./xxx."));
	}

	public void testFirstNdx() {
		assertEquals(-1, MadvocUtil.indexOfDotAfterSlash("xxx"));
		assertEquals(0, MadvocUtil.indexOfDotAfterSlash(".xxx"));
		assertEquals(3, MadvocUtil.indexOfDotAfterSlash("xxx."));

		assertEquals(-1, MadvocUtil.indexOfDotAfterSlash("/xxx"));
		assertEquals(1, MadvocUtil.indexOfDotAfterSlash("/.xxx"));
		assertEquals(4, MadvocUtil.indexOfDotAfterSlash("/xxx."));

		assertEquals(-1, MadvocUtil.indexOfDotAfterSlash("/xxx/"));
		assertEquals(-1, MadvocUtil.indexOfDotAfterSlash("/.xxx/"));
		assertEquals(-1, MadvocUtil.indexOfDotAfterSlash("/xxx./"));

		assertEquals(-1, MadvocUtil.indexOfDotAfterSlash("/xxx/xxx"));
		assertEquals(6, MadvocUtil.indexOfDotAfterSlash("/.xxx/.xxx"));
		assertEquals(9, MadvocUtil.indexOfDotAfterSlash("/xxx./xxx."));

	}

	public void testSplitActionPath() {
		assertTrue(Arrays.equals(new String[] {"qqq"}, MadvocUtil.splitActionPath("/qqq")));
		assertTrue(Arrays.equals(new String[] {"qqq", "www"}, MadvocUtil.splitActionPath("/qqq/www")));
		assertTrue(Arrays.equals(new String[] {"qqq", "www", "eee"}, MadvocUtil.splitActionPath("/qqq/www/eee")));
	}

	public void testActionName() {
		assertEquals("foo", MadvocUtil.stripLastCamelWord("fooAction"));
		assertEquals("foo", MadvocUtil.stripLastCamelWord("foo"));
		assertEquals("fooBoo", MadvocUtil.stripLastCamelWord("fooBooAction"));
		assertEquals("fooBoo", MadvocUtil.stripLastCamelWord("fooBooZoooo"));
	}

}
