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
package jodd.datetime.format;

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Iso8601JdtFormatterTest {

	@Test
	public void testFindPattern() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		assertTrue(formatter.findPattern("YYYY".toCharArray(), 0) > -1);
		assertFalse(formatter.findPattern("YYY".toCharArray(), 0) > -1);
		assertFalse(formatter.findPattern(" YYYY".toCharArray(), 0) > -1);
		assertTrue(formatter.findPattern(" YYYY".toCharArray(), 1) > -1);

		assertArrayEquals(formatter.patterns[formatter.findPattern("DDD".toCharArray(), 0)], "DDD".toCharArray());
		assertArrayEquals(formatter.patterns[formatter.findPattern("DD".toCharArray(), 0)], "DD".toCharArray());
		assertArrayEquals(formatter.patterns[formatter.findPattern("D".toCharArray(), 0)], "D".toCharArray());
	}

	@Test
	public void testParseWithDelimiters() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		assertEquals(new DateTimeStamp(123, 1, 2), formatter.parse("123-1-2", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(123, 11, 12), formatter.parse("123-11-12", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(1234, 11, 12), formatter.parse("1234-11-12", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(12345, 11, 12), formatter.parse("12345-11-12", "YYYY-MM-DD"));
		assertEquals(new DateTimeStamp(12345, 11, 12), formatter.parse("12345 - 11 - 12", "YYYY-MM-DD"));
	}

	@Test
	public void testParseWithoutDelimiters() {
		Iso8601JdtFormatter formatter = new Iso8601JdtFormatter();

		assertEquals(new DateTimeStamp(123, 1, 2), formatter.parse("01230102", "YYYYMMDD"));
		assertEquals(new DateTimeStamp(1234, 11, 12), formatter.parse("12341112", "YYYYMMDD"));
	}


	@Test
	public void testParseBackAndFort() {
		String timePattern = "YYYYMMDDhhmmssmss";

		JDateTime jdt = new JDateTime();

		String format = jdt.toString(timePattern);

		assertEquals(jdt, new JDateTime().parse(format, timePattern));
	}


	@Test
	public void testParse_423() {
		JDateTime jdt;

		jdt = new JDateTime("20170808 100808", "YYYYMMDD hhmmss");

		assertEquals(2017, jdt.getYear());
		assertEquals(8, jdt.getMonth());

		jdt = new JDateTime("20170808_100808", "YYYYMMDD_hhmmss");

		assertEquals(2017, jdt.getYear());
		assertEquals(8, jdt.getMonth());
	}


}
