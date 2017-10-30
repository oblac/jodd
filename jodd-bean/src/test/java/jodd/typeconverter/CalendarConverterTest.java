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
import jodd.typeconverter.impl.CalendarConverter;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CalendarConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	CalendarConverter calendarConverter = new CalendarConverter();

	@Test
	void testNull() {
		assertNull(calendarConverter.convert(null));
	}

	@Test
	void testCalendar2Calendar() {
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(time);
		Calendar calendar = calendarConverter.convert(calendar2);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	void testDate2Calendar() {
		Date date = new Date(time);
		Calendar calendar = calendarConverter.convert(date);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	void testTimestamp2Calendar() {
		Timestamp timestamp = new Timestamp(time);
		Calendar calendar = calendarConverter.convert(timestamp);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	void testSqlDate2Calendar() {
		java.sql.Date date = new java.sql.Date(time);
		Calendar calendar = calendarConverter.convert(date);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	void testSqlTime2Calendar() {
		java.sql.Time sqltime = new java.sql.Time(time);
		Calendar calendar = calendarConverter.convert(sqltime);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	void testJDateTime2Calendar() {
		JDateTime jdt = new JDateTime(time);
		Calendar calendar = calendarConverter.convert(jdt);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	void testCalendarDate() {
		JDateTime jdt = new JDateTime();

		CalendarConverter calendarConverter = new CalendarConverter();
		Calendar gc = calendarConverter.convert(jdt);
		DateFormat df = new SimpleDateFormat();
		assertEquals(df.format(gc.getTime()), df.format(Converter.get().toDate(jdt)));
	}
}
