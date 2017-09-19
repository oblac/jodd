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

package jodd.madvoc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MadvocUtilTest extends MadvocTestCase {

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testSplitActionPath() {
		assertTrue(Arrays.equals(new String[]{"qqq"}, MadvocUtil.splitActionPath("/qqq")));
		assertTrue(Arrays.equals(new String[]{"qqq", "www"}, MadvocUtil.splitActionPath("/qqq/www")));
		assertTrue(Arrays.equals(new String[]{"qqq", "www", "eee"}, MadvocUtil.splitActionPath("/qqq/www/eee")));
	}

	@Test
	public void testActionName() {
		assertEquals("foo", MadvocUtil.stripLastCamelWord("fooAction"));
		assertEquals("foo", MadvocUtil.stripLastCamelWord("foo"));
		assertEquals("fooBoo", MadvocUtil.stripLastCamelWord("fooBooAction"));
		assertEquals("fooBoo", MadvocUtil.stripLastCamelWord("fooBooZoooo"));
	}

}
