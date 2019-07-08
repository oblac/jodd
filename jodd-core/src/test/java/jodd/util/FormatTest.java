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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FormatTest {

	@Test
	void testByteSizes_noSi() {
		assertEquals("10 B", Format.humanReadableByteCount(10, false));
		assertEquals("1.0 KiB", Format.humanReadableByteCount(1024, false));
		assertEquals("1.5 KiB", Format.humanReadableByteCount(1024 + 512, false));
	}

	@Test
	void testPadLeft() {
		assertEquals("123   ", Format.alignLeftAndPad("123", 6));
		assertEquals("123", Format.alignLeftAndPad("123", 3));
		assertEquals("12", Format.alignLeftAndPad("123", 2));
	}

	@Test
	void testPadRight() {
		assertEquals("   123", Format.alignRightAndPad("123", 6));
		assertEquals("123", Format.alignRightAndPad("123", 3));
		assertEquals("23", Format.alignRightAndPad("123", 2));
	}


	@Test
	void testToPrettyString() {
		assertEquals(StringPool.NULL, Format.toPrettyString(null));

		assertEquals("[A,B]", Format.toPrettyString(new String[]{"A", "B"}));
		assertEquals("[1,2]", Format.toPrettyString(new int[]{1,2}));
		assertEquals("[1,2]", Format.toPrettyString(new long[]{1,2}));
		assertEquals("[1,2]", Format.toPrettyString(new short[]{1,2}));
		assertEquals("[1,2]", Format.toPrettyString(new byte[]{1,2}));
		assertEquals("[1.0,2.0]", Format.toPrettyString(new double[]{1,2}));
		assertEquals("[1.0,2.0]", Format.toPrettyString(new float[]{1,2}));
		assertEquals("[true,false]", Format.toPrettyString(new boolean[] {true, false}));

		try {
			Format.toPrettyString(new char[]{'a','b'});
			fail("error");
		} catch (IllegalArgumentException e) {
			// ignore
		}

		assertEquals("[[1,2],[3,4]]", Format.toPrettyString(new int[][] {{1, 2}, {3, 4}}));

		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(4);

		assertEquals("{1,4}", Format.toPrettyString(list));
	}

	@Test
	void testToCamelCase() {
		assertEquals("oneTwoThree", Format.toCamelCase("one two   three", false, ' '));
		assertEquals("OneTwo.Three", Format.toCamelCase("one two. three", true, ' '));
		assertEquals("OneTwoThree", Format.toCamelCase("One-two-three", true, '-'));

		assertEquals("userName", Format.toCamelCase("user_name", false, '_'));
		assertEquals("UserName", Format.toCamelCase("user_name", true, '_'));
		assertEquals("user", Format.toCamelCase("user", false, '_'));
		assertEquals("User", Format.toCamelCase("user", true, '_'));
	}

	@Test
	void testFromCamelCase() {
		assertEquals("one two three", Format.fromCamelCase("oneTwoThree", ' '));
		assertEquals("one-two-three", Format.fromCamelCase("oneTwoThree", '-'));
		assertEquals("one. two. three", Format.fromCamelCase("one.Two.Three", ' '));

		assertEquals("user_name", Format.fromCamelCase("userName", '_'));
		assertEquals("user_name", Format.fromCamelCase("UserName", '_'));
		assertEquals("user_name", Format.fromCamelCase("USER_NAME", '_'));
		assertEquals("user_name", Format.fromCamelCase("user_name", '_'));
		assertEquals("user", Format.fromCamelCase("user", '_'));
		assertEquals("user", Format.fromCamelCase("User", '_'));
		assertEquals("user", Format.fromCamelCase("USER", '_'));
		assertEquals("user", Format.fromCamelCase("_user", '_'));
		assertEquals("user", Format.fromCamelCase("_User", '_'));
		assertEquals("_user", Format.fromCamelCase("__user", '_'));
		assertEquals("user__name", Format.fromCamelCase("user__name", '_'));
	}


	@Test
	void testFormatPara() {
		String txt = "123 567 90AB";
		String p = Format.formatParagraph(txt, 6, false);
		assertEquals("123 56\n7 90AB\n", p);

		p = Format.formatParagraph(txt, 4, false);
		assertEquals("123\n567\n90AB\n", p);

		txt = "123  67 90AB";
		p = Format.formatParagraph(txt, 4, false);
		assertEquals("123\n67\n90AB\n", p);

		txt = "123 567 90AB";
		p = Format.formatParagraph(txt, 6, true);
		assertEquals("123\n567\n90AB\n", p);

		txt = "123  67 90AB";
		p = Format.formatParagraph(txt, 4, true);
		assertEquals("123\n67\n90AB\n", p);
		txt = "123  67 90ABCDE";
		p = Format.formatParagraph(txt, 4, true);
		assertEquals("123\n67\n90AB\nCDE\n", p);

		txt = "1234567";
		p = Format.formatParagraph(txt, 4, true);
		assertEquals("1234\n567\n", p);
		p = Format.formatParagraph(txt, 4, false);
		assertEquals("1234\n567\n", p);

	}

	@Test
	void testTabsToSpaces() {
		String s = Format.convertTabsToSpaces("q\tqa\t", 3);
		assertEquals("q  qa ", s);

		s = Format.convertTabsToSpaces("q\tqa\t", 0);
		assertEquals("qqa", s);
	}


	@Test
	void testJavaEscapes() {
		String from = "\r\t\b\f\n\\\"asd\u0111q\u0173aa\u0ABC\u0abc";
		String to = "\\r\\t\\b\\f\\n\\\\\\\"asd\\u0111q\\u0173aa\\u0abc\\u0abc";

		assertEquals(to, Format.escapeJava(from));
		assertEquals(from, Format.unescapeJava(to));

		try {
			Format.unescapeJava("\\r\\t\\b\\f\\q");
			fail("error");
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}



}
