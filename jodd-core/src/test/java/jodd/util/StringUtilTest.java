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
import java.util.Locale;

import static jodd.util.ArraysUtil.array;
import static jodd.util.StringPool.ISO_8859_1;
import static jodd.util.StringPool.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {

	@Test
	public void testSplit() {
		String src = "1,22,3,44,5";
		String[] r;

		r = StringUtil.split(src, ",");
		assertEquals(5, r.length);
		assertEquals("1", r[0]);
		assertEquals("22", r[1]);
		assertEquals("3", r[2]);
		assertEquals("44", r[3]);
		assertEquals("5", r[4]);

		src = "1,22,,,5";
		r = StringUtil.split(src, ",");
		assertEquals(5, r.length);
		assertEquals("1", r[0]);
		assertEquals("22", r[1]);
		assertEquals("", r[2]);
		assertEquals("", r[3]);
		assertEquals("5", r[4]);


		src = "173";
		r = StringUtil.split(src, ",");
		assertEquals(1, r.length);
		assertEquals("173", r[0]);

		src = ",";
		r = StringUtil.split(src, ",");
		assertEquals(2, r.length);
		assertEquals("", r[0]);
		assertEquals("", r[1]);
	}


	@Test
	public void testSplit2() {
		String src = "1,22,3,44,5";
		String[] r;

		assertArrayEquals(new String[]{src}, StringUtil.splitc(src, new char[]{}));
		assertArrayEquals(new String[]{""}, StringUtil.splitc("", new char[]{','}));
		assertArrayEquals(new String[]{""}, StringUtil.splitc("", ','));

		r = StringUtil.splitc(src, "");
		assertEquals(1, r.length);
		assertEquals(src, r[0]);

		r = StringUtil.splitc(src, "a,.q");
		assertEquals(5, r.length);
		assertEquals("1", r[0]);
		assertEquals("22", r[1]);
		assertEquals("3", r[2]);
		assertEquals("44", r[3]);
		assertEquals("5", r[4]);

		src = "1,22,,,5";
		r = StringUtil.splitc(src, ",");
		assertEquals(3, r.length);
		assertEquals("1", r[0]);
		assertEquals("22", r[1]);
		assertEquals("5", r[2]);

		r = StringUtil.splitc(src, ',');
		assertEquals(3, r.length);
		assertEquals("1", r[0]);
		assertEquals("22", r[1]);
		assertEquals("5", r[2]);


		src = "173";
		r = StringUtil.splitc(src, ",");
		assertEquals(1, r.length);
		assertEquals("173", r[0]);

		src = "173";
		r = StringUtil.splitc(src, ',');
		assertEquals(1, r.length);
		assertEquals("173", r[0]);


		src = ",";
		r = StringUtil.splitc(src, ",");
		assertEquals(2, r.length);
		assertEquals("", r[0]);
		assertEquals("", r[1]);

		src = ",";
		r = StringUtil.splitc(src, ',');
		assertEquals(2, r.length);
		assertEquals("", r[0]);
		assertEquals("", r[1]);


		src = "1, 22 , 5";
		r = StringUtil.splitc(src, ", ");
		assertEquals(3, r.length);
		assertEquals("1", r[0]);
		assertEquals("22", r[1]);
		assertEquals("5", r[2]);

		src = "   , 22 , 5";
		r = StringUtil.splitc(src, ", ");
		assertEquals(3, r.length);
		assertEquals("", r[0]);
		assertEquals("22", r[1]);
		assertEquals("5", r[2]);
	}


	@Test
	public void testReplace() {
		String src = "12345";

		assertEquals("12qwe45", StringUtil.replace(src, "3", "qwe"));
		assertEquals("1234qwe", StringUtil.replace(src, "5", "qwe"));
		assertEquals("qwe2345", StringUtil.replace(src, "1", "qwe"));
		assertEquals(src, StringUtil.replace(src, "0", "qwe"));
		assertEquals(src, StringUtil.replace(src, "0", null));

		src = "100010001";
		assertEquals("dd000dd000dd", StringUtil.replace(src, "1", "dd"));
		assertEquals(src, StringUtil.replace(src, "2", "dd"));

		src = "qweqwe";
		assertEquals("QWEQWE", StringUtil.replace(src, "qwe", "QWE"));

		src = "11221144";

		assertEquals(src, StringUtil.replaceFirst(src, '5', '5'));
		assertEquals(src, StringUtil.replaceLast(src, '5', '5'));

		src = StringUtil.replaceFirst(src, "11", "55");
		assertEquals("55221144", src);
		src = StringUtil.replaceFirst(src, "11", "55");
		assertEquals("55225544", src);
		src = StringUtil.replaceFirst(src, "11", "55");
		assertEquals("55225544", src);
		String src2 = StringUtil.replaceFirst(src, '5', 'a');
		assertEquals("a5225544", src2);

		src2 = StringUtil.replaceLast(src2, '5', 'x');
		assertEquals("a5225x44", src2);

		src = StringUtil.replaceLast(src, "55", "11");
		assertEquals("55221144", src);
		src = StringUtil.replaceLast(src, "55", "11");
		assertEquals("11221144", src);
		src = StringUtil.replaceLast(src, "55", "11");
		assertEquals("11221144", src);

		assertEquals("qwerty", StringUtil.replace("qwerty", "", "xxxxxxxxxxxxx"));
	}


	@Test
	public void testIndexOf() {
		String src = "1234567890qWeRtY";

		assertEquals(1, StringUtil.indexOfIgnoreCase(src, new String[]{"345", "234"})[0]);
		assertEquals(1, StringUtil.indexOfIgnoreCase(src, new String[]{"345", "234"})[1]);
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase(src, new String[]{"345", "234"})[0]);
		assertEquals(2, StringUtil.lastIndexOfIgnoreCase(src, new String[]{"345", "234"})[1]);

		assertEquals(10, StringUtil.indexOf(src, 'q', 5, 20));
		assertEquals(10, StringUtil.indexOfIgnoreCase(src, 'Q', 5, 20));
		assertEquals(-1, StringUtil.lastIndexOf("123", "12345", 0, 5));
		assertEquals(-1, StringUtil.lastIndexOf("", 'a', 0, 5));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("", 'a', 0, 5));
		assertEquals(10, StringUtil.lastIndexOfIgnoreCase(src, 'Q', 20, 1));

		assertEquals(0, StringUtil.indexOfIgnoreCase(src, "123"));
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase(src, "123"));
		assertEquals(0, src.lastIndexOf("123"));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "123"));
		assertTrue(StringUtil.endsWithIgnoreCase(src, "y"));
		assertTrue(StringUtil.endsWithIgnoreCase(src, "qwerty"));
		assertFalse(StringUtil.endsWithIgnoreCase(src, "qwert"));
		assertFalse(StringUtil.endsWithIgnoreCase(src, "q"));
		assertFalse(StringUtil.endsWithIgnoreCase("123", "12345"));


		assertEquals(0, StringUtil.indexOfIgnoreCase(src, "1234567890QwErTy"));
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase(src, "1234567890QwErTy"));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "1234567890QwErTy"));
		assertTrue(StringUtil.endsWithIgnoreCase(src, "1234567890QwErTy"));

		assertEquals(1, StringUtil.indexOfIgnoreCase(src, "2345"));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(src, "2345"));
		assertFalse(StringUtil.startsWithIgnoreCase(src, "2345"));

		assertEquals(10, StringUtil.indexOfIgnoreCase(src, "qwe"));
		assertEquals(10, StringUtil.lastIndexOfIgnoreCase(src, "qwe"));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "qwe", 10));

		assertEquals(10, StringUtil.indexOfIgnoreCase(src, "qwerty"));
		assertEquals(10, StringUtil.lastIndexOfIgnoreCase(src, "qwerty"));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "qwerty", 10));

		assertEquals(10, StringUtil.indexOfIgnoreCase(src, "QWERTY"));
		assertEquals(10, StringUtil.lastIndexOfIgnoreCase(src, "QWERTY"));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "QWERTY", 10));

		assertEquals(-1, StringUtil.indexOfIgnoreCase(src, "qwertyu"));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(src, "qwertyu"));
		assertFalse(StringUtil.startsWithIgnoreCase(src, "qwertyu", 10));

		assertEquals(10, StringUtil.indexOfIgnoreCase(src, "qwerty", 9));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(src, "qwerty", 9));
		assertEquals(-1, src.lastIndexOf("qWeRtY", 9));

		assertEquals(-1, StringUtil.indexOfIgnoreCase(src, "qwerty", 11));
		assertEquals(10, StringUtil.lastIndexOfIgnoreCase(src, "qwerty", 11));
		assertEquals(10, src.lastIndexOf("qWeRtY", 11));
		assertEquals(10, StringUtil.lastIndexOfIgnoreCase(src, "qwerty", 10));
		assertEquals(10, src.lastIndexOf("qWeRtY", 10));
		assertFalse(StringUtil.startsWithIgnoreCase(src, "qwerty", 11));

		src = "AAA111aaa";

		assertEquals(0, StringUtil.indexOfIgnoreCase(src, "aaa", 0));
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase(src, "aaa", 0));
		assertEquals(0, src.lastIndexOf("AAA", 0));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "aaa", 0));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "aa", 1));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "a", 2));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "A", 6));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "AA", 6));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "AAA", 6));
		assertFalse(StringUtil.startsWithIgnoreCase(src, "AAAA", 6));

		assertEquals(6, StringUtil.indexOfIgnoreCase(src, "aaa", 1));
		assertEquals(6, StringUtil.lastIndexOfIgnoreCase(src, "aaa", 7));
		assertEquals(6, src.lastIndexOf("aaa", 7));

		src = "123";

		assertEquals(-1, StringUtil.indexOfIgnoreCase(src, "1234"));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(src, "1234"));

		assertEquals(0, StringUtil.indexOfIgnoreCase(src, ""));
		assertEquals(3, StringUtil.lastIndexOfIgnoreCase(src, ""));
		assertEquals(3, src.lastIndexOf(""));


		// boundaries
		assertEquals(-1, StringUtil.indexOf("xxx", "a", -1, 20));
		assertEquals(-1, StringUtil.indexOf("xxx", "a", 0, 20));
		assertEquals(-1, StringUtil.indexOf("xxx", "a", 10, 20));
		assertEquals(-1, StringUtil.indexOf("xxx", 'a', -1, 20));
		assertEquals(-1, StringUtil.indexOf("xxx", 'a', 0, 20));
		assertEquals(-1, StringUtil.indexOf("xxx", 'a', 10, 20));
		assertEquals(-1, StringUtil.indexOfIgnoreCase("xxx", "a", -1, 20));
		assertEquals(-1, StringUtil.indexOfIgnoreCase("xxx", "a", 0, 20));
		assertEquals(-1, StringUtil.indexOfIgnoreCase("xxx", "a", 10, 20));
		assertEquals(-1, StringUtil.indexOfIgnoreCase("xxx", 'a', -1, 20));
		assertEquals(-1, StringUtil.indexOfIgnoreCase("xxx", 'a', 0, 20));
		assertEquals(-1, StringUtil.indexOfIgnoreCase("xxx", 'a', 10, 20));

		assertEquals(-1, StringUtil.lastIndexOf("xxx", "a", 20, 0));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", "a", 3, -1));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", "a", -5, -10));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", 'a', 20, 0));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", 'a', 3, -1));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", 'a', -5, -10));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", "a", 20, 0));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", "a", 3, -1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", "a", -5, -10));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", 'a', 20, 0));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", 'a', 3, -1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", 'a', -5, -10));


		// empty
		assertEquals(0, StringUtil.indexOf("xxx", "", -1, 3));
		assertEquals(0, StringUtil.indexOf("xxx", "", 0, 3));
		assertEquals(1, StringUtil.indexOf("xxx", "", 1, 3));
		assertEquals(2, StringUtil.indexOf("xxx", "", 2, 3));
		assertEquals(3, StringUtil.indexOf("xxx", "", 3, 30));
		assertEquals(3, StringUtil.indexOf("xxx", "", 4, 30));

		assertEquals(0, StringUtil.indexOfIgnoreCase("xxx", "", -1, 3));
		assertEquals(0, StringUtil.indexOfIgnoreCase("xxx", "", 0, 3));
		assertEquals(1, StringUtil.indexOfIgnoreCase("xxx", "", 1, 3));
		assertEquals(2, StringUtil.indexOfIgnoreCase("xxx", "", 2, 3));
		assertEquals(3, StringUtil.indexOfIgnoreCase("xxx", "", 3, 30));
		assertEquals(3, StringUtil.indexOfIgnoreCase("xxx", "", 4, 30));

		assertEquals(3, StringUtil.lastIndexOf("xxx", "", 10, -1));
		assertEquals(3, StringUtil.lastIndexOf("xxx", "", 3, -1));
		assertEquals(2, StringUtil.lastIndexOf("xxx", "", 2, -1));
		assertEquals(1, StringUtil.lastIndexOf("xxx", "", 1, -1));
		assertEquals(0, StringUtil.lastIndexOf("xxx", "", 0, -1));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", "", -1, -10));
		assertEquals(-1, StringUtil.lastIndexOf("xxx", "", -10, -20));

		assertEquals(3, StringUtil.lastIndexOfIgnoreCase("xxx", "", 10, -1));
		assertEquals(3, StringUtil.lastIndexOfIgnoreCase("xxx", "", 3, -1));
		assertEquals(2, StringUtil.lastIndexOfIgnoreCase("xxx", "", 2, -1));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase("xxx", "", 1, -1));
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase("xxx", "", 0, -1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", "", -1, -10));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("xxx", "", -10, -20));


	}

	@Test
	public void testRemove() {
		String s = "qwertyq";
		assertEquals("qwertyq", StringUtil.remove(s, "W"));
		assertEquals("qertyq", StringUtil.remove(s, "w"));
		assertEquals("", StringUtil.remove(s, "qwertyq"));
		assertEquals("qwertyq", StringUtil.remove(s, ""));
		assertEquals("werty", StringUtil.remove(s, "q"));
		assertEquals("qq", StringUtil.remove(s, "werty"));

		assertEquals("werty", StringUtil.removeChars(s, "q"));
		assertEquals("werty", StringUtil.remove(s, 'q'));
		assertEquals(s, StringUtil.remove(s, 'x'));
		assertEquals("qeryq", StringUtil.removeChars(s, "wt"));
		assertEquals("qeryq", StringUtil.removeChars(s, new char[]{'w', 't'}));
		assertEquals("", StringUtil.removeChars(s, "qwerty".toCharArray()));
		assertEquals("", StringUtil.removeChars(s, "qwerty"));
	}

	@Test
	public void testArrays() {
		String s = "qwertyuiop";

		assertEquals("qWERtyuIOp", StringUtil.replace(s, new String[]{"wer", "io"}, new String[]{"WER", "IO"}));
		assertEquals("qwertyuiop", StringUtil.replace(s, new String[]{"wer1", "io1"}, new String[]{"WER", "IO"}));

		assertEquals("qWERtyuIOP", StringUtil.replace(s, new String[] {"wer", "iop"}, new String[] {"WER", "IOP"}));
		assertEquals("qWERtyuIOP", StringUtil.replaceIgnoreCase(s, new String[] {"WER", "IOP"}, new String[] {"WER", "IOP"}));
		assertEquals(s, StringUtil.replaceIgnoreCase(s, new String[] {"WER", "IOP"}, new String[] {"WER"}));
		assertEquals(s, StringUtil.replaceIgnoreCase(s, new String[] {}, new String[] {"WER"}));

		assertEquals("qwertyuiop", StringUtil.replace(s, new String[] {}, new String[] {}));

		assertEquals("qWERtyuiop", StringUtil.replace(s, new String[] {"wer", "we"}, new String[] {"WER", "11"}));

		assertTrue(StringUtil.equals(new String[] {"wer", "io"}, new String[] {"wer", "io"}));
		assertFalse(StringUtil.equals(new String[] {"wer", "io"}, new String[] {"WER", "IO"}));
		assertTrue(StringUtil.equalsIgnoreCase(new String[] {"wer", "io"}, new String[] {"WER", "IO"}));

		assertEquals(1, StringUtil.indexOf(s, new String[]{"wer", "io"})[1]);
		assertEquals(7, StringUtil.indexOfIgnoreCase(s, new String[]{"wer", "IO"}, 2)[1]);
		assertEquals(7, StringUtil.lastIndexOf(s, new String[]{"wer", "io"})[1]);
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, new String[]{"WER", "io"}, 5)[1]);
	}

	@Test
	public void testRanges() {
		String s = "qwertyiop";

		assertEquals(1, StringUtil.indexOf(s, "wer", 0, 5));
		assertEquals(1, StringUtil.indexOf(s, 'w', 0, 5));
		assertEquals(1, StringUtil.indexOf(s, "wer", 1, 5));
		assertEquals(1, StringUtil.indexOf(s, 'w', 1, 5));
		assertEquals(-1, StringUtil.indexOf(s, "wer", 2, 5));
		assertEquals(-1, StringUtil.indexOf(s, 'w', 2, 5));
		assertEquals(1, s.indexOf("wer"));
		assertEquals(1, s.indexOf("wer", 1));
		assertEquals(-1, s.indexOf("wer", 2));

		assertEquals(1, StringUtil.indexOf(s, "wer", 1, 4));
		assertEquals(1, StringUtil.indexOf(s, 'w', 1, 4));
		assertEquals(-1, StringUtil.indexOf(s, "wer", 1, 3));
		assertEquals(1, StringUtil.indexOf(s, 'w', 1, 3));
		assertEquals(-1, StringUtil.indexOf(s, "wer", 0, 3));
		assertEquals(1, StringUtil.indexOf(s, 'w', 0, 3));
		assertEquals('r', s.charAt(3));


		assertEquals(6, StringUtil.indexOf(s, "iop", 0, s.length()));
		assertEquals(6, StringUtil.indexOf(s, 'i', 0, s.length()));
		assertEquals(6, StringUtil.indexOf(s, "iop", 6, s.length()));
		assertEquals(6, StringUtil.indexOf(s, 'i', 6, s.length()));
		assertEquals(-1, StringUtil.indexOf(s, "iop", 0, s.length() - 1));
		assertEquals(6, StringUtil.indexOf(s, 'i', 0, s.length() - 1));
		assertEquals(-1, StringUtil.indexOf(s, "iop", 7, s.length()));
		assertEquals(-1, StringUtil.indexOf(s, 'i', 7, s.length()));
		assertEquals(6, s.indexOf("iop", 0));
		assertEquals(6, s.indexOf("iop", 6));
		assertEquals(-1, s.indexOf("iop", 7));


		assertEquals(1, StringUtil.indexOfIgnoreCase(s, "wEr", 0, 5));
		assertEquals(1, StringUtil.indexOfIgnoreCase(s, "wEr", 1, 5));
		assertEquals(-1, StringUtil.indexOfIgnoreCase(s, "wEr", 2, 5));

		assertEquals(1, StringUtil.indexOfIgnoreCase(s, "wEr", 1, 4));
		assertEquals(-1, StringUtil.indexOfIgnoreCase(s, "wEr", 0, 3));

		assertEquals(6, StringUtil.indexOfIgnoreCase(s, "iOp", 0, s.length()));
		assertEquals(6, StringUtil.indexOfIgnoreCase(s, "iOp", 6, s.length()));
		assertEquals(-1, StringUtil.indexOfIgnoreCase(s, "iOp", 0, s.length() - 1));
		assertEquals(-1, StringUtil.indexOfIgnoreCase(s, "iOp", 7, s.length()));


		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 5, 0));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 5, 1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 5, 2));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 5, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 5, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 5, 1));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 5, 1));
		assertEquals(-1, StringUtil.lastIndexOf(s, "wer", 5, 2));
		assertEquals(-1, StringUtil.lastIndexOf(s, 'w', 5, 2));
		assertEquals('w', s.charAt(1));
		assertEquals(1, s.lastIndexOf("wer", 4));
		assertEquals(1, s.lastIndexOf("wer", 3));
		assertEquals(1, s.lastIndexOf("wer", 2));
		assertEquals(1, s.lastIndexOf("wer", 1));
		assertEquals(-1, s.lastIndexOf("wer", 0));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 4));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 3));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 2));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 0));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 4, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 4, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 3, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 3, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 2, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 2, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 1, 0));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 1, 0));
		assertEquals(-1, StringUtil.lastIndexOf(s, "wer", 0, 0));
		assertEquals(-1, StringUtil.lastIndexOf(s, 'w', 0, 0));

		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 4, 1));
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 3, 1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(s, "wEr", 3, 2));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 4, 1));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 4, 1));
		assertEquals(1, StringUtil.lastIndexOf(s, "wer", 3, 1));
		assertEquals(1, StringUtil.lastIndexOf(s, 'w', 3, 1));
		assertEquals(-1, StringUtil.lastIndexOf(s, "wer", 3, 2));
		assertEquals(-1, StringUtil.lastIndexOf(s, 'w', 3, 2));

		assertEquals(6, StringUtil.lastIndexOfIgnoreCase(s, "iOp", s.length(), 0));
		assertEquals(6, StringUtil.lastIndexOf(s, "iop", s.length() - 1, 0));
		assertEquals(6, StringUtil.lastIndexOf(s, 'i', s.length() - 1, 0));
		assertEquals(6, s.lastIndexOf("iop", s.length()));
		assertEquals(6, StringUtil.lastIndexOfIgnoreCase(s, "iOp", s.length(), 6));
		assertEquals(6, StringUtil.lastIndexOf(s, "iop", s.length() - 1, 6));
		assertEquals(6, StringUtil.lastIndexOf(s, 'i', s.length() - 1, 6));
		assertEquals(6, StringUtil.lastIndexOfIgnoreCase(s, "iOp", s.length() - 1, 0));
		assertEquals(6, StringUtil.lastIndexOf(s, "iop", s.length() - 1, 0));
		assertEquals(6, StringUtil.lastIndexOf(s, 'i', s.length() - 1, 0));
		assertEquals(6, s.lastIndexOf("iop", s.length() - 1));
		assertEquals(-1, StringUtil.lastIndexOfIgnoreCase(s, "iOp", s.length(), 7));
		assertEquals(-1, StringUtil.lastIndexOf(s, "iop", s.length() - 1, 7));
		assertEquals(-1, StringUtil.lastIndexOf(s, 'i', s.length() - 1, 7));


		assertEquals(0, StringUtil.indexOf(s, "", 0, 5));
		assertEquals(1, StringUtil.indexOf(s, "", 1, 5));
		assertEquals(0, s.indexOf("", 0));
		assertEquals(1, s.indexOf("", 1));

		assertEquals(4, StringUtil.lastIndexOf(s, "", 4, 0));

		assertEquals(-1, StringUtil.indexOfWhitespace("123"));
		assertEquals(0, StringUtil.indexOfNonWhitespace("123"));
		assertEquals(-1, StringUtil.indexOfWhitespace(""));
		assertEquals(-1, StringUtil.indexOfNonWhitespace(""));
		assertEquals(2, StringUtil.indexOfWhitespace("12 123"));
		assertEquals(2, StringUtil.indexOfNonWhitespace("  12 123"));
		assertEquals(5, StringUtil.indexOfNonWhitespace("  12 123", 4));
		assertEquals(0, StringUtil.indexOfNonWhitespace("12 123"));
		assertEquals(2, StringUtil.lastIndexOfWhitespace("12 123"));
		assertEquals(5, StringUtil.indexOfWhitespace("12 12 3", 4));
		assertEquals(2, StringUtil.lastIndexOfWhitespace("12 12 3", 4));
		assertEquals(-1, StringUtil.lastIndexOfWhitespace("12 12 3", 4, 3));
		assertEquals(6, StringUtil.lastIndexOfNonWhitespace("12 12 3"));
		assertEquals(4, StringUtil.lastIndexOfNonWhitespace("12 12  "));
		assertEquals(-1, StringUtil.lastIndexOfNonWhitespace("       "));
		assertEquals(6, StringUtil.lastIndexOfNonWhitespace("12 12 3", 9));
		assertEquals(4, StringUtil.lastIndexOfNonWhitespace("12 12  ", 6, 3));
		assertEquals(4, StringUtil.lastIndexOfNonWhitespace("12 12  ", 6, 4));

		assertEquals(-1, StringUtil.lastIndexOfWhitespace("", 4));
		assertEquals(2, StringUtil.lastIndexOfWhitespace("12 12 ", 4, -1));
		assertEquals(-1, StringUtil.lastIndexOfNonWhitespace("", 4));
		assertEquals(4, StringUtil.lastIndexOfNonWhitespace("12 12 ", 4, -1));
	}


	@Test
	public void testEscapeQuotes() {
		String s = "ccc'aaa";
		assertEquals("ccc\\'aaa", StringUtil.replace(s, "'", "\\'"));

		s = "ccc\"aaa";
		assertEquals("ccc\\\"aaa", StringUtil.replace(s, "\"", "\\\""));
	}


	@Test
	public void testTrim() {
		assertEquals("123", StringUtil.trimDown(" 123 "));
		assertEquals("123", StringUtil.trimDown("123"));
		assertNull(StringUtil.trimDown(""));
		assertNull(StringUtil.trimDown("     "));

		String[] strings = new String[]{" aa ", "\ra\t", null, " "};
		StringUtil.trimAll(strings);
		assertEquals("aa", strings[0]);
		assertEquals("a", strings[1]);
		assertNull(strings[2]);
		assertEquals("", strings[3]);

		strings = new String[]{" aa ", "\ra\t", null, " "};
		StringUtil.trimDownAll(strings);
		assertEquals("aa", strings[0]);
		assertEquals("a", strings[1]);
		assertNull(strings[2]);
		assertNull(strings[3]);

		String s = " \t123\t ";
		assertEquals("123\t ", StringUtil.trimLeft(s));
		assertEquals(" \t123", StringUtil.trimRight(s));
	}


	void checkInts(int x, int y, int z, int w, int[] arr) {
		assertNotNull(arr);
		assertEquals(x, arr[0], "1.arg");
		assertEquals(y, arr[1], "2.arg");
		assertEquals(z, arr[2], "3.arg");
		assertEquals(w, arr[3], "4.arg");
	}

	@Test
	public void testRegion() {
		String string = "qwertyuiop";
		assertNull(StringUtil.indexOfRegion(string, "x", "e"));
		int[] res;
		res = StringUtil.indexOfRegion(string, "q", "e");
		checkInts(0, 1, 2, 3, res);
		assertEquals("w", string.substring(res[1], res[2]));
		res = StringUtil.indexOfRegion(string, "qw", "e");
		checkInts(0, 2, 2, 3, res);
		assertEquals("", string.substring(res[1], res[2]));
		res = StringUtil.indexOfRegion(string, "qwe", "e");
		assertNull(res);
		res = StringUtil.indexOfRegion(string, "qwe", "ui");
		checkInts(0, 3, 6, 8, res);
		assertEquals("rty", string.substring(res[1], res[2]));

		string = "aa\\${fo${o\\}foo}dd";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		checkInts(7, 9, 15, 16, res);
		assertEquals("o\\}foo", string.substring(res[1], res[2]));


		string = "xx${123}www";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		checkInts(2, 4, 7, 8, res);
		res = StringUtil.indexOfRegion(string, "${", "}", '\\', -10);
		checkInts(2, 4, 7, 8, res);

		res = StringUtil.indexOfRegion(string, "${", "}", '\\', 3);
		assertNull(res);
		res = StringUtil.indexOfRegion(string, "${", "}", '\\', 100);
		assertNull(res);


		string = "xx${123www";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		assertNull(res);

		string = "xx${123}ww";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		checkInts(2, 4, 7, 8, res);

		string = "xx\\${123}ww";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		assertNull(res);

		string = "xx\\\\${123}ww";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		assertNotNull(res);
		checkInts(3, 6, 9, 10, res);
		assertEquals("\\${", string.substring(res[0], res[1]));
		assertEquals("123", string.substring(res[1], res[2]));
		assertEquals("}", string.substring(res[2], res[3]));

		string = "xx\\\\\\${123}ww";
		res = StringUtil.indexOfRegion(string, "${", "}", '\\');
		assertNotNull(res);
		checkInts(4, 7, 10, 11, res);
		assertEquals("\\${", string.substring(res[0], res[1]));
		assertEquals("123", string.substring(res[1], res[2]));
		assertEquals("}", string.substring(res[2], res[3]));

	}

	@Test
	public void testReplaceChar() {
		String s = "1234567890";

		assertEquals("x234567890", StringUtil.replaceChar(s, '1', 'x'));
		assertEquals("x2yz567890", StringUtil.replaceChars(s, new char[] {'1', '3', '4'}, new char[] {'x', 'y', 'z'}));
		assertEquals(s, StringUtil.replaceChar(s, 'x', 'x'));
	}

	@Test
	public void testSurround() {
		assertEquals("preqwesuf", StringUtil.surround("qwe", "pre", "suf"));
		assertEquals("preqwesuf", StringUtil.surround("preqwe", "pre", "suf"));
		assertEquals("preqwesuf", StringUtil.surround("qwesuf", "pre", "suf"));
		assertEquals("preqwesuf", StringUtil.surround("preqwesuf", "pre", "suf"));

		assertEquals("fixqwefix", StringUtil.surround("qwe", "fix"));

		assertEquals("preqwe", StringUtil.prefix("qwe", "pre"));
		assertEquals("preqwe", StringUtil.prefix("preqwe", "pre"));

		assertEquals("qwesuf", StringUtil.suffix("qwe", "suf"));
		assertEquals("qwesuf", StringUtil.suffix("qwesuf", "suf"));

	}

	@Test
	public void testCuts() {
		assertEquals("1", StringUtil.cutToIndexOf("123", "2"));
		assertEquals("1", StringUtil.cutToIndexOf("123", '2'));
		assertEquals("123", StringUtil.cutToIndexOf("123", "4"));
		assertEquals("123", StringUtil.cutToIndexOf("123", '4'));
		assertEquals("", StringUtil.cutToIndexOf("123", "1"));

		assertEquals("23", StringUtil.cutFromIndexOf("123", "2"));
		assertEquals("23", StringUtil.cutFromIndexOf("123", '2'));
		assertEquals("3", StringUtil.cutFromIndexOf("123", "3"));
		assertEquals("3", StringUtil.cutFromIndexOf("123", '3'));
		assertEquals("123", StringUtil.cutFromIndexOf("123", "4"));
		assertEquals("123", StringUtil.cutFromIndexOf("123", '4'));
		assertEquals("123", StringUtil.cutFromIndexOf("123", "1"));

		assertEquals("qwe", StringUtil.cutPrefix("preqwe", "pre"));
		assertEquals("preqwe", StringUtil.cutPrefix("preqwe", "pre2"));

		assertEquals("qwe", StringUtil.cutSuffix("qwesuf", "suf"));
		assertEquals("qwesuf", StringUtil.cutPrefix("qwesuf", "suf2"));

		assertEquals("qwe", StringUtil.cutSurrounding("preqwesuf", "pre", "suf"));
		assertEquals("qwe", StringUtil.cutSurrounding("qwesuf", "pre", "suf"));
		assertEquals("qwe", StringUtil.cutSurrounding("preqwe", "pre", "suf"));
		assertEquals("qwe", StringUtil.cutSurrounding("qwe", "pre", "suf"));

		assertEquals("qwe", StringUtil.cutSurrounding("preqwepre", "pre"));
	}


	@Test
	public void testCount() {
		String s = "qwertywer";
		assertEquals(0, StringUtil.count(s, "xxx"));
		assertEquals(0, StringUtil.count(s, ""));
		assertEquals(1, StringUtil.count(s, "qwe"));
		assertEquals(0, StringUtil.count(s, "qwe", 1));
		assertEquals(0, StringUtil.count(s, "qwe", 50));
		assertEquals(1, StringUtil.count(s, "qwe", -1));
		assertEquals(1, StringUtil.count(s, "qwertywer"));
		assertEquals(2, StringUtil.count(s, "we"));

		assertEquals(0, StringUtil.count(s, '0'));
		assertEquals(1, StringUtil.count(s, 'q'));
		assertEquals(2, StringUtil.count(s, 'w'));

		assertEquals(0, StringUtil.countIgnoreCase(s, "xxx"));
		assertEquals(0, StringUtil.countIgnoreCase(s, ""));
		assertEquals(1, StringUtil.countIgnoreCase(s, "qwe"));
		assertEquals(1, StringUtil.countIgnoreCase(s, "qwertywer"));
		assertEquals(2, StringUtil.countIgnoreCase(s, "we"));
	}

	@Test
	public void testIndexOfChars() {
		String s = "12345qwerty";
		assertEquals(0, StringUtil.indexOfChars(s, "1q"));
		assertEquals(0, StringUtil.indexOfChars(s, "1q", 0));
		assertEquals(5, StringUtil.indexOfChars(s, "1q", 1));
		assertEquals(0, StringUtil.indexOfChars(s, "1q", -11));
		assertEquals(-1, StringUtil.indexOfChars(s, "1q", 200));
		assertEquals(1, StringUtil.indexOfChars(s, "q2"));
		assertEquals(5, StringUtil.indexOfChars(s, "yq"));

		assertEquals(5, StringUtil.indexOfChars(s, new char[] {'y', 'q'}));
		assertEquals(-1, StringUtil.indexOfChars(s, new char[] {'x', 'o'}));
	}

	@Test
	public void testEquals() {
		assertTrue(StringUtil.equals("1", "1"));
		assertFalse(StringUtil.equals("1", null));
		assertFalse(StringUtil.equals(null, "2"));
		assertTrue(StringUtil.equals((String) null, null));

		assertFalse(StringUtil.equals(new String[] {"abc", "de"}, new String[] {"abc"}));
		assertFalse(StringUtil.equalsIgnoreCase(new String[] {"abc", "de"}, new String[] {"ABC"}));
		assertFalse(StringUtil.equalsIgnoreCase(new String[] {"abc", "de"}, new String[] {"ab", "dE"}));

		assertEquals(2, StringUtil.equalsOne("src", new String[] {"123", null, "src"}));
		assertEquals(-1, StringUtil.equalsOne("src", new String[] {"123", null, "Src"}));

		assertEquals(2, StringUtil.equalsOneIgnoreCase("sRc", new String[] {"123", null, "Src"}));
		assertEquals(-1, StringUtil.equalsOneIgnoreCase("sRc", new String[] {"123", null, "Dsrc"}));
	}

	@Test
	public void testEmpty() {
		assertFalse(StringUtil.isBlank("foo"));
		assertTrue(StringUtil.isNotBlank("foo"));
		assertTrue(StringUtil.isBlank(""));
		assertFalse(StringUtil.isNotBlank(""));
		assertTrue(StringUtil.isBlank("  "));
		assertFalse(StringUtil.isNotBlank("  "));
		assertTrue(StringUtil.isBlank("  \t \t"));
		assertFalse(StringUtil.isNotBlank("  \t \t"));
		assertTrue(StringUtil.isBlank(null));
		assertFalse(StringUtil.isNotBlank(null));

		assertFalse(StringUtil.isEmpty("foo"));
		assertTrue(StringUtil.isNotEmpty("foo"));
		assertTrue(StringUtil.isEmpty(""));
		assertFalse(StringUtil.isNotEmpty(""));
		assertFalse(StringUtil.isEmpty("  "));
		assertTrue(StringUtil.isNotEmpty("  "));
		assertFalse(StringUtil.isEmpty("  \t \t"));
		assertTrue(StringUtil.isNotEmpty("  \t \t"));
		assertTrue(StringUtil.isEmpty(null));
		assertFalse(StringUtil.isNotEmpty(null));

		assertTrue(StringUtil.isAllEmpty("", null));
		assertFalse(StringUtil.isAllEmpty("", null, "a"));
		assertTrue(StringUtil.isAllBlank("", " ", "\t", "\r", null));
		assertFalse(StringUtil.isAllBlank("", " ", "\t", "\ra", null));
	}

	@Test
	public void testToString() {
		assertEquals("aaa", StringUtil.toString("aaa"));
		assertEquals("173", StringUtil.toString(Integer.valueOf(173)));
		assertNull(StringUtil.toString(null));
		assertEquals("", StringUtil.toSafeString(null));
		assertEquals("3", StringUtil.toSafeString(Long.valueOf(3)));
	}

	@Test
	public void testToPrettyString() {
		assertEquals(StringPool.NULL, StringUtil.toPrettyString(null));

		assertEquals("[A,B]", StringUtil.toPrettyString(new String[]{"A", "B"}));
		assertEquals("[1,2]", StringUtil.toPrettyString(new int[]{1,2}));
		assertEquals("[1,2]", StringUtil.toPrettyString(new long[]{1,2}));
		assertEquals("[1,2]", StringUtil.toPrettyString(new short[]{1,2}));
		assertEquals("[1,2]", StringUtil.toPrettyString(new byte[]{1,2}));
		assertEquals("[1.0,2.0]", StringUtil.toPrettyString(new double[]{1,2}));
		assertEquals("[1.0,2.0]", StringUtil.toPrettyString(new float[]{1,2}));
		assertEquals("[true,false]", StringUtil.toPrettyString(new boolean[] {true, false}));

		try {
			StringUtil.toPrettyString(new char[]{'a','b'});
			fail("error");
		} catch (IllegalArgumentException e) {
			// ignore
		}

		assertEquals("[[1,2],[3,4]]", StringUtil.toPrettyString(new int[][] {{1, 2}, {3, 4}}));

		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(4);

		assertEquals("{1,4}", StringUtil.toPrettyString(list));
	}

	@Test
	public void testCapitalize() {
		assertEquals("F", StringUtil.capitalize("f"));
		assertEquals("Foo", StringUtil.capitalize("foo"));
		assertEquals("Foo", StringUtil.capitalize("Foo"));
		assertEquals("", StringUtil.capitalize(""));

		assertEquals("f", StringUtil.uncapitalize("F"));
		assertEquals("foo", StringUtil.uncapitalize("foo"));
		assertEquals("foo", StringUtil.uncapitalize("Foo"));
		assertEquals("uRL", StringUtil.uncapitalize("URL"));
		assertEquals("", StringUtil.uncapitalize(""));

		assertEquals("f", StringUtil.decapitalize("F"));
		assertEquals("foo", StringUtil.decapitalize("foo"));
		assertEquals("foo", StringUtil.decapitalize("Foo"));
		assertEquals("URL", StringUtil.decapitalize("URL"));
		assertEquals("", StringUtil.decapitalize(""));
	}

	@Test
	public void testTruncate() {
		assertEquals("fo", StringUtil.truncate("foo", 2));
		assertEquals("f", StringUtil.truncate("foo", 1));
		assertEquals("", StringUtil.truncate("foo", 0));
		assertEquals("foo", StringUtil.truncate("foo", 4));
	}

	@Test
	public void testStartWith() {
		assertTrue(StringUtil.startsWithChar("asd", 'a'));
		assertFalse(StringUtil.startsWithChar("asd", 's'));
		assertFalse(StringUtil.startsWithChar("", 'a'));

		assertTrue(StringUtil.endsWithChar("asd", 'd'));
		assertFalse(StringUtil.endsWithChar("asd", 's'));
		assertFalse(StringUtil.endsWithChar("", 'd'));

		assertEquals(3, StringUtil.startsWithOne("qwe123", new String[]{"Qwe", null, ".", "qwe"}));
		assertEquals(-1, StringUtil.startsWithOne("qwe123", new String[]{"Qwe", null, ".", "we"}));
		assertEquals(0, StringUtil.startsWithOneIgnoreCase("qwe123", new String[]{"Qwe", null, ".", "qwe"}));
		assertEquals(-1, StringUtil.startsWithOneIgnoreCase("qwe123", new String[]{"we", null, ".", "we"}));

		assertEquals(3, StringUtil.endsWithOne("qwezxc", new String[] {"Zxc", null, ".", "zxc"}));
		assertEquals(-1, StringUtil.endsWithOne("qwezxc", new String[] {"Zxc", null, ".", "zx"}));
		assertEquals(0, StringUtil.endsWithOneIgnoreCase("qweZXC", new String[] {"Zxc", null, ".", "zxc"}));
		assertEquals(-1, StringUtil.endsWithOneIgnoreCase("qweZXC", new String[] {"zx", null, ".", "zx"}));
	}


	@Test
	public void testStrip() {
		assertEquals("we", StringUtil.stripLeadingChar("qwe", 'q'));
		assertEquals("qwe", StringUtil.stripLeadingChar("qwe", '4'));
		assertEquals("qw", StringUtil.stripTrailingChar("qwe", 'e'));
		assertEquals("qwe", StringUtil.stripTrailingChar("qwe", '4'));
		assertEquals("", StringUtil.stripChar("", '4'));
		assertEquals("", StringUtil.stripChar("4", '4'));
		assertEquals("2", StringUtil.stripChar("424", '4'));
		assertEquals("23", StringUtil.stripChar("423", '4'));
		assertEquals("23", StringUtil.stripChar("4234", '4'));
		assertEquals("4", StringUtil.stripChar("4", '5'));
	}

	@Test
	public void testCrop() {
		assertEquals("123", StringUtil.crop("123"));
		assertEquals(" ", StringUtil.crop(" "));
		assertNull(StringUtil.crop(""));

		String[] s = new String[]{" ", null, ""};
		StringUtil.cropAll(s);
		assertEquals(" ", s[0]);
		assertNull(s[1]);
		assertNull(s[2]);
	}

	@Test
	public void testJoin() {
		assertNull(StringUtil.join(null));
		assertEquals(StringPool.EMPTY, StringUtil.join(new Object[] {}));

		assertEquals("123", StringUtil.join(array("123")));

		assertEquals("123", StringUtil.join(array("1", "2", "3")));
		assertEquals("13", StringUtil.join(array("1", "", "3")));
		assertEquals("1null3", StringUtil.join(array("1", null, "3")));

		String s = StringUtil.join(array("1", "2", "3"), ".");
		assertEquals("1.2.3", s);

		 s = StringUtil.join(array("1", "2", "3"), '.');
			assertEquals("1.2.3", s);

		s = StringUtil.join(array("1"), '.');
		assertEquals("1", s);

		s = StringUtil.join(new String[0], ".");
		assertEquals("", s);

		assertNull(StringUtil.join(array(null), "."));
		assertEquals(StringPool.EMPTY, StringUtil.join(new Object[] {}, "."));
		assertEquals("123", StringUtil.join(new String[] { "123" }, "."));

		assertNull(StringUtil.join(array(null), '.'));
		assertEquals(StringPool.EMPTY, StringUtil.join(new Object[] {}, '.'));
		assertEquals("123", StringUtil.join(new String[] { "123" }, '.'));
	}

	@Test
	public void testCharset() {
		assertEquals("123", StringUtil.convertCharset("123", UTF_8, UTF_8));
		assertEquals("123", StringUtil.convertCharset("123", ISO_8859_1, UTF_8));
		String s = StringUtil.convertCharset("\250\275", UTF_8, ISO_8859_1);
		assertEquals(4, s.length());
		assertEquals(194, s.charAt(0));
		assertEquals(168, s.charAt(1));
		assertEquals(194, s.charAt(2));
		assertEquals(189, s.charAt(3));

		try {
			assertEquals("123", StringUtil.convertCharset("123", "yyy", "xxx"));
			fail("error");
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}

	@Test
	public void testIsCharAt() {
		assertTrue(StringUtil.isCharAtEqual("123", 0, '1'));
		assertTrue(StringUtil.isCharAtEqual("123", 1, '2'));
		assertTrue(StringUtil.isCharAtEqual("123", 2, '3'));
		assertFalse(StringUtil.isCharAtEqual("123", 0, '3'));
		assertFalse(StringUtil.isCharAtEqual("123", -1, '3'));
		assertFalse(StringUtil.isCharAtEqual("123", 5, '3'));
	}

	@Test
	public void testEscape() {
		assertFalse(StringUtil.isCharAtEscaped("1\\23", 1, '\\'));
		assertTrue(StringUtil.isCharAtEscaped("1\\23", 2, '\\'));
		assertFalse(StringUtil.isCharAtEscaped("1\\23", 3, '\\'));
		assertFalse(StringUtil.isCharAtEscaped("1\\23", 0, '\\'));

		assertEquals(4, StringUtil.indexOfUnescapedChar("1\\23244", '2', '\\'));
		assertEquals(4, StringUtil.indexOfUnescapedChar("1\\23244", '2', '\\', -1));
		assertEquals(-1, StringUtil.indexOfUnescapedChar("1\\23244", '2', '\\', 6));
		assertEquals(-1, StringUtil.indexOfUnescapedChar("1\\23", '2', '\\'));
	}

	@Test
	public void testRepeat() {
		assertEquals("1212", StringUtil.repeat("12", 2));
		assertEquals("xxxx", StringUtil.repeat('x', 4));
	}

	@Test
	public void testReverse() {
		assertEquals("12345", StringUtil.reverse("54321"));
	}

	@Test
	public void testMaxCommonPrefix() {
		assertEquals("", StringUtil.maxCommonPrefix("qwe", "asd"));
		assertEquals("1", StringUtil.maxCommonPrefix("1qwe", "1asd"));
		assertEquals("123", StringUtil.maxCommonPrefix("123", "123"));
		assertEquals("123", StringUtil.maxCommonPrefix("123456", "123"));
	}

	@Test
	public void testToCamelCase() {
		assertEquals("oneTwoThree", StringUtil.toCamelCase("one two   three", false, ' '));
		assertEquals("OneTwo.Three", StringUtil.toCamelCase("one two. three", true, ' '));
		assertEquals("OneTwoThree", StringUtil.toCamelCase("One-two-three", true, '-'));

		assertEquals("userName", StringUtil.toCamelCase("user_name", false, '_'));
		assertEquals("UserName", StringUtil.toCamelCase("user_name", true, '_'));
		assertEquals("user", StringUtil.toCamelCase("user", false, '_'));
		assertEquals("User", StringUtil.toCamelCase("user", true, '_'));
	}

	@Test
	public void testFromCamelCase() {
		assertEquals("one two three", StringUtil.fromCamelCase("oneTwoThree", ' '));
		assertEquals("one-two-three", StringUtil.fromCamelCase("oneTwoThree", '-'));
		assertEquals("one. two. three", StringUtil.fromCamelCase("one.Two.Three", ' '));

		assertEquals("user_name", StringUtil.fromCamelCase("userName", '_'));
		assertEquals("user_name", StringUtil.fromCamelCase("UserName", '_'));
		assertEquals("user_name", StringUtil.fromCamelCase("USER_NAME", '_'));
		assertEquals("user_name", StringUtil.fromCamelCase("user_name", '_'));
		assertEquals("user", StringUtil.fromCamelCase("user", '_'));
		assertEquals("user", StringUtil.fromCamelCase("User", '_'));
		assertEquals("user", StringUtil.fromCamelCase("USER", '_'));
		assertEquals("user", StringUtil.fromCamelCase("_user", '_'));
		assertEquals("user", StringUtil.fromCamelCase("_User", '_'));
		assertEquals("_user", StringUtil.fromCamelCase("__user", '_'));
		assertEquals("user__name", StringUtil.fromCamelCase("user__name", '_'));
	}

	@Test
	public void testJavaEscapes() {
		String from = "\r\t\b\f\n\\\"asd\u0111q\u0173aa\u0ABC\u0abc";
		String to = "\\r\\t\\b\\f\\n\\\\\\\"asd\\u0111q\\u0173aa\\u0abc\\u0abc";

		assertEquals(to, StringUtil.escapeJava(from));
		assertEquals(from, StringUtil.unescapeJava(to));

		try {
			StringUtil.unescapeJava("\\r\\t\\b\\f\\q");
			fail("error");
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}

	@Test
	public void testFindCommonPrefix() {
		assertEquals("", StringUtil.findCommonPrefix("123", "234"));
		assertEquals("123", StringUtil.findCommonPrefix("123"));
		assertEquals("12", StringUtil.findCommonPrefix("123xxx", "125", "12xxxxx"));
		assertEquals("1", StringUtil.findCommonPrefix("123xxx", "1", "12xxxxx"));
		assertEquals("", StringUtil.findCommonPrefix("", "1", "12xxxxx"));
		assertEquals("123", StringUtil.findCommonPrefix("123", "123", "123"));
	}

	@Test
	public void testShorten() {
		assertEquals("Long...", StringUtil.shorten("Long long sentence", 8, "..."));
		assertEquals("Longl...", StringUtil.shorten("Longlong sentence", 8, "..."));
	}

	@Test
	public void testCompressChars() {
		assertEquals("1 2 3", StringUtil.compressChars("1   2    3", ' '));
		assertEquals("1 2 3", StringUtil.compressChars("1 2 3", ' '));
	}

	@Test
	public void testTitle() {
		assertEquals("A New Day Is Born", StringUtil.title("a neW day IS born"));
	}

	@Test
	public void testSubstring() {
		assertEquals("2", StringUtil.substring("123", 1, 2));
		assertEquals("23", StringUtil.substring("123", 1, 2000));
		assertEquals("123", StringUtil.substring("123", -1000, 2000));
		assertEquals("", StringUtil.substring("123", 1, 1));

		assertEquals("2", StringUtil.substring("123", 1, -1));
		assertEquals("", StringUtil.substring("123", 1, -2));
		assertEquals("", StringUtil.substring("123", 2, -1));

		assertEquals("2", StringUtil.substring("123", -2, -1));

		assertEquals("23", StringUtil.substring("123", -2, 0));

		assertEquals("AbCdE", StringUtil.substring("AbCdEf", 0, -1));
		assertEquals("dEf", StringUtil.substring("AbCdEf", -3, 0));
		assertEquals("dE", StringUtil.substring("AbCdEf", -3, -1));
		assertEquals("AbCdE", StringUtil.substring("AbCdEf", -3000, -1));
		assertEquals("", StringUtil.substring("AbCdEf", 1000, 2000));
	}

	@Test
	public void testStripFromToChar() {
		assertEquals("1", StringUtil.stripFromChar("1234", '2'));
		assertEquals("", StringUtil.stripFromChar("1234", '1'));
		assertEquals("1234", StringUtil.stripFromChar("1234", 'X'));

		assertEquals("234", StringUtil.stripToChar("1234", '2'));
		assertEquals("1234", StringUtil.stripToChar("1234", '1'));
		assertEquals("1234", StringUtil.stripToChar("1234", 'X'));
	}


	@Test
	public void testFormatPara() {
		String txt = "123 567 90AB";
		String p = StringUtil.formatParagraph(txt, 6, false);
		assertEquals("123 56\n7 90AB\n", p);

		p = StringUtil.formatParagraph(txt, 4, false);
		assertEquals("123\n567\n90AB\n", p);

		txt = "123  67 90AB";
		p = StringUtil.formatParagraph(txt, 4, false);
		assertEquals("123\n67\n90AB\n", p);

		txt = "123 567 90AB";
		p = StringUtil.formatParagraph(txt, 6, true);
		assertEquals("123\n567\n90AB\n", p);

		txt = "123  67 90AB";
		p = StringUtil.formatParagraph(txt, 4, true);
		assertEquals("123\n67\n90AB\n", p);
		txt = "123  67 90ABCDE";
		p = StringUtil.formatParagraph(txt, 4, true);
		assertEquals("123\n67\n90AB\nCDE\n", p);

		txt = "1234567";
		p = StringUtil.formatParagraph(txt, 4, true);
		assertEquals("1234\n567\n", p);
		p = StringUtil.formatParagraph(txt, 4, false);
		assertEquals("1234\n567\n", p);

	}

	@Test
	public void testTabsToSpaces() {
		String s = StringUtil.convertTabsToSpaces("q\tqa\t", 3);
		assertEquals("q  qa ", s);

		s = StringUtil.convertTabsToSpaces("q\tqa\t", 0);
		assertEquals("qqa", s);
	}

	@Test
	public void testContainsOnly() {
		assertTrue(StringUtil.containsOnlyWhitespaces("       "));
		assertFalse(StringUtil.containsOnlyWhitespaces("12345"));
		assertTrue(StringUtil.containsOnlyDigits("12345"));
		assertFalse(StringUtil.containsOnlyDigits("12345a"));
		assertTrue(StringUtil.containsOnlyDigitsAndSigns("+12345"));
		assertFalse(StringUtil.containsOnlyDigitsAndSigns("-12345a"));
	}

	@Test
	public void testToStringArray() {
		assertArrayEquals(new String[0], StringUtil.toStringArray(null));
		assertArrayEquals(new String[]{"abc"}, StringUtil.toStringArray("abc"));
		assertArrayEquals(new String[]{"ab", "cd"}, StringUtil.toStringArray(new String[]{"ab", "cd"}));

		assertArrayEquals(new String[]{"1","2"}, StringUtil.toStringArray(new int[]{1,2}));
		assertArrayEquals(new String[] {"1", "2"}, StringUtil.toStringArray(new long[] {1, 2}));
		assertArrayEquals(new String[] {"1", "2"}, StringUtil.toStringArray(new short[] {1, 2}));
		assertArrayEquals(new String[] {"1", "2"}, StringUtil.toStringArray(new byte[] {1, 2}));
		assertArrayEquals(new String[] {"1.0", "2.0"}, StringUtil.toStringArray(new double[] {1, 2}));
		assertArrayEquals(new String[] {"1.0", "2.0"}, StringUtil.toStringArray(new float[] {1, 2}));
		assertArrayEquals(new String[] {"true", "false"}, StringUtil.toStringArray(new boolean[] {true, false}));
		try {
			StringUtil.toStringArray(new char[]{'a','b'});
			fail("error");
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}

	@Test
	public void testInsert() {
		assertEquals("abcd12345", StringUtil.insert("12345", "abcd"));
		assertEquals("abcd12345", StringUtil.insert("12345", "abcd", -1));
		assertEquals("12345abcd", StringUtil.insert("12345", "abcd", 6));
		assertEquals("12abcd345", StringUtil.insert("12345", "abcd", 2));
	}

	@Test
	public void testToLowerCase() {
		assertNull(StringUtil.toLowerCase(null));
		assertEquals("abcd", StringUtil.toLowerCase("abCD"));
		assertEquals("abcdčđž", StringUtil.toLowerCase("abCDČđŽ"));
		assertEquals("abcdčđž", StringUtil.toLowerCase("abCDČđŽ", Locale.US));
		assertEquals("*-+", StringUtil.toLowerCase("*-+"));
	}

	@Test
	public void testToUpperCase() {
		assertNull(StringUtil.toUpperCase(null));
		assertEquals("ABCD", StringUtil.toUpperCase("abCD"));
		assertEquals("ABCDČĐŽ", StringUtil.toUpperCase("abCDČđŽ"));
		assertEquals("ABCDČĐŽ", StringUtil.toUpperCase("abCDČđŽ", Locale.US));
		assertEquals("*-+", StringUtil.toUpperCase("*-+"));
	}

	@Test
	public void testToHexString() {
		assertEquals("3F", StringUtil.toHexString(new byte[] {0x3F}));
		assertEquals("CAFEBABE", StringUtil.toHexString(new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE}));
	}

	@Test
	public void testCutBetween() {
		assertEquals("45", StringUtil.cutBetween("123456", "23", "6"));
		assertEquals("", StringUtil.cutBetween("123456", "234", "456"));
		assertEquals("", StringUtil.cutBetween("123456", "2345", "456"));
		assertNull(StringUtil.cutBetween("123456", "2345", "aa"));
		assertNull(StringUtil.cutBetween("123456", "bbb", "34"));
	}

	@Test
	public void testIsSubstringAt() {
		assertTrue(StringUtil.isSubstringAt("qwerty", "we", 1));
		assertTrue(StringUtil.isSubstringAt("qwerty", "qwe", 0));
		assertTrue(StringUtil.isSubstringAt("qwerty", "qwerty", 0));
		assertFalse(StringUtil.isSubstringAt("qwerty", "qwerty", 1));
		assertFalse(StringUtil.isSubstringAt("qwerty", "y", 4));
		assertTrue(StringUtil.isSubstringAt("qwerty", "y", 5));
		assertTrue(StringUtil.isSubstringAt("qwerty", "", 5));
	}

	@Test
	public void testRemoveQuotes() {
		assertEquals("123", StringUtil.removeQuotes("123"));
		assertEquals("'123", StringUtil.removeQuotes("'123"));
		assertEquals("123", StringUtil.removeQuotes("'123'"));
		assertEquals("123'", StringUtil.removeQuotes("123'"));

		assertEquals("\"123", StringUtil.removeQuotes("\"123"));
		assertEquals("123", StringUtil.removeQuotes("\"123\""));
		assertEquals("123\"", StringUtil.removeQuotes("123\""));

		assertEquals("'123\"", StringUtil.removeQuotes("'123\""));
		assertEquals("\"123'", StringUtil.removeQuotes("\"123'"));
	}

}
