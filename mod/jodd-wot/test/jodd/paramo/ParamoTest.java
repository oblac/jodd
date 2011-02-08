// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.util.ReflectUtil;
import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ParamoTest extends TestCase {

	public static class Foo {
		public Foo(String something) {}
		public void hello() {}
		public void one(String foo) {}
		public void two(String username, String password) {}
		public void array(String foo, Integer[] ints, float[] floats) {}
		public void primitives(int i, long l, float f, double d, short s, boolean b, char c, byte y) {}
	}

	public void testConstructor() throws NoSuchMethodException {
		Constructor c = Foo.class.getConstructor(String.class);
		String[] s = Paramo.resolveParameterNames(c);
		assertEquals(1, s.length);
		assertEquals("something", s[0]);
	}

	public void testOneParam() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("one", String.class);
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(1, s.length);
		assertEquals("foo", s[0]);
	}

	public void testTwoParams() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("two", String.class, String.class);
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(2, s.length);
		assertEquals("username", s[0]);
		assertEquals("password", s[1]);
	}

	public void testNoParams() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("hello");
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(0, s.length);
	}

	public void testArray() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("array", String.class, Integer[].class, float[].class);
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(3, s.length);
		assertEquals("foo", s[0]);
		assertEquals("ints", s[1]);
		assertEquals("floats", s[2]);
	}

	public void testTwoPrimitives() throws NoSuchMethodException {
		Method m = ReflectUtil.findDeclaredMethod(Foo.class, "primitives");
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(8, s.length);
		assertEquals("i", s[0]);
		assertEquals("l", s[1]);
		assertEquals("f", s[2]);
		assertEquals("d", s[3]);
		assertEquals("s", s[4]);
		assertEquals("b", s[5]);
		assertEquals("c", s[6]);
		assertEquals("y", s[7]);
	}

}
