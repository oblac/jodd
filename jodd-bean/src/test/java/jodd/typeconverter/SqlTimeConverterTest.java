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
import jodd.typeconverter.impl.SqlTimeConverter;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SqlTimeConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	SqlTimeConverter sqlTimeConverter = new SqlTimeConverter();

	@Test
	public void testNull() {
		assertNull(sqlTimeConverter.convert(null));
	}

	@Test
	public void testCalendar2Timestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Time sqltime = sqlTimeConverter.convert(calendar);
		assertEquals(time, sqltime.getTime());
	}

	@Test
	public void testDate2Timestamp() {
		Date date = new Date(time);
		Time sqltime = sqlTimeConverter.convert(date);
		assertEquals(time, sqltime.getTime());
	}

	@Test
	public void testTimestamp2Timestamp() {
		Timestamp timestamp2 = new Timestamp(time);
		Time sqltime = sqlTimeConverter.convert(timestamp2);
		assertEquals(time, sqltime.getTime());
	}

	@Test
	public void testSqlDate2Timestamp() {
		java.sql.Date date = new java.sql.Date(time);
		Time sqltime = sqlTimeConverter.convert(date);
		assertEquals(time, sqltime.getTime());
	}

	@Test
	public void testSqlTime2Timestamp() {
		Time sqltime2 = new Time(time);
		Time sqltime = sqlTimeConverter.convert(sqltime2);
		assertEquals(time, sqltime.getTime());
	}

	@Test
	public void testJDateTime2Timestamp() {
		JDateTime jdt = new JDateTime(time);
		Time sqltime = sqlTimeConverter.convert(jdt);
		assertEquals(time, sqltime.getTime());
	}

	@Test
	public void testConversion() {
		assertNull(sqlTimeConverter.convert(null));

		assertEquals(Time.valueOf("00:01:02"), sqlTimeConverter.convert(Time.valueOf("00:01:02")));
		assertEquals(new Time(60), sqlTimeConverter.convert(Integer.valueOf(60)));
		assertEquals(Time.valueOf("00:01:02"), sqlTimeConverter.convert("00:01:02"));
		assertEquals(Time.valueOf("00:01:02"), sqlTimeConverter.convert("       00:01:02    "));

		try {
			sqlTimeConverter.convert("00:01");
			fail("error");
		} catch (TypeConversionException ignore) {
		}

		try {
			sqlTimeConverter.convert("a");
			fail("error");
		} catch (TypeConversionException ignore) {
		}
	}
}
