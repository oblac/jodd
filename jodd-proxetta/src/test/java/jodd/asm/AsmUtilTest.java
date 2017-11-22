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

package jodd.asm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsmUtilTest {

	@Test
	void testTyperef2Name() {
		assertEquals("java.lang.String", AsmUtil.typeref2Name("Ljava/lang/String;"));
	}

	@ParameterizedTest
	@MethodSource(value = "testdata_testTypeToTyperef")
	void testTypeToTyperef(final String expected, final Class input) {
		assertEquals(expected, AsmUtil.typeToTyperef(input));
	}

	private static List<Arguments> testdata_testTypeToTyperef() {
		final List<Arguments> params = new ArrayList<>();

		// primitives
		params.add(Arguments.of("I", int.class));
		params.add(Arguments.of("J", long.class));
		params.add(Arguments.of("Z", boolean.class));
		params.add(Arguments.of("D", double.class));
		params.add(Arguments.of("F", float.class));
		params.add(Arguments.of("S", short.class));
		params.add(Arguments.of("V", void.class));
		params.add(Arguments.of("B", byte.class));
		params.add(Arguments.of("C", char.class));
		// non primitives
		params.add(Arguments.of("Ljava/lang/String;", String.class));
		params.add(Arguments.of("Ljodd/asm/AsmUtil;", AsmUtil.class));
		// Array
		params.add(Arguments.of("[I", int[].class));
		params.add(Arguments.of("[Ljava.lang.String;", String[].class));
		params.add(Arguments.of("[Ljodd.asm.AsmUtil;", AsmUtil[].class));


		return params;
	}

	@ParameterizedTest
	@MethodSource(value = "testdata_testTypeToSignature_Class")
	void testTypeToSignature_Class(final String expected, final Class input) {
		assertEquals(expected, AsmUtil.typeToSignature(input));
	}

	private static List<Arguments> testdata_testTypeToSignature_Class() {
		final List<Arguments> params = new ArrayList<>();

		// primitives
		params.add(Arguments.of("int", int.class));
		params.add(Arguments.of("long", long.class));
		params.add(Arguments.of("boolean", boolean.class));
		params.add(Arguments.of("double", double.class));
		params.add(Arguments.of("float", float.class));
		params.add(Arguments.of("short", short.class));
		params.add(Arguments.of("void", void.class));
		params.add(Arguments.of("byte", byte.class));
		params.add(Arguments.of("char", char.class));
		// non primitives
		params.add(Arguments.of("java/lang/String", String.class));
		params.add(Arguments.of("jodd/asm/AsmUtil", AsmUtil.class));


		return params;
	}

	@ParameterizedTest
	@MethodSource(value = "testdata_testTypeToSignature_String")
	void testTypeToSignature_String(final String expected, final String input) {
		assertEquals(expected, AsmUtil.typeToSignature(input));
	}

	private static List<Arguments> testdata_testTypeToSignature_String() {
		final List<Arguments> params = new ArrayList<>();

		params.add(Arguments.of("java/lang/String", String.class.getName()));
		params.add(Arguments.of("Jodd/makes/fun", "Jodd.makes.fun"));

		return params;
	}
}
