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
import java.util.Map;

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
		MethodSignatureVisitor msv = getMethodSignatureForSingleMethod(M1.class);

		assertEquals(0, msv.getArgumentsCount());

		assertEquals(CLASS_SIGNATURE + "$M1", msv.getClassname());
		assertEquals("macka#()V", msv.getCleanSignature());
		assertEquals("()", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M1", msv.getDeclaredClassName());
		assertEquals("()V", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals(null, msv.getRawSignature());
		assertEquals('V', msv.getReturnOpcodeType());
		assertEquals("void", msv.getReturnType());
		assertEquals("", msv.getReturnTypeName());
	}


	// ---------------------------------------------------------------- 2

	public static class M2 {
		public int macka(long in1, double in2) {return 0;}
	}

	@Test
	public void testMethodSignature2() throws IOException {
		MethodSignatureVisitor msv = getMethodSignatureForSingleMethod(M2.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals(null, msv.getArgumentTypeName(1));
		assertEquals('J', msv.getArgumentOpcodeType(1));
		assertEquals(null, msv.getArgumentTypeName(2));
		assertEquals('D', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M2", msv.getClassname());
		assertEquals("macka#(JD)I", msv.getCleanSignature());
		assertEquals("(long, double)", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M2", msv.getDeclaredClassName());
		assertEquals("(JD)I", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals(null, msv.getRawSignature());		// ?
		assertEquals('I', msv.getReturnOpcodeType());
		assertEquals("int", msv.getReturnType());
		assertEquals("", msv.getReturnTypeName());
	}

	// ---------------------------------------------------------------- 3

	public static class M3 {
		public Integer macka(Long in1, Double in2) {return 0;}
	}

	@Test
	public void testMethodSignature3() throws IOException {
		MethodSignatureVisitor msv = getMethodSignatureForSingleMethod(M3.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgumentTypeName(1));
		assertEquals('L', msv.getArgumentOpcodeType(1));
		assertEquals("L" + SIGNATURE_JAVA_LANG_DOUBLE + ";", msv.getArgumentTypeName(2));
		assertEquals('L', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M3", msv.getClassname());
		assertEquals("macka#(L" + SIGNATURE_JAVA_LANG_LONG + ";L" + SIGNATURE_JAVA_LANG_DOUBLE + ";)L" + SIGNATURE_JAVA_LANG_INTEGER + ";", msv.getCleanSignature());
		assertEquals("(java.lang.Long, java.lang.Double)", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M3", msv.getDeclaredClassName());
		assertEquals("(Ljava/lang/Long;Ljava/lang/Double;)Ljava/lang/Integer;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals(null, msv.getRawSignature());		// ?
		assertEquals('L', msv.getReturnOpcodeType());
		assertEquals("java.lang.Integer", msv.getReturnType());
		assertEquals("Ljava/lang/Integer;", msv.getReturnTypeName());
	}

	// ---------------------------------------------------------------- 4

	public static class M4 {
		public M4[] macka(Long[] in1, double[] in2) {return null;}
	}

	@Test
	public void testMethodSignature4() throws IOException {
		MethodSignatureVisitor msv = getMethodSignatureForSingleMethod(M4.class);

		assertEquals(2, msv.getArgumentsCount());
		assertEquals(null, msv.getArgumentTypeName(0));
		assertEquals('L', msv.getArgumentOpcodeType(0));
		assertEquals("[L" + SIGNATURE_JAVA_LANG_LONG + ";", msv.getArgumentTypeName(1));
		assertEquals('[', msv.getArgumentOpcodeType(1));
		assertEquals("[D", msv.getArgumentTypeName(2));
		assertEquals('[', msv.getArgumentOpcodeType(2));

		assertEquals(CLASS_SIGNATURE + "$M4", msv.getClassname());
		assertEquals("macka#([L" + SIGNATURE_JAVA_LANG_LONG + ";[D)[" + L_CLASS_SIGNATURE + "$M4;", msv.getCleanSignature());
		assertEquals("(java.lang.Long[], double[])", msv.getDeclaration());
		assertEquals(CLASS_SIGNATURE + "$M4", msv.getDeclaredClassName());
		assertEquals("([Ljava/lang/Long;[D)[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getDescription());
		assertEquals(null, msv.getExceptions());
		assertEquals("macka", msv.getMethodName());
		assertEquals(null, msv.getRawSignature());		// ?
		assertEquals('[', msv.getReturnOpcodeType());
		assertEquals("jodd.proxetta.MethodSignatureVisitorTest$M4[]", msv.getReturnType());
		assertEquals("[Ljodd/proxetta/MethodSignatureVisitorTest$M4;", msv.getReturnTypeName());
	}

	// ---------------------------------------------------------------- util

	private MethodSignatureVisitor getMethodSignatureForSingleMethod(Class klass) throws IOException {
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
