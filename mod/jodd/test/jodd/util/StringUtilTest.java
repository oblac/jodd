// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;

import static jodd.util.StringPool.ISO_8859_1;
import static jodd.util.StringPool.UTF_8;

public class StringUtilTest extends TestCase {

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


	public void testSplit2() {
		String src = "1,22,3,44,5";
		String[] r;

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
	}


	public void testIndexOf() {
		String src = "1234567890qWeRtY";

		assertEquals(1, StringUtil.indexOfIgnoreCase(src, new String[] {"345", "234"})[0]);
		assertEquals(1, StringUtil.indexOfIgnoreCase(src, new String[] {"345", "234"})[1]);
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase(src, new String[] {"345", "234"})[0]);
		assertEquals(2, StringUtil.lastIndexOfIgnoreCase(src, new String[] {"345", "234"})[1]);
		
		assertEquals(0, StringUtil.indexOfIgnoreCase(src, "123"));
		assertEquals(0, StringUtil.lastIndexOfIgnoreCase(src, "123"));
		assertEquals(0, src.lastIndexOf("123"));
		assertTrue(StringUtil.startsWithIgnoreCase(src, "123"));
		assertTrue(StringUtil.endsWithIgnoreCase(src, "y"));
		assertTrue(StringUtil.endsWithIgnoreCase(src, "qwerty"));
		assertFalse(StringUtil.endsWithIgnoreCase(src, "qwert"));
		assertFalse(StringUtil.endsWithIgnoreCase(src, "q"));
		
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
		assertEquals("qeryq", StringUtil.removeChars(s, "wt"));
		assertEquals("qeryq", StringUtil.removeChars(s, new char[] {'w', 't'}));
		assertEquals("", StringUtil.removeChars(s, "qwerty".toCharArray()));
		assertEquals("", StringUtil.removeChars(s, "qwerty"));
	}
	
	public void testArrays() {
		String s = "qwertyuiop";
		
		assertEquals("qWERtyuIOp", StringUtil.replace(s, new String[] {"wer", "io"}, new String[] {"WER", "IO"}));
		assertEquals("qwertyuiop", StringUtil.replace(s, new String[] {"wer1", "io1"}, new String[] {"WER", "IO"}));
		
		assertEquals("qWERtyuIOP", StringUtil.replace(s, new String[] {"wer", "iop"}, new String[] {"WER", "IOP"}));		
		assertEquals("qWERtyuIOP", StringUtil.replaceIgnoreCase(s, new String[] {"WER", "IOP"}, new String[] {"WER", "IOP"}));
		
		assertEquals("qwertyuiop", StringUtil.replace(s, new String[] {}, new String[] {}));
		
		assertEquals("qWERtyuiop", StringUtil.replace(s, new String[] {"wer", "we"}, new String[] {"WER", "11"}));
		
		assertTrue(StringUtil.equals(new String[] {"wer", "io"}, new String[] {"wer", "io"}));
		assertFalse(StringUtil.equals(new String[] {"wer", "io"}, new String[] {"WER", "IO"}));
		assertTrue(StringUtil.equalsIgnoreCase(new String[] {"wer", "io"}, new String[] {"WER", "IO"}));
		
		assertEquals(1, StringUtil.indexOf(s, new String[] {"wer", "io"})[1]);
		assertEquals(7, StringUtil.indexOfIgnoreCase(s, new String[] {"wer", "IO"}, 2)[1]);
		assertEquals(7, StringUtil.lastIndexOf(s, new String[] {"wer", "io"})[1]);
		assertEquals(1, StringUtil.lastIndexOfIgnoreCase(s, new String[] {"WER", "io"}, 5)[1]);
	}

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
		assertEquals(4, StringUtil.lastIndexOfNonWhitespace("12 12  ", 6, 3));
		assertEquals(4, StringUtil.lastIndexOfNonWhitespace("12 12  ", 6, 4));
	}


	public void testEscapeQuotes() {
		String s = "ccc'aaa";
		assertEquals("ccc\\'aaa", StringUtil.replace(s, "'", "\\'"));

		s = "ccc\"aaa";
		assertEquals("ccc\\\"aaa", StringUtil.replace(s, "\"", "\\\""));
	}


	public void testTrim() {
		assertEquals("123", StringUtil.trimDown(" 123 "));
		assertEquals("123", StringUtil.trimDown("123"));
		assertNull(StringUtil.trimDown(""));
		assertNull(StringUtil.trimDown("     "));

		String[] strings = new String[] {" aa ", "\ra\t", null, " "};
		StringUtil.trimAll(strings);
		assertEquals("aa", strings[0]);
		assertEquals("a", strings[1]);
		assertNull(strings[2]);
		assertEquals("", strings[3]);

		strings = new String[] {" aa ", "\ra\t", null, " "};
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
		assertEquals("1.arg", x, arr[0]);
		assertEquals("2.arg", y, arr[1]);
		assertEquals("3.arg", z, arr[2]);
		assertEquals("4.arg", w, arr[3]);
	}

	public void testRegion() {
		String string = "qwertyuiop";
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

	public void testReplaceChar() {
		String s = "1234567890";

		assertEquals("x234567890", StringUtil.replaceChar(s, '1', 'x')); 
		assertEquals("x2yz567890", StringUtil.replaceChars(s, new char[] {'1', '3', '4'}, new char[] {'x', 'y', 'z'})); 
	}

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

	public void testIndexOfChars() {
		String s = "12345qwerty";
		assertEquals(0, StringUtil.indexOfChars(s, "1q"));
		assertEquals(0, StringUtil.indexOfChars(s, "1q", 0));
		assertEquals(5, StringUtil.indexOfChars(s, "1q", 1));
		assertEquals(0, StringUtil.indexOfChars(s, "1q", -11));
		assertEquals(-1, StringUtil.indexOfChars(s, "1q", 200));
		assertEquals(1, StringUtil.indexOfChars(s, "q2"));
		assertEquals(5, StringUtil.indexOfChars(s, "yq"));
	}

	public void testEquals() {
		assertTrue(StringUtil.equals("1", "1"));
		assertFalse(StringUtil.equals("1", null));
		assertFalse(StringUtil.equals(null, "2"));
		assertTrue(StringUtil.equals((String)null, null));

		assertEquals(2, StringUtil.equalsOne("src", new String[] {"123", null, "src"}));
		assertEquals(-1, StringUtil.equalsOne("src", new String[] {"123", null, "Src"}));

		assertEquals(2, StringUtil.equalsOneIgnoreCase("sRc", new String[] {"123", null, "Src"}));
		assertEquals(-1, StringUtil.equalsOneIgnoreCase("sRc", new String[] {"123", null, "Dsrc"}));
	}

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

	public void testToString() {
		assertEquals("aaa", StringUtil.toString("aaa"));
		assertEquals("173", StringUtil.toString(Integer.valueOf(173)));
		assertNull(StringUtil.toString(null));
		assertEquals("", StringUtil.toSafeString(null));
		assertEquals("3", StringUtil.toSafeString(Long.valueOf(3)));

		String[] arr = StringUtil.toStringArray("123, 234");
		assertEquals("123", arr[0]);
		assertEquals(" 234", arr[1]);
	}

	public void testCapitalize() {
		assertEquals("Foo", StringUtil.capitalize("foo"));
		assertEquals("Foo", StringUtil.capitalize("Foo"));
		assertEquals("", StringUtil.capitalize(""));

		assertEquals("foo", StringUtil.uncapitalize("foo"));
		assertEquals("foo", StringUtil.uncapitalize("Foo"));
		assertEquals("", StringUtil.uncapitalize(""));
	}

	public void testTruncate() {
		assertEquals("fo", StringUtil.truncate("foo", 2));
		assertEquals("f", StringUtil.truncate("foo", 1));
		assertEquals("", StringUtil.truncate("foo", 0));
		assertEquals("foo", StringUtil.truncate("foo", 4));
	}

	public void testStartWith() {
		assertTrue(StringUtil.startsWithChar("asd", 'a'));
		assertFalse(StringUtil.startsWithChar("asd", 's'));
		assertFalse(StringUtil.startsWithChar("", 'a'));

		assertTrue(StringUtil.endsWithChar("asd", 'd'));
		assertFalse(StringUtil.endsWithChar("asd", 's'));
		assertFalse(StringUtil.endsWithChar("", 'd'));

		assertEquals(3, StringUtil.startsWithOne("qwe123", new String[] {"Qwe", null, ".", "qwe"}));
		assertEquals(-1, StringUtil.startsWithOne("qwe123", new String[] {"Qwe", null, ".", "we"}));
		assertEquals(0, StringUtil.startsWithOneIgnoreCase("qwe123", new String[] {"Qwe", null, ".", "qwe"}));
		assertEquals(-1, StringUtil.startsWithOneIgnoreCase("qwe123", new String[] {"we", null, ".", "we"}));

		assertEquals(3, StringUtil.endsWithOne("qwezxc", new String[] {"Zxc", null, ".", "zxc"}));
		assertEquals(-1, StringUtil.endsWithOne("qwezxc", new String[] {"Zxc", null, ".", "zx"}));
		assertEquals(0, StringUtil.endsWithOneIgnoreCase("qweZXC", new String[] {"Zxc", null, ".", "zxc"}));
		assertEquals(-1, StringUtil.endsWithOneIgnoreCase("qweZXC", new String[] {"zx", null, ".", "zx"}));
	}


	public void testStrip() {
		assertEquals("we", StringUtil.stripLeadingChar("qwe", 'q'));
		assertEquals("qwe", StringUtil.stripLeadingChar("qwe", '4'));
		assertEquals("qw", StringUtil.stripTrailingChar("qwe", 'e'));
		assertEquals("qwe", StringUtil.stripTrailingChar("qwe", '4'));
	}

	public void testCrop() {
		assertEquals("123", StringUtil.crop("123"));
		assertEquals(" ", StringUtil.crop(" "));
		assertNull(StringUtil.crop(""));

		String[] s = new String[] {" ", null, ""};
		StringUtil.cropAll(s);
		assertEquals(" ", s[0]);
		assertNull(s[1]);
		assertNull(s[2]);
	}

	public void testJoin() {
		assertEquals("123", StringUtil.join("1", "2", "3"));
		assertEquals("13", StringUtil.join("1", "", "3"));
		assertEquals("1null3", StringUtil.join("1", null, "3"));
	}

	public void testCharset() throws UnsupportedEncodingException {
		assertEquals("123", StringUtil.convertCharset("123", ISO_8859_1, UTF_8));
		String s = StringUtil.convertCharset("\250\275", UTF_8, ISO_8859_1);
		assertEquals(4, s.length());
		assertEquals(194, s.charAt(0));
		assertEquals(168, s.charAt(1));
		assertEquals(194, s.charAt(2));
		assertEquals(189, s.charAt(3));
	}

	public void testIsCharAt() {
		assertTrue(StringUtil.isCharAtEqual("123", 0, '1'));
		assertTrue(StringUtil.isCharAtEqual("123", 1, '2'));
		assertTrue(StringUtil.isCharAtEqual("123", 2, '3'));
		assertFalse(StringUtil.isCharAtEqual("123", 0, '3'));
		assertFalse(StringUtil.isCharAtEqual("123", -1, '3'));
		assertFalse(StringUtil.isCharAtEqual("123", 5, '3'));
	}

	public void testEscape() {
		assertFalse(StringUtil.isCharAtEscaped("1\\23", 1, '\\'));
		assertTrue(StringUtil.isCharAtEscaped("1\\23", 2, '\\'));
		assertFalse(StringUtil.isCharAtEscaped("1\\23", 3, '\\'));
		assertFalse(StringUtil.isCharAtEscaped("1\\23", 0, '\\'));

		assertEquals(4, StringUtil.indexOfUnescapedChar("1\\23244", '2', '\\'));
		assertEquals(-1, StringUtil.indexOfUnescapedChar("1\\23244", '2', '\\', 6));
		assertEquals(-1, StringUtil.indexOfUnescapedChar("1\\23", '2', '\\'));
	}

	public void testRepeat() {
		assertEquals("1212", StringUtil.repeat("12", 2));
		assertEquals("xxxx", StringUtil.repeat('x', 4));
	}

	public void testReverse() {
		assertEquals("12345", StringUtil.reverse("54321"));
	}

	public void testMaxCommonPrefix() {
		assertEquals("", StringUtil.maxCommonPrefix("qwe", "asd"));
		assertEquals("1", StringUtil.maxCommonPrefix("1qwe", "1asd"));
		assertEquals("123", StringUtil.maxCommonPrefix("123", "123"));
		assertEquals("123", StringUtil.maxCommonPrefix("123456", "123"));
	}

	public void testCamelCase() {
		assertEquals("oneTwoThree", StringUtil.wordsToCamelCase("one two   three"));
		assertEquals("OneTwoThree", StringUtil.wordsToCamelCase("One two three"));
		assertEquals("oneTwo.Three", StringUtil.wordsToCamelCase("one two. three"));
		assertEquals("OneTwoThree", StringUtil.wordsToCamelCase("One-two-three", '-'));

		assertEquals("One two three", StringUtil.camelCaseToWords("OneTwoThree"));
		assertEquals("one two three", StringUtil.camelCaseToWords("oneTwoThree"));
		assertEquals("one-two-three", StringUtil.camelCaseToWords("oneTwoThree", '-'));
		assertEquals("one. two. three", StringUtil.camelCaseToWords("one.Two.Three"));
	}

	public void testJavaEscapes() {
		String from = "\r\t\b\f\n\\\"asd\u0111q\u0173aa\u0ABC\u0abc";
		String to = "\\r\\t\\b\\f\\n\\\\\\\"asd\\u0111q\\u0173aa\\u0abc\\u0abc";

		assertEquals(to, StringUtil.escapeJava(from));
		assertEquals(from, StringUtil.unescapeJava(to));
	}

	public void testFindCommonPrefix() {
		assertEquals("", StringUtil.findCommonPrefix("123", "234"));
		assertEquals("123", StringUtil.findCommonPrefix("123"));
		assertEquals("12", StringUtil.findCommonPrefix("123xxx", "125", "12xxxxx"));
		assertEquals("1", StringUtil.findCommonPrefix("123xxx", "1", "12xxxxx"));
		assertEquals("", StringUtil.findCommonPrefix("", "1", "12xxxxx"));
		assertEquals("123", StringUtil.findCommonPrefix("123", "123", "123"));
	}

	public void testShorten() {
		assertEquals("Long...", StringUtil.shorten("Long long sentence", 8, "..."));
		assertEquals("Longl...", StringUtil.shorten("Longlong sentence", 8, "..."));
	}
	
	public void testCompressChars() {
		assertEquals("1 2 3", StringUtil.compressChars("1   2    3", ' '));
		assertEquals("1 2 3", StringUtil.compressChars("1 2 3", ' '));
	}
}
