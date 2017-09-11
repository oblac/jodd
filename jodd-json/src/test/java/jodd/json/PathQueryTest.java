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

package jodd.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathQueryTest {

	@Test
	public void testPathMatching() {
		assertTrue(new PathQuery("hello", true).matches(Path.parse("hello")));
		assertFalse(new PathQuery("hello", true).matches(Path.parse("boom")));
		assertTrue(new PathQuery("hello.world", true).matches(Path.parse("hello.world")));
		assertFalse(new PathQuery("hello", true).matches(new Path()));
		assertTrue(new PathQuery("hello.*.world", true).matches(Path.parse("hello.jupiter.world")));
		assertTrue(new PathQuery("hello.*.world", true).matches(Path.parse("hello.earth.moon.world")));
		assertTrue(new PathQuery("*.class", true).matches(Path.parse("foo.class")));
		assertTrue(new PathQuery("*.class", true).matches(Path.parse("foo.bar.tzar.class")));
		assertFalse(new PathQuery("*.class", true).matches(Path.parse("foo.bar.tzar")));
		assertTrue(new PathQuery("*", true).matches(Path.parse("a.b.c.d")));
		assertTrue(new PathQuery("*.class.*", true).matches(Path.parse("a.b.class.d")));
		assertTrue(new PathQuery("*", true).matches(Path.parse("123.asd.234s.fsdre")));
		assertTrue(new PathQuery("*.*", true).matches(Path.parse("billy.bong.class.yeker")));
	}

	@Test
	public void testPathMatchingDifferences() {
		assertTrue(new PathQuery("one.two", true).matches(Path.parse("one")));
		assertTrue(new PathQuery("one.two.three", true).matches(Path.parse("one")));
		assertTrue(new PathQuery("one.two", true).matches(Path.parse("one.two")));
		assertFalse(new PathQuery("one.two", true).matches(Path.parse("one.two.three")));

		assertFalse(new PathQuery("one.two", false).matches(Path.parse("one")));
		assertTrue(new PathQuery("one.two", false).matches(Path.parse("one.two")));
		assertFalse(new PathQuery("one.two", false).matches(Path.parse("one.two.three")));
	}

}