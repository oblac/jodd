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

package jodd.madvoc.macro;

import jodd.util.StringPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathMacroTest {

	private static final String[] SEP1 = new String[] {
			StringPool.DOLLAR_LEFT_BRACE, StringPool.COLON, StringPool.RIGHT_BRACE
	};
	private static final String[] SEP2 = new String[] {
			"<", StringPool.COLON, ">"
	};

	@Test
	public void testSimplePathMacro() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertFalse(pathMacros.init("/some/path/no/macros", SEP1));

		assertTrue(pathMacros.init("/img-${id}.png", SEP1));
		assertEquals("id", pathMacros.getNames()[0]);

		String actionPath = "/img-123.png";
		assertEquals(9, pathMacros.match(actionPath));
		String[] values = pathMacros.extract(actionPath);

		assertEquals(1, values.length);
		assertEquals("123", values[0]);
	}

	@Test
	public void testSimplePathMacroSep2() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertFalse(pathMacros.init("/some/path/no/macros", SEP2));

		assertTrue(pathMacros.init("/img-<id>.png", SEP2));
		assertEquals("id", pathMacros.getNames()[0]);

		String actionPath = "/img-123.png";
		assertEquals(9, pathMacros.match(actionPath));
		String[] values = pathMacros.extract(actionPath);

		assertEquals(1, values.length);
		assertEquals("123", values[0]);
	}

	@Test
	public void testFirstLastPathMacro() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertTrue(pathMacros.init("${di}/img/${id}", SEP1));
		assertEquals("di", pathMacros.getNames()[0]);
		assertEquals("id", pathMacros.getNames()[1]);

		String actionPath = "987/img/123";
		assertEquals(5, pathMacros.match(actionPath));
		String[] values = pathMacros.extract(actionPath);

		assertEquals(2, values.length);
		assertEquals("987", values[0]);
		assertEquals("123", values[1]);
	}

	@Test
	public void testSinglePathMacro() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertTrue(pathMacros.init("${id}", SEP1));
		assertEquals("id", pathMacros.getNames()[0]);

		String actionPath = "123.jpg";
		assertEquals(0, pathMacros.match(actionPath));
		String[] values = pathMacros.extract(actionPath);

		assertEquals(1, values.length);
		assertEquals("123.jpg", values[0]);
	}

	@Test
	public void testThreesomePathMacro() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertTrue(pathMacros.init("/user/${userId}/doc${docId}/v${version}", SEP1));
		assertEquals("userId", pathMacros.getNames()[0]);
		assertEquals("docId", pathMacros.getNames()[1]);
		assertEquals("version", pathMacros.getNames()[2]);

		String actionPath = "/user/173/doc8/";
		assertEquals(-1, pathMacros.match(actionPath));

		actionPath = "/user/173/doc8/v12";
		assertEquals(12, pathMacros.match(actionPath));
		String[] values = pathMacros.extract(actionPath);

		assertEquals(3, values.length);
		assertEquals("173", values[0]);
		assertEquals("8", values[1]);
		assertEquals("12", values[2]);

		actionPath = "/user/173/doc8/v";
		values = pathMacros.extract(actionPath);

		assertEquals(3, values.length);
		assertEquals("173", values[0]);
		assertEquals("8", values[1]);
		assertEquals(StringPool.EMPTY, values[2]);

		actionPath = "/user//doc/v";
		values = pathMacros.extract(actionPath);

		assertEquals(3, values.length);
		assertEquals(StringPool.EMPTY, values[0]);
		assertEquals(StringPool.EMPTY, values[1]);
		assertEquals(StringPool.EMPTY, values[2]);
	}

	@Test
	public void testDummyPathMacro() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertTrue(pathMacros.init("/user/${userId}${version}", SEP1));
		assertEquals("userId", pathMacros.getNames()[0]);
		assertEquals("version", pathMacros.getNames()[1]);

		String actionPath = "/user/jodd";
		assertEquals(6, pathMacros.match(actionPath));

		String[] values = pathMacros.extract(actionPath);
		assertEquals("jodd", values[0]);
		assertEquals(null, values[1]);
	}

	@Test
	public void testWildcardMatch() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertTrue(pathMacros.init("/user-${userId:1*7?3}", SEP1));
		assertEquals("userId", pathMacros.getNames()[0]);

		assertEquals(6, pathMacros.match("/user-1773"));
		assertEquals(6, pathMacros.match("/user-122723"));
		assertEquals(-1, pathMacros.match("/user-17"));
	}

	@Test
	public void testWildcardMatchContaining() {
		PathMacros pathMacros1 = new WildcardPathMacros();
		assertTrue(pathMacros1.init("/${entityName}/dba.delete", SEP1));
		assertEquals("entityName", pathMacros1.getNames()[0]);

		PathMacros pathMacros2 = new WildcardPathMacros();
		assertTrue(pathMacros2.init("/${entityName}/dba.delete_multi", SEP1));
		assertEquals("entityName", pathMacros2.getNames()[0]);

		assertEquals(18, pathMacros2.match("/config/dba.delete_multi"));
		assertEquals(-1, pathMacros1.match("/config/dba.delete_multi"));
	}

	@Test
	public void testWildcardMatchContaining2() {
		PathMacros pathMacros1 = new WildcardPathMacros();
		assertTrue(pathMacros1.init("/dba.delete/${entityName}", SEP1));
		assertEquals("entityName", pathMacros1.getNames()[0]);

		PathMacros pathMacros2 = new WildcardPathMacros();
		assertTrue(pathMacros2.init("/dba.delete_multi/${entityName}", SEP1));
		assertEquals("entityName", pathMacros2.getNames()[0]);

		assertEquals(18, pathMacros2.match("/dba.delete_multi/config"));
		assertEquals(-1, pathMacros1.match("/dba.delete_multi/config"));
	}

	@Test
	public void testWildcardMatchContainingWithTwo() {
		PathMacros pathMacros1 = new WildcardPathMacros();
		assertTrue(pathMacros1.init("/${entityName}/dba.delete${xxx}", SEP1));
		assertEquals("entityName", pathMacros1.getNames()[0]);
		assertEquals("xxx", pathMacros1.getNames()[1]);

		PathMacros pathMacros2 = new WildcardPathMacros();
		assertTrue(pathMacros2.init("/${entityName}/dba.delete_multi${xxx}", SEP1));
		assertEquals("entityName", pathMacros2.getNames()[0]);
		assertEquals("xxx", pathMacros2.getNames()[1]);

		assertEquals(18, pathMacros2.match("/config/dba.delete_multiZZZ"));
		// the following is still matching, but the number of non-matching chars is smaller
		assertEquals(12, pathMacros1.match("/config/dba.delete_multiZZZ"));
	}

}
