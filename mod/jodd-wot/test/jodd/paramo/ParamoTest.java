// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.paramo.data.Foo;
import jodd.util.ReflectUtil;
import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ParamoTest extends TestCase {

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

	public void testPrimitives() throws NoSuchMethodException {
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

	public void testPrimitivesArrays1() throws NoSuchMethodException {
		Method m = ReflectUtil.findDeclaredMethod(Foo.class, "primarr1");
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(2, s.length);
		assertEquals("one", s[0]);
		assertEquals("two", s[1]);
	}

	public void testPrimitivesArrays2() throws NoSuchMethodException {
		Method m = ReflectUtil.findDeclaredMethod(Foo.class, "primarr2");
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(6, s.length);
		assertEquals("i", s[0]);
		assertEquals("l", s[1]);
		assertEquals("f", s[2]);
		assertEquals("d", s[3]);
		assertEquals("b", s[4]);
		assertEquals("c", s[5]);
	}

	public void testPrimitivesArrays3() throws NoSuchMethodException {
		Method m = ReflectUtil.findDeclaredMethod(Foo.class, "primarrShortByte");
		String[] s = Paramo.resolveParameterNames(m);
		assertEquals(3, s.length);
		assertEquals("s", s[0]);
		assertEquals("y", s[1]);
		assertEquals("somethingElse", s[2]);
	}

}
