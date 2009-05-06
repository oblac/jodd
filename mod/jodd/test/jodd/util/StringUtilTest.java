// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

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

		src = StringUtil.replaceLast(src, "55", "11");
		assertEquals("55221144", src);
		src = StringUtil.replaceLast(src, "55", "11");
		assertEquals("11221144", src);
		src = StringUtil.replaceLast(src, "55", "11");
		assertEquals("11221144", src);
	}


/*
	public static void main(String[] args) {
		String s = "123";
		System.out.println(s.indexOf("", -1));			//0
		System.out.println(s.indexOf("", 1));			//1
		System.out.println(s.indexOf("", 10));			//3
		System.out.println(s.lastIndexOf("", 10));		//3
		System.out.println(s.lastIndexOf("", 2));		//2
		System.out.println(s.lastIndexOf("", -1));		//-1
		System.out.println(s.lastIndexOf("", -10));		//-1

		System.out.println(s.indexOf("1", -1));			//0
		System.out.println(s.lastIndexOf("1", 10));		//0

		System.out.println(s.lastIndexOf("x", 10));
		System.out.println(s.indexOf("x", -1));
		System.out.println(s.lastIndexOf('x', 10));
		System.out.println(s.indexOf('x', -1));
	}
*/


	public void testIndexOf() {
		String src = "1234567890qWeRtY";

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
	}


	public void testEscapeQuotes() {
		String s = "ccc'aaa";
		assertEquals("ccc\\'aaa", StringUtil.replace(s, "'", "\\'"));

		s = "ccc\"aaa";
		assertEquals("ccc\\\"aaa", StringUtil.replace(s, "\"", "\\\""));
	}


	public void testTrim() {
		assertEquals("123", StringUtil.trimNonEmpty(" 123 "));
		assertEquals("123", StringUtil.trimNonEmpty("123"));
		assertNull(StringUtil.trimNonEmpty(""));
		assertNull(StringUtil.trimNonEmpty("     "));
	}


	void checkInts(int x, int y, int z, int w, int[] arr) {
		assertNotNull(arr);
		assertEquals(x, arr[0]);
		assertEquals(y, arr[1]);
		assertEquals(z, arr[2]);
		assertEquals(w, arr[3]);
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
		assertEquals("1", StringUtil.cutFromIndexOf("123", "2"));
		assertEquals("123", StringUtil.cutFromIndexOf("123", "4"));
		assertEquals("", StringUtil.cutFromIndexOf("123", "1"));

		assertEquals("23", StringUtil.cutToIndexOf("123", "2"));
		assertEquals("3", StringUtil.cutToIndexOf("123", "3"));
		assertEquals("123", StringUtil.cutToIndexOf("123", "4"));
		assertEquals("123", StringUtil.cutToIndexOf("123", "1"));

		assertEquals("qwe", StringUtil.cutPreffix("preqwe", "pre"));
		assertEquals("preqwe", StringUtil.cutPreffix("preqwe", "pre2"));

		assertEquals("qwe", StringUtil.cutSuffix("qwesuf", "suf"));
		assertEquals("qwesuf", StringUtil.cutPreffix("qwesuf", "suf2"));

		assertEquals("qwe", StringUtil.cutSurrounding("preqwesuf", "pre", "suf"));
		assertEquals("qwe", StringUtil.cutSurrounding("qwesuf", "pre", "suf"));
		assertEquals("qwe", StringUtil.cutSurrounding("preqwe", "pre", "suf"));
		assertEquals("qwe", StringUtil.cutSurrounding("qwe", "pre", "suf"));


		assertEquals("qwe", StringUtil.cutLastWord("qwe"));
		assertEquals("qwe", StringUtil.cutLastWord("qweWord"));
		assertEquals("qweWord", StringUtil.cutLastWord("qweWordWord2"));
		assertEquals("", StringUtil.cutLastWord("Qwe"));
		assertEquals("qwe", StringUtil.cutLastWordNotFirst("qwe"));
		assertEquals("qwe", StringUtil.cutLastWordNotFirst("qweWord"));
		assertEquals("qweWord", StringUtil.cutLastWordNotFirst("qweWordWord2"));
		assertEquals("Qwe", StringUtil.cutLastWordNotFirst("Qwe"));

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
}
