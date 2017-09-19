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
import jodd.typeconverter.impl.SqlTimestampConverter;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SqlTimestampConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	SqlTimestampConverter sqlTimestampConverter = new SqlTimestampConverter();

	@Test
	public void testNull() {
		assertNull(sqlTimestampConverter.convert(null));
	}

	@Test
	public void testCalendar2Timestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Timestamp timestamp = sqlTimestampConverter.convert(calendar);
		assertEquals(time, timestamp.getTime());
	}

	@Test
	public void testDate2Timestamp() {
		Date date = new Date(time);
		Timestamp timestamp = sqlTimestampConverter.convert(date);
		assertEquals(time, timestamp.getTime());
	}

	@Test
	public void testTimestamp2Timestamp() {
		Timestamp timestamp2 = new Timestamp(time);
		Timestamp timestamp = sqlTimestampConverter.convert(timestamp2);
		assertEquals(time, timestamp.getTime());
	}

	@Test
	public void testSqlDate2Timestamp() {
		java.sql.Date date = new java.sql.Date(time);
		Timestamp timestamp = sqlTimestampConverter.convert(date);
		assertEquals(time, timestamp.getTime());
	}

	@Test
	public void testSqlTime2Timestamp() {
		Time sqltime = new Time(time);
		Timestamp timestamp = sqlTimestampConverter.convert(sqltime);
		assertEquals(time, timestamp.getTime());
	}

	@Test
	public void testJDateTime2Timestamp() {
		JDateTime jdt = new JDateTime(time);
		Timestamp timestamp = sqlTimestampConverter.convert(jdt);
		assertEquals(time, timestamp.getTime());
	}

	@Test
	public void testConversion() {
		assertNull(sqlTimestampConverter.convert(null));

		assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), sqlTimestampConverter.convert(Timestamp.valueOf("2011-01-01 00:01:02")));
		assertEquals(new Timestamp(60), sqlTimestampConverter.convert(Integer.valueOf(60)));
		assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), sqlTimestampConverter.convert("2011-01-01 00:01:02"));
		assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), sqlTimestampConverter.convert("     2011-01-01 00:01:02       "));

		try {
			sqlTimestampConverter.convert("00:01");
			fail("error");
		} catch (TypeConversionException ignore) {
		}

		try {
			sqlTimestampConverter.convert("a");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
	}
}
