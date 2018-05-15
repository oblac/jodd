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

package jodd.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharUtilTest {

	@Test
	void testToSimpleByteArray() {
		char[] src = new char[]{0, 10, 'A', 127, 128, 255, 256};
		byte[] dest = CharUtil.toSimpleByteArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals(65, dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(-128, dest[4]);
		assertEquals(-1, dest[5]);
		assertEquals(0, dest[6]);
	}

	@Test
	void testToSimpleCharArray() {
		byte[] src = new byte[]{0, 10, 65, 127, -128, -1};
		char[] dest = CharUtil.toSimpleCharArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals('A', dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(128, dest[4]);
		assertEquals(255, dest[5]);
	}

	@Test
	void testToAsciiByteArray() {
		char[] src = new char[]{0, 10, 'A', 127, 128, 255, 256};
		byte[] dest = CharUtil.toAsciiByteArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals(65, dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(-128, dest[4]);
		assertEquals(-1, dest[5]);
		assertEquals(0x3F, dest[6]);
	}

	@Test
	void testToRawByteArray() {
		char[] src = new char[]{0, 'A', 255, 256, 0xFF7F};
		byte[] dest = CharUtil.toRawByteArray(src);

		assertEquals(src.length * 2, dest.length);

		assertEquals(0, dest[0]);
		assertEquals(0, dest[1]);

		assertEquals(0, dest[2]);
		assertEquals(65, dest[3]);

		assertEquals(0, dest[4]);
		assertEquals(-1, dest[5]);

		assertEquals(1, dest[6]);
		assertEquals(0, dest[7]);

		assertEquals(-1, dest[8]);
		assertEquals(127, dest[9]);
	}

	@Test
	void testToRawCharArray() {
		byte[] src = new byte[]{0, 0, 0, 65, 0, -1, 1, 0, -1};
		char[] dest = CharUtil.toRawCharArray(src);

		assertEquals(src.length / 2 + src.length % 2, dest.length);

		assertEquals(0, dest[0]);
		assertEquals('A', dest[1]);
		assertEquals(255, dest[2]);
		assertEquals(256, dest[3]);
		assertEquals(0xFF00, dest[4]);

	}

	@Test
	void testToByte() throws UnsupportedEncodingException {
		char[] src = "tstč".toCharArray();
		assertEquals(4, src.length);
		assertEquals(269, src[3]);

		byte[] dest = CharUtil.toSimpleByteArray(src);
		assertEquals(4, dest.length);
		assertEquals(269 - 256, dest[3]);
		char[] src2 = CharUtil.toSimpleCharArray(dest);
		assertEquals(4, src2.length);
		assertTrue(src[3] != src2[3]);

		byte[] dest2 = CharUtil.toByteArray(src, "US-ASCII");
		assertEquals(4, dest2.length);
		assertEquals(0x3F, dest2[3]);

		byte[] dest3 = CharUtil.toAsciiByteArray(src);
		assertEquals(4, dest3.length);
		assertEquals(0x3F, dest3[3]);

		dest = CharUtil.toByteArray(src, "UTF16");
		assertEquals(8 + 2, dest.length);    // BOM included
		assertEquals(269 - 256, dest[9]);
		assertEquals(1, dest[8]);
		src2 = CharUtil.toCharArray(dest, "UTF16");
		assertEquals(src[3], src2[3]);

		dest = CharUtil.toByteArray(src, "UTF8");
		assertEquals(5, dest.length);
	}

	@Test
	void testHexToInt() {
		assertEquals(0, CharUtil.hex2int('0'));
		assertEquals(1, CharUtil.hex2int('1'));
		assertEquals(2, CharUtil.hex2int('2'));
		assertEquals(3, CharUtil.hex2int('3'));
		assertEquals(4, CharUtil.hex2int('4'));
		assertEquals(5, CharUtil.hex2int('5'));
		assertEquals(6, CharUtil.hex2int('6'));
		assertEquals(7, CharUtil.hex2int('7'));
		assertEquals(8, CharUtil.hex2int('8'));
		assertEquals(9, CharUtil.hex2int('9'));
		assertEquals(10, CharUtil.hex2int('A'));
		assertEquals(10, CharUtil.hex2int('a'));
		assertEquals(11, CharUtil.hex2int('B'));
		assertEquals(11, CharUtil.hex2int('b'));
		assertEquals(12, CharUtil.hex2int('C'));
		assertEquals(12, CharUtil.hex2int('c'));
		assertEquals(13, CharUtil.hex2int('D'));
		assertEquals(13, CharUtil.hex2int('d'));
		assertEquals(14, CharUtil.hex2int('E'));
		assertEquals(14, CharUtil.hex2int('e'));
		assertEquals(15, CharUtil.hex2int('F'));
		assertEquals(15, CharUtil.hex2int('f'));
	}

	@ParameterizedTest (name = "{index} : CharUtil.toAscii({1}) == {0}")
	@MethodSource("testdata_testToAscii")
	void testToAscii(final int expected, final char input) {
		assertEquals(expected, CharUtil.toAscii(input));
	}

	private static List<Arguments> testdata_testToAscii() {
		// from https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
		// The char data type is a single 16-bit Unicode character. It has a minimum value of '\u0000' (or 0) and a maximum value of '\uffff' (or 65,535 inclusive).
		List<Arguments> params = new ArrayList<>();
		// chars 0 - 255 -> ASCII chars
		for (int c = 0; c <= 255; c++) {
			params.add(Arguments.of(c, (char) c));
		}
		// chars > 255
		params.add(Arguments.of(0x3F, (char)256));
		params.add(Arguments.of(0x3F, (char)Integer.MAX_VALUE));
		// chars < 0 => 65535 and smaller
		params.add(Arguments.of(0x3F, (char)-1));
		params.add(Arguments.of(0x3F, (char)-2));
		params.add(Arguments.of(1, (char)-65535));

		return params;
	}

	@ParameterizedTest (name = "{index} : CharUtil.toSimpleByteArray({1}) == {0}")
	@MethodSource("testdata_testToSimpleByteArray_with_charsequence")
	// Note : launch test method separatly via IntelliJ (2017.2.5) does not work properly
	void testToSimpleByteArray_with_charsequence(final byte[] expected, final CharSequence input) {
		assertArrayEquals(expected, CharUtil.toSimpleByteArray(input));
	}

	private static Stream<Arguments> testdata_testToSimpleByteArray_with_charsequence() {
		return Stream.of(
			Arguments.of(new byte[] {106}, "j"),
			Arguments.of(new byte[] {106,111}, "jo"),
			Arguments.of(new byte[] {106,111,100}, "jod"),
			Arguments.of(new byte[] {106,111,100,100}, "jodd"),
			Arguments.of(new byte[] {74}, "J"),
			Arguments.of(new byte[] {74,79}, "JO"),
			Arguments.of(new byte[] {74,79,68}, "JOD"),
			Arguments.of(new byte[] {74,79,68,68}, "JODD")
		);
	}


	@ParameterizedTest (name = "{index} : CharUtil.toAsciiByteArray({1}) == {0}")
	@MethodSource({"testdata_testToSimpleByteArray_with_charsequence", "testdata_testToAsciiByteArray_with_charsequence"})
		// Note : launch test method separatly via IntelliJ (2017.2.5) does not work properly
	void testToAsciiByteArray_with_charsequence(final byte[] expected, final CharSequence input) {
		assertArrayEquals(expected, CharUtil.toAsciiByteArray(input));
	}

	private static Stream<Arguments> testdata_testToAsciiByteArray_with_charsequence() {
		return Stream.of(
				Arguments.of(new byte[] {63}, new String(new char[]{256})) ,
				Arguments.of(new byte[] {63, 63}, new String(new char[]{454, 276})) ,
				Arguments.of(new byte[] {106, 111, 111, 100}, new String(new char[]{106, 111, 111, 100}))
		);
	}


	@ParameterizedTest (name = "{index} : CharUtil.isUppercaseAlpha({1}) == {0}")
	@MethodSource({"testdata_testIsUppercaseAlpha"})
	void testIsUppercaseAlpha(final boolean expected, final char input) {
		final boolean actual = CharUtil.isUppercaseAlpha(input);

		if (expected) {
			assertTrue(actual, "char '" + input + "' is no uppercase alpha but was expected.");
		} else {
			assertFalse(actual, "char '" + input + "' is uppercase alpha but was not expected.");
		}
	}

	private static List<Arguments> testdata_testIsUppercaseAlpha() {
		final List<Arguments> params = new ArrayList<>();

		// chars 0 - 64 => non uppercase chars
		for (int c = 0; c <= 64; c++) {
			params.add(Arguments.of(false, (char)c));
		}
		// chars 65 - 90 => uppercase chars
		for (int c = 65; c <= 90; c++) {
			params.add(Arguments.of(true, (char)c));
		}
		// chars 91 - 255 => non uppercase chars
		for (int c = 91; c <= 255; c++) {
			params.add(Arguments.of(false, (char)c));
		}

		return params;
	}

	@ParameterizedTest (name = "{index} : CharUtil.isUppercaseAlpha({1}) == {0}")
	@MethodSource({"testdata_testIsHexDigit"})
	void testIsHexDigit(final boolean expected, final char input) {
		final boolean actual = CharUtil.isHexDigit(input);

		if (expected) {
			assertTrue(actual, "char '" + input + "' is no hex digit char but was expected.");
		} else {
			assertFalse(actual, "char '" + input + "' is hex digit char but was not expected.");
		}
	}

	private static List<Arguments> testdata_testIsHexDigit() {
		final List<Arguments> params = new ArrayList<>();

		final List<Integer> hexdigit_chars_as_integers =
				Arrays.asList(
						48,49,50,51,52,53,54,55,56,57, // 0-9
						65,66,67,68,69,70, // A-F
						97,98,99,100,101,102 // a-f
				);

		for (int c = 0; c <= 255; c++) {
			params.add(Arguments.of(hexdigit_chars_as_integers.contains(c), (char)c));
		}

		return params;
	}

	@ParameterizedTest (name = "{index} : CharUtil.isGenericDelimiter({1}) == {0}")
	@CsvSource({
			// only generic delimiters
			"true, :", "true, /", "true, ?", "true, #", "true, [", "true, ]", "true, @",
			// few non generic delimters
			"false, ','", "false, !", "false, +"
		})
	void testIsGenericDelimiter(final boolean expected, final char input) {
		assertEquals(expected, CharUtil.isGenericDelimiter(input));
	}

	@ParameterizedTest (name = "{index} : CharUtil.isSubDelimiter({1}) == {0}")
	@CsvSource({
			// only generic delimiters
			"true, !", "true, $", "true, &", "true, (", "true, )", "true, *", "true, +", "true, ','", "true, ;", "true, =",
			// few non generic delimters
			"false, #", "false, ]"
		})
	void testIsSubDelimiter(final boolean expected, final char input) {
		assertEquals(expected, CharUtil.isSubDelimiter(input));
	}

	@Test
	void testIsSubDelimiter_specialCase() {
		// special case as I dont know how to add singletquote to the parameterized CsvSource
		assertTrue(CharUtil.isSubDelimiter('\''));
	}

	@ParameterizedTest (name = "{index} : CharUtil.isLowercaseAlpha({1}) == {0}")
	@MethodSource({"testdata_testIsLowercaseAlpha"})
	void testIsLowercaseAlpha(final boolean expected, final char input) {
		final boolean actual = CharUtil.isLowercaseAlpha(input);

		if (expected) {
			assertTrue(actual, "char '" + input + "' is no lowercase alpha char but was expected.");
		} else {
			assertFalse(actual, "char '" + input + "' is lowercase alpha char but was not expected.");
		}
	}

	private static List<Arguments> testdata_testIsLowercaseAlpha() {
		final List<Arguments> params = new ArrayList<>();

		final List<Integer> hexdigit_chars_as_integers =
				Arrays.asList(
						// a-z
						97,98,99,100,101,102,103,104,105,106,107,108,109,
						110,111,112,113,114,115,116,117,118,119,120,121,122
				);

		for (int c = 0; c <= 255; c++) {
			params.add(Arguments.of(hexdigit_chars_as_integers.contains(c), (char)c));
		}

		return params;
	}

	@ParameterizedTest (name = "{index} - CharUtil.isWhitespace({1}) == {0}")
	@MethodSource({"testdata_testIsWhitespace"})
	void testIsWhitespace(final boolean expected, final char input) {
		assertEquals(expected, CharUtil.isWhitespace(input));
	}

	private static List<Arguments> testdata_testIsWhitespace() {
		List<Arguments> params = new ArrayList<>();

		// due to code -> char <= ' '
		for (int c = 0; c <= 32; c++) {
			params.add(Arguments.of(true, (char)c));
		}
		// few greater than 32
		params.add(Arguments.of(false, (char)33));
		params.add(Arguments.of(false, (char)43));
		params.add(Arguments.of(false, (char)53));
		params.add(Arguments.of(false, (char)63));
		params.add(Arguments.of(false, (char)73));

		return params;
	}
}
