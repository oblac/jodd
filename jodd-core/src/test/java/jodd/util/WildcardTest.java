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

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WildcardTest {

	@Test
	public void testMatch() {
		assertTrue(Wildcard.match("CfgOptions.class", "C*class"));
		assertFalse(Wildcard.match("CfgOptions.class", "C*clas"));
		assertFalse(Wildcard.match("C*r*class", "CfgOptions.class"));

		// wildcard on the start and end of the expression
		assertTrue(Wildcard.match("CfgOptions.class", "*g*class"));
		assertTrue(Wildcard.match("CfgOptions.class", "*C*g*class"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*g*cl*"));
		assertTrue(Wildcard.match("CfgOptions.class", "*C*g*cl*"));

		// multiple wildcards
		assertTrue(Wildcard.match("CfgOptions.class", "*g*c**ss"));
		assertTrue(Wildcard.match("CfgOptions.class", "*g*c**s"));    // THIS IS TRUE!!!
		assertTrue(Wildcard.match("CfgOptions.class", "*gOpti******ons.c**ss"));
		assertTrue(Wildcard.match("CfgOptions.class", "***gOpti*ons.c**ss"));
		assertTrue(Wildcard.match("CfgOptions.class", "***gOptions.c**"));

		// wildcard '?':
		assertTrue(Wildcard.match("CfgOptions.class", "*gOpti*c?ass"));
		assertFalse(Wildcard.match("CfgOptions.class", "*gOpti*c?ss"));

		// wildcard on the start and end of the expression:
		assertTrue(Wildcard.match("CfgOptions.class", "*gOpti*c?as?"));
		assertFalse(Wildcard.match("CfgOptions.class", "*gOpti*c?a?"));
		assertTrue(Wildcard.match("CfgOptions.class", "?fgOpti*c?ass"));
		assertFalse(Wildcard.match("CfgOptions.class", "?gOpti*c?as?"));
		assertTrue(Wildcard.match("CfgOptions.class", "??gOpti*c?ass"));

		// multiple wildcards
		assertTrue(Wildcard.match("CfgOptions.class", "C????ti*c?ass"));
		assertFalse(Wildcard.match("CfgOptions.class", "C???ti*c?ass"));

		// wildcards '*' and '?' mixed together:
		assertTrue(Wildcard.match("CfgOptions.class", "C??*ti*c?ass"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*c?as?*"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*c?a*?"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*?a*"));
		assertTrue(Wildcard.match("CfgOptions.class", "C*ti*c?a?*"));
		assertFalse(Wildcard.match("CfgOptions.class", "C*ti*c?*la?*"));
	}

	/**
	 * Only match() has escaped wildcards; matchPath() does not have them
	 * as escape character is equal to one of the path characters..
	 */
	@Test
	public void testMatchEscapedWildcards() {
		assertFalse(Wildcard.match("CfgOptions.class", "*gOpti\\*c?ass"));
		assertTrue(Wildcard.match("CfgOpti*class", "*gOpti\\*class"));
		assertTrue(Wildcard.match("CfgOpti*class", "*gOpti\\*c?ass"));
		assertFalse(Wildcard.match("CfgOpti*class", "*gOpti\\\\*c?ass"));
		assertTrue(Wildcard.match("CfgOpti\\*class", "*gOpti\\\\*c?ass"));
		assertTrue(Wildcard.match("CfgOpti?class", "*gOpti\\?c*ass"));
		assertFalse(Wildcard.match("CfgOpti\\?class", "*gOpti\\?c*ass"));
		assertTrue(Wildcard.match("CfgOptions.class", "CfgOpti\\ons.class"));
		assertTrue(Wildcard.match("What's this?", "What*\\?"));
	}

	@Test
	public void testMatchPath1() {
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C*class"));
		assertFalse(Wildcard.matchPath("CfgOptions.class", "C*clas"));
		assertFalse(Wildcard.matchPath("C*r*class", "CfgOptions.class"));

		// wildcard on the start and end of the expression
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*g*class"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*C*g*class"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C*g*cl*"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*C*g*cl*"));

		// multiple wildcards
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*g*c**ss"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*g*c**s"));    // THIS IS TRUE!!!
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*gOpti******ons.c**ss"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "***gOpti*ons.c**ss"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "***gOptions.c**"));

		// wildcard '?':
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*gOpti*c?ass"));
		assertFalse(Wildcard.matchPath("CfgOptions.class", "*gOpti*c?ss"));

		// wildcard on the start and end of the expression:
		assertTrue(Wildcard.matchPath("CfgOptions.class", "*gOpti*c?as?"));
		assertFalse(Wildcard.matchPath("CfgOptions.class", "*gOpti*c?a?"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "?fgOpti*c?ass"));
		assertFalse(Wildcard.matchPath("CfgOptions.class", "?gOpti*c?as?"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "??gOpti*c?ass"));

		// multiple wildcards
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C????ti*c?ass"));
		assertFalse(Wildcard.matchPath("CfgOptions.class", "C???ti*c?ass"));

		// wildcards '*' and '?' mixed together:
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C??*ti*c?ass"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C*ti*c?as?*"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C*ti*c?a*?"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C*ti*?a*"));
		assertTrue(Wildcard.matchPath("CfgOptions.class", "C*ti*c?a?*"));
		assertFalse(Wildcard.matchPath("CfgOptions.class", "C*ti*c?*la?*"));
	}

	@Test
	public void testMatchWildcard() {
		assertTrue(Wildcard.match("app.nfo", "app*"));
		assertFalse(Wildcard.match("\\app.nfo", "app*"));
		assertTrue(Wildcard.match("\\app.nfo", "\\\\app*"));
	}

	@Test
	public void testMatchPath2() {
		assertTrue(Wildcard.matchPath("/foo", "/fo*"));
		assertTrue(Wildcard.matchPath("/foo", "/**"));
		assertTrue(Wildcard.matchPath("/foo", "**"));

		assertFalse(Wildcard.matchPath("/foo", "**/"));
		assertFalse(Wildcard.matchPath("/foo", "/**/"));
		assertTrue(Wildcard.matchPath("/foo/", "/**/"));

		assertTrue(Wildcard.matchPath("/foo/boo", "/**/bo*"));
		assertTrue(Wildcard.matchPath("/foo/soo/doo/boo", "/**/bo*"));
		assertTrue(Wildcard.matchPath("/foo/boo", "/**/**/bo*"));
		assertTrue(Wildcard.matchPath("/foo/one/two/three/boo", "/**/**/bo*"));
		assertTrue(Wildcard.matchPath("/foo/one/two/three/boo", "/**/**/**/bo*"));

		assertTrue(Wildcard.matchPath("/foo/one/two/three/boo", "/**/one/**"));
		assertTrue(Wildcard.matchPath("/foo/one/two/three/boo", "/**/two/**"));
		assertTrue(Wildcard.matchPath("/foo/one/two/three/boo", "**/two/**"));
		assertTrue(Wildcard.matchPath("/foo/one/two/three/boo", "**/t?o/**"));

		assertTrue(Wildcard.matchPath("sys/java/bin", "sys/**/bin"));
		assertTrue(Wildcard.matchPath("sys/java/bin", "?ys/**/bin"));
		assertTrue(Wildcard.matchPath("c:\\Users\\najgor", "?:\\**\\najgor"));
		assertTrue(Wildcard.matchPath("c:\\najgor", "?:\\**\\naj**r"));
	}

	@Test
	public void testDifferences() {
		assertTrue(Wildcard.match("/uphea", "*/uphea*"));
		assertTrue(Wildcard.match("/prj/uphea-app.jar", "*/uphea*"));

		assertTrue(Wildcard.matchPath("/uphea", "*/uphea*"));
		assertTrue(Wildcard.matchPath("prj/uphea", "*/uphea*"));
		assertFalse(Wildcard.matchPath("/prj/uphea-app.jar", "*/uphea*"));
		assertTrue(Wildcard.matchPath("/prj/uphea-app.jar", "**/uphea*"));

		assertTrue(Wildcard.match("/some/path/lib-jodd-v1", "*jodd*"));
		assertTrue(Wildcard.matchPath("/some/path/lib-jodd-v1", "**/*jodd*"));

		assertTrue(Wildcard.match("/some/path/lib-jodd-v1", "*/path/lib-jodd*"));
		assertTrue(Wildcard.matchPath("/some/path/lib-jodd-v1", "**/path/lib-jodd*"));

		assertTrue(Wildcard.match("/some/path/lib-jodd-v1", "*/lib-jodd*"));
		assertTrue(Wildcard.matchPath("/some/path/lib-jodd-v1", "**/lib-jodd*"));
	}

	@Test
	public void testMore() {
		assertTrue(Wildcard.matchPath("/a/b/c/d", "**/b/**"));
		assertTrue(Wildcard.matchPath("/a/b/c", "**/b/**"));
		assertTrue(Wildcard.matchPath("/a/b/c/d", "**/c/**"));
	}
}
