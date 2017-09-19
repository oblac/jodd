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
import jodd.typeconverter.impl.DateConverter;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DateConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	DateConverter dateConverter = new DateConverter();

	@Test
	public void testNull() {
		assertNull(dateConverter.convert(null));
	}

	@Test
	public void testCalendar2Date() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Date date = dateConverter.convert(calendar);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testDate2Date() {
		Date date2 = new Date(time);
		Date date = dateConverter.convert(date2);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testTimestamp2Date() {
		Timestamp timestamp = new Timestamp(time);
		Date date = dateConverter.convert(timestamp);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testSqlDate2Date() {
		java.sql.Date date2 = new java.sql.Date(time);
		Date date = dateConverter.convert(date2);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testSqlTime2Date() {
		Time sqltime = new Time(time);
		Date date = dateConverter.convert(sqltime);
		assertEquals(time, date.getTime());
	}

	@Test
	public void testJDateTime2Date() {
		JDateTime jdt = new JDateTime(time);
		Date date = dateConverter.convert(jdt);
		assertEquals(time, date.getTime());
	}


	@Test
	@SuppressWarnings({"deprecation"})
	public void testConversion() {
		assertNull(dateConverter.convert(null));

		assertEquals(new Date(885858), dateConverter.convert("885858"));
		assertEquals(new Date(123), dateConverter.convert(Integer.valueOf(123)));

		Date date = new Date(111, 0, 1);
		assertEquals(date, dateConverter.convert("2011-01-01"));

		date = new Date(111, 0, 1, 10, 59, 55);
		assertEquals(date, dateConverter.convert("2011-01-01 10:59:55"));
	}
}

