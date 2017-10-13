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

package jodd.paramo;

import jodd.asm.TraceSignatureVisitor;
import jodd.asm5.signature.SignatureReader;
import jodd.paramo.fixtures.Foo;
import jodd.paramo.fixtures.Generic;
import jodd.paramo.fixtures.NonGeneric;
import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParamoTest {

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

	@Test
	public void testConstructor() throws NoSuchMethodException {
		Constructor c = Foo.class.getConstructor(String.class);
		MethodParameter[] mps = Paramo.resolveParameters(c);
		String[] s = resolveParameterNames(mps);
		assertEquals(1, s.length);
		assertEquals("something", s[0]);
	}

	@Test
	public void testOneParam() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("one", String.class);
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(1, s.length);
		assertEquals("foo", s[0]);
	}

	@Test
	public void testTwoParams() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("two", String.class, String.class);
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(2, s.length);
		assertEquals("username", s[0]);
		assertEquals("password", s[1]);
	}

	@Test
	public void testNoParams() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("hello");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertNotNull(mps);
		assertEquals(0, mps.length);
	}

	@Test
	public void testArray() throws NoSuchMethodException {
		Method m = Foo.class.getMethod("array", String.class, Integer[].class, float[].class);
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(3, s.length);
		assertEquals("foo", s[0]);
		assertEquals("ints", s[1]);
		assertEquals("floats", s[2]);
	}

	@Test
	public void testPrimitives() throws NoSuchMethodException {
		Method m = ClassUtil.findDeclaredMethod(Foo.class, "primitives");
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

	@Test
	public void testPrimitivesArrays1() throws NoSuchMethodException {
		Method m = ClassUtil.findDeclaredMethod(Foo.class, "primarr1");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(2, s.length);
		assertEquals("one", s[0]);
		assertEquals("two", s[1]);
	}

	@Test
	public void testPrimitivesArrays2() throws NoSuchMethodException {
		Method m = ClassUtil.findDeclaredMethod(Foo.class, "primarr2");
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

	@Test
	public void testPrimitivesArrays3() throws NoSuchMethodException {
		Method m = ClassUtil.findDeclaredMethod(Foo.class, "primarrShortByte");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		String[] s = resolveParameterNames(mps);
		assertEquals(3, s.length);
		assertEquals("s", s[0]);
		assertEquals("y", s[1]);
		assertEquals("somethingElse", s[2]);
	}

	@Test
	public void testNonGeneric() {
		Method m = ClassUtil.findDeclaredMethod(NonGeneric.class, "one");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertEquals(2, mps.length);
		assertEquals("foo", mps[0].getName());
		assertEquals("Ljava/util/Map;", mps[0].getSignature());
		assertEquals("aLong", mps[1].getName());
		assertEquals("Ljava/lang/Long;", mps[1].getSignature());
	}

	@Test
	public void testGeneric() {
		Method m = ClassUtil.findDeclaredMethod(Generic.class, "one");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertEquals(2, mps.length);
		assertEquals("foo", mps[0].getName());
		assertEquals("Ljava/util/Map<Ljava/lang/String;Ljava/lang/Long;>;", mps[0].getSignature());
		assertEquals("aLong", mps[1].getName());
		assertEquals("Ljava/lang/Long;", mps[1].getSignature());

		m = ClassUtil.findDeclaredMethod(Generic.class, "two");
		mps = Paramo.resolveParameters(m);
		assertEquals(1, mps.length);
		assertEquals("zzz", mps[0].getName());
		assertEquals("Ljava/util/Map<Ljava/lang/String;Ljodd/paramo/fixtures/Bar<Ljava/lang/Long;>;>;", mps[0].getSignature());
	}

	@Test
	public void testGenericsWildcards() {
		Method m = ClassUtil.findDeclaredMethod(Generic.class, "three");
		MethodParameter[] mps = Paramo.resolveParameters(m);
		assertEquals(3, mps.length);

		assertEquals("comparable", mps[0].getName());
		assertEquals("Ljava/lang/Comparable<*>;", mps[0].getSignature());
		assertEquals("(java.lang.Comparable<?>)", resolveSignature(mps[0].getSignature()));


		assertEquals("iterator", mps[1].getName());
		assertEquals("Ljava/util/Iterator<+Ljava/lang/CharSequence;>;", mps[1].getSignature());
		assertEquals("(java.util.Iterator<? extends java.lang.CharSequence>)", resolveSignature(mps[1].getSignature()));


		assertEquals("list", mps[2].getName());
		assertEquals("Ljava/util/List<-Ljava/lang/Integer;>;", mps[2].getSignature());
		assertEquals("(java.util.List<? super java.lang.Integer>)", resolveSignature(mps[2].getSignature()));
	}


	private String resolveSignature(String signature) {
		SignatureReader signatureReader = new SignatureReader("(" + signature + ")V");
		StringBuilder sb = new StringBuilder();
		signatureReader.accept(new TraceSignatureVisitor(sb, true));
		return sb.toString();
	}

}
