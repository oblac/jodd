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

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

	@Test
	public void testPaths() {
		Path path = new Path();

		assertEquals(0, path.length());
		assertEquals("[]", path.toString());

		path.push("one");
		assertEquals(1, path.length());
		assertEquals("[one]", path.toString());

		path.push("two");
		assertEquals(2, path.length());
		assertEquals("[one.two]", path.toString());

		path.push("three");
		assertEquals(3, path.length());
		assertEquals("[one.two.three]", path.toString());

		path.push("four");
		path.push("five");
		path.push("six");
		path.push("seven");
		path.push("eight");
		path.push("nine");
		path.push("ten");

		assertEquals(10, path.length());
		assertEquals("[one.two.three.four.five.six.seven.eight.nine.ten]", path.toString());

		assertEquals("ten", path.pop());
		assertEquals("nine", path.pop());
		assertEquals("eight", path.pop());
		assertEquals("seven", path.pop());
		assertEquals("six", path.pop());
		assertEquals("five", path.pop());

		assertEquals("[one.two.three.four]", path.toString());

		assertTrue(Path.parse("one.two.three.four").equals(path));
		assertEquals(Path.parse("one.two.three.four").hashCode(), path.hashCode());

		path.pop();

		assertFalse(Path.parse("one.two.three.four").equals(path));
		assertNotEquals(Path.parse("one.two.three.four").hashCode(), path.hashCode());

		assertFalse(Path.parse("one.two.thre").equals(path));
		assertNotEquals(Path.parse("one.two.thre").hashCode(), path.hashCode());
	}
}