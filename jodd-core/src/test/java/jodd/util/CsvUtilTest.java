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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvUtilTest {

	@Test
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

	@Test
	public void testFromCsv() {
		assertStringArray(CsvUtil.toStringArray("a"), "a");
		assertStringArray(CsvUtil.toStringArray("a,b"), "a", "b");
		assertStringArray(CsvUtil.toStringArray("a, b "), "a", " b ");
		assertStringArray(CsvUtil.toStringArray("a,\" b \""), "a", " b ");
		assertStringArray(CsvUtil.toStringArray("a,b,"), "a", "b", "");
		assertStringArray(CsvUtil.toStringArray("a,b,\"jo,e\""), "a", "b", "jo,e");
		assertStringArray(CsvUtil.toStringArray("a,b,\"\"\"some\"\"r\""), "a", "b", "\"some\"r");
		assertStringArray(CsvUtil.toStringArray("1997,Ford,E350,\"Super, luxurious truck\""), "1997", "Ford", "E350", "Super, luxurious truck");
		assertStringArray(CsvUtil.toStringArray("1997,Ford,E350,\"Super \"\"luxurious\"\" truck\""), "1997", "Ford", "E350", "Super \"luxurious\" truck");
		assertStringArray(CsvUtil.toStringArray("\"a\nb\""), "a\nb");
		assertStringArray(CsvUtil.toStringArray("a,,b"), "a", "", "b");
	}


	void assertStringArray(String[] result, String... expected) {
		assertEquals(expected.length, result.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], result[i]);
		}
	}


}
