// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class ParamoTest extends TestCase {

	public static class Boo {
		public void hello() {}
		public void one(String foo) {}
		public void two(String username, String password) {}
		public void array(String foo, Integer[] ints) {}
	}

	public void testOneParam() throws NoSuchMethodException {
		Method m = Boo.class.getMethod("one", String.class);
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(1, s.length);
		assertEquals("foo", s[0]);
	}

	public void testTwoParams() throws NoSuchMethodException {
		Method m = Boo.class.getMethod("two", String.class, String.class);
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(2, s.length);
		assertEquals("username", s[0]);
		assertEquals("password", s[1]);
	}

	public void testNoParams() throws NoSuchMethodException {
		Method m = Boo.class.getMethod("hello");
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(0, s.length);
	}

	public void testArray() throws NoSuchMethodException {
		Method m = Boo.class.getMethod("array", String.class, Integer[].class);
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(2, s.length);
		assertEquals("foo", s[0]);
		assertEquals("ints", s[1]);
	}
}
