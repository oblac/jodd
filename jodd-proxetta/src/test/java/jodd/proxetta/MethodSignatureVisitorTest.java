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

package jodd.proxetta;

import jodd.asm5.ClassReader;
import jodd.proxetta.asm.MethodSignatureVisitor;
import jodd.proxetta.fixtures.TargetClassInfoReaderFixture;
import jodd.util.ClassLoaderUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static jodd.asm.AsmUtil.*;
import static org.junit.Assert.assertEquals;

public class MethodSignatureVisitorTest {

	private static final String CLASS_NAME = MethodSignatureVisitorTest.class.getName();
	private static final String CLASS_SIGNATURE = CLASS_NAME.replace('.', '/');
	private static final String L_CLASS_SIGNATURE = "L" + CLASS_SIGNATURE;

	// ---------------------------------------------------------------- 1

	public static class M1 {
		public void macka() {}
	}

	@Test
	public void testMethodSignature1() throws IOException {
		MethodInfo mi = getMethodSignatureForSingleMethod(M1.class);

		assertEquals(0, mi.getArgumentsCount());

		assertEquals(CLASS_SIGNATURE + "$M1", mi.getClassname());
		assertEquals("macka#()V", mi.getCleanSignature());
		assertEquals("()", mi.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M1", mi.getDeclaredClassName());
		assertEquals("()V", mi.getDescription());
		assertEquals(null, mi.getExceptions());
		assertEquals("macka", mi.getMethodName());
		assertEquals('V', mi.getReturnOpcodeType());
		assertEquals("void", mi.getReturnType());
		assertEquals("", mi.getReturnTypeName());
		assertEquals("", mi.getReturnTypeRawName());
	}


	// ---------------------------------------------------------------- 2

	public static class M2 {
		public int macka(long in1, double in2) {return 0;}
	}

	@Test
	public void testMethodSignature2() throws IOException {
		MethodInfo mi = getMethodSignatureForSingleMethod(M2.class);

		assertEquals(2, mi.getArgumentsCount());
		assertEquals(null, mi.getArgumentTypeName(0));
		assertEquals('L', mi.getArgumentOpcodeType(0));
		assertEquals(null, mi.getArgumentTypeName(1));
		assertEquals(null, mi.getArgumentTypeRawName(1));
		assertEquals('J', mi.getArgumentOpcodeType(1));
		assertEquals(null, mi.getArgumentTypeName(2));
		assertEquals(null, mi.getArgumentTypeRawName(2));
		assertEquals('D', mi.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M2", mi.getClassname());
		assertEquals("macka#(JD)I", mi.getCleanSignature());
		assertEquals("(long, double)", mi.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M2", mi.getDeclaredClassName());
		assertEquals("(JD)I", mi.getDescription());
		assertEquals(null, mi.getExceptions());
		assertEquals("macka", mi.getMethodName());
		assertEquals('I', mi.getReturnOpcodeType());
		assertEquals("int", mi.getReturnType());
		assertEquals("", mi.getReturnTypeName());
		assertEquals("", mi.getReturnTypeRawName());
	}

	// ---------------------------------------------------------------- 3

	public static class M3 {
		public Integer macka(Long in1, Double in2) {return 0;}
	}

	@Test
	public void testMethodSignature3() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M3.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgumentTypeName(1));
		assertEquals("L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgumentTypeRawName(1));
		assertEquals('L', msv.getArgumentOpcodeType(1));
		assertEquals("L" + SIGNATURE_JAVA_LANG_DOUBLE + ";", msv.getArgumentTypeName(2));
		assertEquals("L" + SIGNATURE_JAVA_LANG_DOUBLE + ";", msv.getArgumentTypeRawName(2));
		assertEquals('L', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M3", msv.getClassname());
		assertEquals("macka#(L" + SIGNATURE_JAVA_LANG_LONG + ";L" + SIGNATURE_JAVA_LANG_DOUBLE + ";)L" + SIGNATURE_JAVA_LANG_INTEGER + ";", msv.getCleanSignature());
		assertEquals("(java.lang.Long, java.lang.Double)", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M3", msv.getDeclaredClassName());
		assertEquals("(Ljava/lang/Long;Ljava/lang/Double;)Ljava/lang/Integer;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals('L', msv.getReturnOpcodeType());
		assertEquals("java.lang.Integer", msv.getReturnType());
		assertEquals("Ljava/lang/Integer;", msv.getReturnTypeName());
		assertEquals("Ljava/lang/Integer;", msv.getReturnTypeRawName());
	}

	// ---------------------------------------------------------------- 4

	public static class M4 {
		public M4[] macka(Long[] in1, double[] in2) {return null;}
	}

	@Test
	public void testMethodSignature4() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M4.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("[L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgumentTypeName(1));
		assertEquals("[L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgumentTypeRawName(1));
		assertEquals('[', msv.getArgumentOpcodeType(1));
		assertEquals("[D", msv.getArgumentTypeName(2));
		assertEquals("[D", msv.getArgumentTypeRawName(2));
		assertEquals('[', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M4", msv.getClassname());
		assertEquals("macka#([L" + SIGNATURE_JAVA_LANG_LONG + ";[D)[" + L_CLASS_SIGNATURE + "$M4;", msv.getCleanSignature());
		assertEquals("(java.lang.Long[], double[])", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M4", msv.getDeclaredClassName());
		assertEquals("([Ljava/lang/Long;[D)[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals('[', msv.getReturnOpcodeType());
		assertEquals("jodd.proxetta.MethodSignatureVisitorTest$M4[]", msv.getReturnType());
		assertEquals("[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getReturnTypeName());
		assertEquals("[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getReturnTypeRawName());
	}

	// ---------------------------------------------------------------- 5

	public static class M5<Gen, T> {
		public List<Gen> macka(Set<Gen> in1, T[] in2) {return null;}
	}

	@Test
	public void testMethodSignature5() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M5.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("Ljava/util/Set;", msv.getArgumentTypeName(1));
		assertEquals("Ljava/util/Set;", msv.getArgumentTypeRawName(1));
		assertEquals('L', msv.getArgumentOpcodeType(1));
		assertEquals("[T", msv.getArgumentTypeName(2));
		assertEquals("[Ljava/lang/Object;", msv.getArgumentTypeRawName(2));
		assertEquals('[', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M5", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;[Ljava/lang/Object;)Ljava/util/List;", msv.getCleanSignature());
		assertEquals("(java.util.Set<Gen>, T[])", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M5", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;[Ljava/lang/Object;)Ljava/util/List;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals('L', msv.getReturnOpcodeType());
		assertEquals("java.util.List<Gen>", msv.getReturnType());
		assertEquals("Ljava/util/List;", msv.getReturnTypeName());
		assertEquals("Ljava/util/List;", msv.getReturnTypeRawName());
	}


	// ---------------------------------------------------------------- 6

	public static class M6<Gen, Tuta> {
		public Gen[] macka(Set<Gen> in1, Tuta[] in2) {return null;}
	}

	@Test
	public void testMethodSignature6() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M6.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("Ljava/util/Set;", msv.getArgumentTypeName(1));
		assertEquals("Ljava/util/Set;", msv.getArgumentTypeRawName(1));
		assertEquals('L', msv.getArgumentOpcodeType(1));
		assertEquals("[Tuta", msv.getArgumentTypeName(2));
		assertEquals("[Ljava/lang/Object;", msv.getArgumentTypeRawName(2));
		assertEquals('[', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M6", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getCleanSignature());
		assertEquals("(java.util.Set<Gen>, Tuta[])", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M6", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals('[', msv.getReturnOpcodeType());
		assertEquals("Gen[]", msv.getReturnType());
		assertEquals("[Gen", msv.getReturnTypeName());
		assertEquals("[Ljava/lang/Object;", msv.getReturnTypeRawName());
	}

	// ---------------------------------------------------------------- 7

	public static class M7<Gen> {
		public <T> T[] macka(Set<Gen> in1, T[] in2) {return null;}
	}

	@Test
	public void testMethodSignature7() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M7.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("Ljava/util/Set;", msv.getArgumentTypeName(1));
		assertEquals("Ljava/util/Set;", msv.getArgumentTypeRawName(1));
		assertEquals('L', msv.getArgumentOpcodeType(1));
		assertEquals("[T", msv.getArgumentTypeName(2));
		assertEquals("[Ljava/lang/Object;", msv.getArgumentTypeRawName(2));
		assertEquals('[', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M7", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getCleanSignature());
		assertEquals("<T>(java.util.Set<Gen>, T[])", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M7", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals('[', msv.getReturnOpcodeType());
		assertEquals("T[]", msv.getReturnType());
		assertEquals("[T", msv.getReturnTypeName());
		assertEquals("[Ljava/lang/Object;", msv.getReturnTypeRawName());
	}

	// ---------------------------------------------------------------- util

	private MethodInfo getMethodSignatureForSingleMethod(Class klass) throws IOException {
		InputStream in = ClassLoaderUtil.getClassAsStream(klass.getName());
		ClassReader classReader = new ClassReader(in);
		TargetClassInfoReaderFixture targetClassInfoReader = new TargetClassInfoReaderFixture(this.getClass().getClassLoader());
		classReader.accept(targetClassInfoReader, 0);
		in.close();

		Map<String, MethodSignatureVisitor> methodSignatures = targetClassInfoReader.getMethodSignatures();

		return methodSignatures.values()
			.stream()
			.filter(msv -> {
				if (msv.getMethodName().equals("<init>")) {
					return false;
				}
				if (msv.getDeclaredClassName().startsWith(CLASS_SIGNATURE)) {
					return true;
				}
				return false;
			})
			.findFirst().get();
	}
}
