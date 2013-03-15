// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

import jodd.util.StringPool;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathMacroTest {

	@Test
	public void testSimplePathMacro() {
		PathMacros pathMacros = new WildcardPathMacros();

		assertFalse(pathMacros.init("/some/path/no/macros"));

		assertTrue(pathMacros.init("/img-${id}.png"));
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

		assertTrue(pathMacros.init("${di}/img/${id}"));
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

		assertTrue(pathMacros.init("${id}"));
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

		assertTrue(pathMacros.init("/user/${userId}/doc${docId}/v${version}"));
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

		assertTrue(pathMacros.init("/user/${userId}${version}"));
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

		assertTrue(pathMacros.init("/user-${userId:1*7?3}"));
		assertEquals("userId", pathMacros.getNames()[0]);

		assertEquals(6, pathMacros.match("/user-1773"));
		assertEquals(6, pathMacros.match("/user-122723"));
		assertEquals(-1, pathMacros.match("/user-17"));
	}

}