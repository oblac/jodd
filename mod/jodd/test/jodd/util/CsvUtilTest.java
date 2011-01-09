// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class CsvUtilTest extends TestCase {

	public void testToCsv() {
		assertEquals("a", CsvUtil.toCsvString("a"));
		assertEquals("a,b", CsvUtil.toCsvString("a", "b"));
		assertEquals("a,b,", CsvUtil.toCsvString("a", "b", ""));
		assertEquals("a,\" b \"", CsvUtil.toCsvString("a", " b "));
		assertEquals("a,b,\"jo,e\"", CsvUtil.toCsvString("a", "b", "jo,e"));
		assertEquals("a,b,\"\"\"some\"\"r\"", CsvUtil.toCsvString("a", "b", "\"some\"r"));
		assertEquals("1997,Ford,E350,\"Super, luxurious truck\"", CsvUtil.toCsvString("1997", "Ford", "E350", "Super, luxurious truck"));
		assertEquals("1997,Ford,E350,\"Super \"\"luxurious\"\" truck\"", CsvUtil.toCsvString("1997", "Ford", "E350", "Super \"luxurious\" truck"));
		assertEquals("1,,2", CsvUtil.toCsvString(Integer.valueOf(1), null, Integer.valueOf(2)));
		assertEquals("\"a\nb\"", CsvUtil.toCsvString("a\nb"));
	}

	public void testFromCsv() {
		assertStringArray(CsvUtil.toStringArray("a"), "a");
		assertStringArray(CsvUtil.toStringArray("a,b"), "a","b");
		assertStringArray(CsvUtil.toStringArray("a, b "), "a"," b ");
		assertStringArray(CsvUtil.toStringArray("a,\" b \""), "a"," b ");
		assertStringArray(CsvUtil.toStringArray("a,b,"), "a","b", "");
		assertStringArray(CsvUtil.toStringArray("a,b,\"jo,e\""), "a","b", "jo,e");
		assertStringArray(CsvUtil.toStringArray("a,b,\"\"\"some\"\"r\""), "a","b", "\"some\"r");
		assertStringArray(CsvUtil.toStringArray("1997,Ford,E350,\"Super, luxurious truck\""), "1997", "Ford", "E350", "Super, luxurious truck");
		assertStringArray(CsvUtil.toStringArray("1997,Ford,E350,\"Super \"\"luxurious\"\" truck\""), "1997", "Ford", "E350", "Super \"luxurious\" truck");
		assertStringArray(CsvUtil.toStringArray("\"a\nb\""), "a\nb");
	}


	void assertStringArray(String[] result, String... expected) {
		assertEquals(expected.length, result.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], result[i]);
		}
	}


}
