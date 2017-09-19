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

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.SqlDateConverter;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDateConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	SqlDateConverter sqlDateConverter = new SqlDateConverter();

	@Test
	public void testNull() {
		assertNull(sqlDateConverter.convert(null));
	}

	@Test
	public void testCalendar2SqlDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Date date = sqlDateConverter.convert(calendar);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testDate2SqlDate() {
		java.util.Date date2 = new java.util.Date(time);
		Date date = sqlDateConverter.convert(date2);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testTimestamp2SqlDate() {
		Timestamp timestamp = new Timestamp(time);
		Date date = sqlDateConverter.convert(timestamp);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testSqlDate2SqlDate() {
		Date date2 = new Date(time);
		Date date = sqlDateConverter.convert(date2);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testSqlTime2SqlDate() {
		Time sqltime = new Time(time);
		Date date = sqlDateConverter.convert(sqltime);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testJDateTime2SqlDate() {
		JDateTime jdt = new JDateTime(time);
		Date date = sqlDateConverter.convert(jdt);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testConversion() {
		assertNull(sqlDateConverter.convert(null));

		assertEquals(Date.valueOf("2011-01-01"), sqlDateConverter.convert(Date.valueOf("2011-01-01")));
		assertEquals(new Date(1111111), sqlDateConverter.convert(Integer.valueOf(1111111)));
		assertEquals(Date.valueOf("2011-01-01"), sqlDateConverter.convert("2011-01-01"));
		assertEquals(Date.valueOf("2011-01-01"), sqlDateConverter.convert("      2011-01-01       "));

		try {
			sqlDateConverter.convert("2011.01.01");
			fail("error");
		} catch (TypeConversionException ignore) {
		}

		try {
			sqlDateConverter.convert("a");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
	}
}
