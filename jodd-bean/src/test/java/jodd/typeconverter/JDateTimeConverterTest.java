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
import jodd.typeconverter.impl.JDateTimeConverter;
import jodd.typeconverter.impl.SqlDateConverter;
import jodd.typeconverter.impl.SqlTimestampConverter;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JDateTimeConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	JDateTimeConverter jDateTimeConverter = new JDateTimeConverter();

	@Test
	public void testNull() {
		assertNull(jDateTimeConverter.convert(null));
	}

	@Test
	public void testCalendar2JDateTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		JDateTime jdt = jDateTimeConverter.convert(calendar);
		assertEquals(time, jdt.getTimeInMillis());
	}

	@Test
	public void testDate2JDateTime() {
		Date date = new Date(time);
		JDateTime jdt = jDateTimeConverter.convert(date);
		assertEquals(time, jdt.getTimeInMillis());
	}

	@Test
	public void testTimestamp2JDateTime() {
		Timestamp timestamp = new Timestamp(time);
		JDateTime jdt = jDateTimeConverter.convert(timestamp);
		assertEquals(time, jdt.getTimeInMillis());
	}

	@Test
	public void testSqlDate2JDateTime() {
		java.sql.Date date = new java.sql.Date(time);
		JDateTime jdt = jDateTimeConverter.convert(date);
		assertEquals(time, jdt.getTimeInMillis());
	}

	@Test
	public void testSqlTime2JDateTime() {
		Time sqltime = new Time(time);
		JDateTime jdt = jDateTimeConverter.convert(sqltime);
		assertEquals(time, jdt.getTimeInMillis());
	}

	@Test
	public void testJDateTime2JDateTime() {
		JDateTime jdt2 = new JDateTime(time);
		JDateTime jdt = jDateTimeConverter.convert(jdt2);
		assertEquals(time, jdt.getTimeInMillis());
	}


	@Test
	public void testConversion() {
		assertNull(jDateTimeConverter.convert(null));

		assertEquals(new JDateTime(2010, 10, 10), jDateTimeConverter.convert(new JDateTime(2010, 10, 10)));
		assertEquals(new JDateTime(123456), jDateTimeConverter.convert(Integer.valueOf(123456)));
		assertEquals(new JDateTime(2010, 10, 20, 10, 11, 12, 456), jDateTimeConverter.convert("2010-10-20 10:11:12.456"));
	}

	@Test
	public void testLoadFromStoreTo() {
		Calendar c = Calendar.getInstance();
		c.set(2001, 0, 1, 2, 3, 4);
		c.set(Calendar.MILLISECOND, 500);
		JDateTime jdt = Convert.toJDateTime(c);
		assertEquals("2001-01-01 02:03:04.500", jdt.toString());
		Calendar c1 = Convert.toCalendar(jdt);
		assertEquals(2001, c1.get(Calendar.YEAR));
		assertEquals(0, c1.get(Calendar.MONTH));
		assertEquals(1, c1.get(Calendar.DAY_OF_MONTH));
		assertEquals(2, c1.get(Calendar.HOUR_OF_DAY));
		assertEquals(3, c1.get(Calendar.MINUTE));
		assertEquals(4, c1.get(Calendar.SECOND));
		assertEquals(500, c1.get(Calendar.MILLISECOND));


		GregorianCalendar gc = new GregorianCalendar(2002, 5, 2, 3, 4, 5);
		gc.set(GregorianCalendar.MILLISECOND, 600);
		jdt = Convert.toJDateTime(gc);
		assertEquals("2002-06-02 03:04:05.600", jdt.toString());
		GregorianCalendar gc1 = (GregorianCalendar) Convert.toCalendar(jdt);

		assertEquals(2002, gc1.get(GregorianCalendar.YEAR));
		assertEquals(5, gc1.get(GregorianCalendar.MONTH));
		assertEquals(2, gc1.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(3, gc1.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(4, gc1.get(GregorianCalendar.MINUTE));
		assertEquals(5, gc1.get(GregorianCalendar.SECOND));
		assertEquals(600, gc1.get(GregorianCalendar.MILLISECOND));


		Date d = new Date(101, 2, 3, 4, 5, 6);
		jdt = Convert.toJDateTime(d);
		assertEquals("2001-03-03 04:05:06.000", jdt.toString());
		Date d2 = Convert.toDate(jdt);
		assertEquals(101, d2.getYear());
		assertEquals(2, d2.getMonth());
		assertEquals(3, d2.getDate());
		assertEquals(4, d2.getHours());
		assertEquals(5, d2.getMinutes());
		assertEquals(6, d2.getSeconds());


		JDateTime gt_new = new JDateTime(2003, 6, 5, 4, 3, 2, 100);
		jdt.setJulianDate(gt_new.getJulianDate());
		assertEquals("2003-06-05 04:03:02.100", jdt.toString());
		JDateTime gt2 = jdt.clone();
		assertEquals(2003, gt2.getYear());
		assertEquals(6, gt2.getMonth());
		assertEquals(5, gt2.getDay());
		assertEquals(4, gt2.getHour());
		assertEquals(3, gt2.getMinute());
		assertEquals(2, gt2.getSecond());
		assertEquals(100, gt2.getMillisecond());


		java.sql.Date sd = new java.sql.Date(123, 4, 5);
		jdt = Convert.toJDateTime(sd);
		assertEquals("2023-05-05 00:00:00.000", jdt.toString());
		java.sql.Date sd2 = new java.sql.Date(1, 2, 3);
		SqlDateConverter sqlDateConverter = new SqlDateConverter();
		sd2 = sqlDateConverter.convert(jdt);
		assertEquals(123, sd2.getYear());
		assertEquals(4, sd2.getMonth());
		assertEquals(5, sd2.getDate());


		Timestamp st = new Timestamp(123, 4, 5, 6, 7, 8, 500000000);
		jdt = Convert.toJDateTime(st);
		assertEquals("2023-05-05 06:07:08.500", jdt.toString());
		SqlTimestampConverter sqlTimestampConverter = new SqlTimestampConverter();
		Timestamp st2 = sqlTimestampConverter.convert(jdt);
		assertEquals(123, st2.getYear());
		assertEquals(4, st2.getMonth());
		assertEquals(5, st2.getDate());
		assertEquals(6, st2.getHours());
		assertEquals(7, st2.getMinutes());
		assertEquals(8, st2.getSeconds());
		assertEquals(500, st2.getNanos() / 1000000);
	}

}
