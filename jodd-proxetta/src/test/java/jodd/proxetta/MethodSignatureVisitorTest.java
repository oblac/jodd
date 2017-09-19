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
import jodd.proxetta.fixtures.data.FooAnn;
import jodd.util.ClassLoaderUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static jodd.asm.AsmUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
		assertEquals("()void", mi.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M1", mi.getDeclaredClassName());
		assertEquals("()V", mi.getDescription());
		assertNull(mi.getExceptions());
		assertEquals("macka", mi.getMethodName());

		assertEquals('V', mi.getReturnType().getOpcode());
		assertEquals("void", mi.getReturnType().getType());
		assertEquals("V", mi.getReturnType().getName());
		assertEquals("V", mi.getReturnType().getRawName());
	}


	// ---------------------------------------------------------------- 2

	public static class M2 {
		public int macka(long in1, double in2) {return 0;}
	}

	@Test
	public void testMethodSignature2() throws IOException {
		MethodInfo mi = getMethodSignatureForSingleMethod(M2.class);

		assertEquals(2, mi.getArgumentsCount());

		assertEquals("long", mi.getArgument(1).getType());
		assertEquals("J", mi.getArgument(1).getName());
		assertEquals("J", mi.getArgument(1).getRawName());
		assertEquals('J', mi.getArgument(1).getOpcode());

		assertEquals("double", mi.getArgument(2).getType());
		assertEquals("D", mi.getArgument(2).getName());
		assertEquals("D", mi.getArgument(2).getRawName());
		assertEquals('D', mi.getArgument(2).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M2", mi.getClassname());
		assertEquals("macka#(JD)I", mi.getCleanSignature());
		assertEquals("(long, double)int", mi.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M2", mi.getDeclaredClassName());
		assertEquals("(JD)I", mi.getDescription());
		assertNull(mi.getExceptions());
		assertEquals("macka", mi.getMethodName());

		assertEquals('I', mi.getReturnType().getOpcode());
		assertEquals("int", mi.getReturnType().getType());
		assertEquals("I", mi.getReturnType().getName());
		assertEquals("I", mi.getReturnType().getRawName());
	}

	// ---------------------------------------------------------------- 3

	public static class M3 {
		public Integer macka(Long in1, Double in2) {return 0;}
	}

	@Test
	public void testMethodSignature3() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M3.class);

		assertEquals(2, msv.getArgumentsCount());

		assertEquals("java.lang.Long", msv.getArgument(1).getType());
		assertEquals("L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgument(1).getName());
		assertEquals("L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgument(1).getRawName());
		assertEquals('L', msv.getArgument(1).getOpcode());

		assertEquals("java.lang.Double", msv.getArgument(2).getType());
		assertEquals("L" + SIGNATURE_JAVA_LANG_DOUBLE + ";", msv.getArgument(2).getName());
		assertEquals("L" + SIGNATURE_JAVA_LANG_DOUBLE + ";", msv.getArgument(2).getRawName());
		assertEquals('L', msv.getArgument(2).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M3", msv.getClassname());
		assertEquals("macka#(L" + SIGNATURE_JAVA_LANG_LONG + ";L" + SIGNATURE_JAVA_LANG_DOUBLE + ";)L" + SIGNATURE_JAVA_LANG_INTEGER + ";", msv.getCleanSignature());
		assertEquals("(java.lang.Long, java.lang.Double)java.lang.Integer", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M3", msv.getDeclaredClassName());
		assertEquals("(Ljava/lang/Long;Ljava/lang/Double;)Ljava/lang/Integer;", msv.getDescription());
		assertNull(msv.getExceptions());
		assertEquals("macka", msv.getMethodName());

		assertEquals('L', msv.getReturnType().getOpcode());
		assertEquals("java.lang.Integer", msv.getReturnType().getType());
		assertEquals("Ljava/lang/Integer;", msv.getReturnType().getName());
		assertEquals("Ljava/lang/Integer;", msv.getReturnType().getRawName());
	}

	// ---------------------------------------------------------------- 4

	public static class M4 {
		public M4[] macka(Long[] in1, double[] in2) {return null;}
	}

	@Test
	public void testMethodSignature4() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M4.class);

		assertEquals(2, msv.getArgumentsCount());

		assertEquals("java.lang.Long[]", msv.getArgument(1).getType());
		assertEquals("[L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgument(1).getName());
		assertEquals("[L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgument(1).getRawName());
		assertEquals('[', msv.getArgument(1).getOpcode());

		assertEquals("double[]", msv.getArgument(2).getType());
		assertEquals("[D", msv.getArgument(2).getName());
		assertEquals("[D", msv.getArgument(2).getRawName());
		assertEquals('[', msv.getArgument(2).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M4", msv.getClassname());
		assertEquals("macka#([L" + SIGNATURE_JAVA_LANG_LONG + ";[D)[" + L_CLASS_SIGNATURE + "$M4;", msv.getCleanSignature());
		assertEquals("(java.lang.Long[], double[])jodd.proxetta.MethodSignatureVisitorTest$M4[]", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M4", msv.getDeclaredClassName());
		assertEquals("([Ljava/lang/Long;[D)[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getDescription());
		assertNull(msv.getExceptions());
		assertEquals("macka", msv.getMethodName());

		assertEquals('[', msv.getReturnType().getOpcode());
		assertEquals("jodd.proxetta.MethodSignatureVisitorTest$M4[]", msv.getReturnType().getType());
		assertEquals("[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getReturnType().getName());
		assertEquals("[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getReturnType().getRawName());
	}

	// ---------------------------------------------------------------- 5

	public static class M5<Gen, T> {
		public List<Gen> macka(Set<Gen> in1, T[] in2) {return null;}
	}

	@Test
	public void testMethodSignature5() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M5.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals("java.util.Set<Gen>", msv.getArgument(1).getType());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getName());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getRawName());
		assertEquals('L', msv.getArgument(1).getOpcode());

		assertEquals("T[]", msv.getArgument(2).getType());
		assertEquals("[T", msv.getArgument(2).getName());
		assertEquals("[Ljava/lang/Object;", msv.getArgument(2).getRawName());
		assertEquals('[', msv.getArgument(2).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M5", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;[Ljava/lang/Object;)Ljava/util/List;", msv.getCleanSignature());
		assertEquals("(java.util.Set<Gen>, T[])java.util.List<Gen>", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M5", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;[Ljava/lang/Object;)Ljava/util/List;", msv.getDescription());
		assertNull(msv.getExceptions());
		assertEquals("macka", msv.getMethodName());

		assertEquals('L', msv.getReturnType().getOpcode());
		assertEquals("java.util.List<Gen>", msv.getReturnType().getType());
		assertEquals("Ljava/util/List;", msv.getReturnType().getName());
		assertEquals("Ljava/util/List;", msv.getReturnType().getRawName());
	}

	// ---------------------------------------------------------------- 6

	public static class M6<Gen, Tuta> {
		public Gen[] macka(Set<Gen> in1, Tuta[] in2) {return null;}
	}

	@Test
	public void testMethodSignature6() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M6.class);

		assertEquals(2, msv.getArgumentsCount());

		assertEquals("java.util.Set<Gen>", msv.getArgument(1).getType());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getName());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getRawName());
		assertEquals('L', msv.getArgument(1).getOpcode());

		assertEquals("Tuta[]", msv.getArgument(2).getType());
		assertEquals("[Tuta", msv.getArgument(2).getName());
		assertEquals("[Ljava/lang/Object;", msv.getArgument(2).getRawName());
		assertEquals('[', msv.getArgument(2).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M6", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getCleanSignature());
		assertEquals("(java.util.Set<Gen>, Tuta[])Gen[]", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M6", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getDescription());
		assertNull(msv.getExceptions());
		assertEquals("macka", msv.getMethodName());

		assertEquals('[', msv.getReturnType().getOpcode());
		assertEquals("Gen[]", msv.getReturnType().getType());
		assertEquals("[Gen", msv.getReturnType().getName());
		assertEquals("[Ljava/lang/Object;", msv.getReturnType().getRawName());
	}

	// ---------------------------------------------------------------- 7

	public static class M7<Gen> {
		public <T> T[] macka(Set<Gen> in1, T[] in2) {return null;}
	}

	@Test
	public void testMethodSignature7() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M7.class);

		assertEquals(2, msv.getArgumentsCount());

		assertEquals("java.util.Set<Gen>", msv.getArgument(1).getType());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getName());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getRawName());
		assertEquals('L', msv.getArgument(1).getOpcode());

		assertEquals("T[]", msv.getArgument(2).getType());
		assertEquals("[T", msv.getArgument(2).getName());
		assertEquals("[Ljava/lang/Object;", msv.getArgument(2).getRawName());
		assertEquals('[', msv.getArgument(2).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M7", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getCleanSignature());
		assertEquals("<T>(java.util.Set<Gen>, T[])T[]", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M7", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;[Ljava/lang/Object;)[Ljava/lang/Object;", msv.getDescription());
		assertNull(msv.getExceptions());
		assertEquals("macka", msv.getMethodName());

		assertEquals('[', msv.getReturnType().getOpcode());
		assertEquals("T[]", msv.getReturnType().getType());
		assertEquals("[T", msv.getReturnType().getName());
		assertEquals("[Ljava/lang/Object;", msv.getReturnType().getRawName());
	}

	// ---------------------------------------------------------------- 8

	public static class M8 {
		public void macka() throws IOException, NullPointerException {}
	}

	@Test
	public void testMethodSignature8() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M8.class);

		assertEquals(0, msv.getArgumentsCount());

		assertEquals(CLASS_SIGNATURE + "$M8", msv.getClassname());
		assertEquals("macka#()V", msv.getCleanSignature());
		assertEquals("()void", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M8", msv.getDeclaredClassName());
		assertEquals("()V", msv.getDescription());
		assertEquals("java/io/IOException,java/lang/NullPointerException", msv.getExceptionsAsString());
		assertEquals("macka", msv.getMethodName());
	}

	// ---------------------------------------------------------------- 9

	public static class M9 {
		public static void main(String[] args) {
		}
	}

	@Test
	public void testMethodSignature9() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M9.class);

		assertEquals(1, msv.getArgumentsCount());

		assertEquals("java.lang.String[]", msv.getArgument(1).getType());
		assertEquals("[Ljava/lang/String;", msv.getArgument(1).getName());
		assertEquals("[Ljava/lang/String;", msv.getArgument(1).getRawName());
		assertEquals('[', msv.getArgument(1).getOpcode());


		assertEquals(CLASS_SIGNATURE + "$M9", msv.getClassname());
		assertEquals("main#([Ljava/lang/String;)V", msv.getCleanSignature());
		assertEquals("(java.lang.String[])void", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M9", msv.getDeclaredClassName());
		assertEquals("([Ljava/lang/String;)V", msv.getDescription());
		assertEquals("main", msv.getMethodName());
	}

	// ---------------------------------------------------------------- 10

	public static class M10 {
		@FooAnn
		public void macka(int a, @FooAnn long b) {}
	}

	@Test
	public void testMethodSignature10() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M10.class);

		assertEquals(2, msv.getArgumentsCount());

		assertEquals(0, msv.getArgument(1).getAnnotations().length);
		assertEquals(1, msv.getArgument(2).getAnnotations().length);
		assertEquals("jodd.proxetta.fixtures.data.FooAnn", msv.getArgument(2).getAnnotations()[0].getAnnotationClassname());

		assertEquals("macka", msv.getMethodName());
		assertEquals(1, msv.getAnnotations().length);
		assertEquals("jodd.proxetta.fixtures.data.FooAnn", msv.getAnnotations()[0].getAnnotationClassname());
	}

	// ---------------------------------------------------------------- 11

	public static class M11 {
		public List<Map<String, Object>> macka(Set<List<Map<String, Object>>> in1) {return null;}
	}

	@Test
	public void testMethodSignature11() throws IOException {
		MethodInfo msv = getMethodSignatureForSingleMethod(M11.class);

		assertEquals(1, msv.getArgumentsCount());
		assertEquals("java.util.Set<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>", msv.getArgument(1).getType());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getName());
		assertEquals("Ljava/util/Set;", msv.getArgument(1).getRawName());
		assertEquals('L', msv.getArgument(1).getOpcode());

		assertEquals(CLASS_SIGNATURE + "$M11", msv.getClassname());
		assertEquals("macka#(Ljava/util/Set;)Ljava/util/List;", msv.getCleanSignature());
		assertEquals("(java.util.Set<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>)java.util.List<java.util.Map<java.lang.String, java.lang.Object>>", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M11", msv.getDeclaredClassName());
		assertEquals("(Ljava/util/Set;)Ljava/util/List;", msv.getDescription());
		assertNull(msv.getExceptions());
		assertEquals("macka", msv.getMethodName());

		assertEquals('L', msv.getReturnType().getOpcode());
		assertEquals("java.util.List<java.util.Map<java.lang.String, java.lang.Object>>", msv.getReturnType().getType());
		assertEquals("Ljava/util/List;", msv.getReturnType().getName());
		assertEquals("Ljava/util/List;", msv.getReturnType().getRawName());
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
