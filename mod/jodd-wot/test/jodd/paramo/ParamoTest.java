// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.paramo.data.Foo;
import jodd.paramo.data.Generic;
import jodd.paramo.data.NonGeneric;
import jodd.util.ReflectUtil;
import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ParamoTest extends TestCase {

	private String[] resolveParameterNames(MethodParameter[] methodParameters) {
		String[] result = new String[methodParameters.length];
		for (
			int i = 0, methodParametersLength = methodParameters.length;
			i < methodParametersLength; i++) {
			MethodParameter methodParameter = methodParameters[i];
			
			result[i] = methodParameter.getName();
		}
		return result;
	}

	public void testConstructor() throws NoSuchMethodException {
		Constructor c = Foo.class.getConstructor(String.class);
		MethodParameter[] mps = Paramo.resolveParameters(c);
		String[] s = resolveParameterNames(mps);
		assertEquals(1, s.length);
		assertEquals("something", s[0]);
	}

	public void testOneParam() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("one", String.class);
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(1, s.length);
		assertEquals("foo", s[0]);
	}

	public void testTwoParams() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("two", String.class, String.class);
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(2, s.length);
		assertEquals("username", s[0]);
		assertEquals("password", s[1]);
	}

	public void testNoParams() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("hello");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertNotNull(mps);
		assertEquals(0, mps.length);
	}

	public void testArray() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("array", String.class, Integer[].class, float[].class);
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(3, s.length);
		assertEquals("foo", s[0]);
		assertEquals("ints", s[1]);
		assertEquals("floats", s[2]);
	}

	public void testPrimitives() throws NoSuchMethodException {
		Method m = ReflectUtil.findDeclaredMethod(Foo.class, "primitives");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
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
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(2, s.length);
		assertEquals("one", s[0]);
		assertEquals("two", s[1]);
	}

	public void testPrimitivesArrays2() throws NoSuchMethodException {
		Method m = ReflectUtil.findDeclaredMethod(Foo.class, "primarr2");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
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
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(3, s.length);
		assertEquals("s", s[0]);
		assertEquals("y", s[1]);
		assertEquals("somethingElse", s[2]);
	}

	public void testNonGeneric() {
		Method m = ReflectUtil.findDeclaredMethod(NonGeneric.class, "one");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertEquals(2, mps.length);
		assertEquals("foo", mps[0].getName());
		assertNull(mps[0].getSignature());
		assertEquals("aLong", mps[1].getName());
		assertNull(mps[1].getSignature());
	}

	public void testGeneric() {
		Method m = ReflectUtil.findDeclaredMethod(Generic.class, "one");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertEquals(2, mps.length);
		assertEquals("foo", mps[0].getName());
		assertEquals("Ljava/util/Map<Ljava/lang/String;Ljava/lang/Long;>;", mps[0].getSignature());
		assertEquals("aLong", mps[1].getName());
		assertNull(mps[1].getSignature());

		m = ReflectUtil.findDeclaredMethod(Generic.class, "two");
		mps = Paramo.resolveParameters(m);
		assertEquals(1, mps.length);
		assertEquals("zzz", mps[0].getName());
		assertEquals("Ljava/util/Map<Ljava/lang/String;Ljodd/paramo/data/Bar<Ljava/lang/Long;>;>;", mps[0].getSignature());
	}

}
