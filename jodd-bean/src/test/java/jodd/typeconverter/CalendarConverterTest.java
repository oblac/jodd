// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.CalendarConverter;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CalendarConverterTest {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	CalendarConverter calendarConverter = new CalendarConverter();

	@Test
	public void testNull() {
		assertNull(calendarConverter.convert(null));
	}

	@Test
	public void testCalendar2Calendar() {
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(time);
		Calendar calendar = calendarConverter.convert(calendar2);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	public void testDate2Calendar() {
		Date date = new Date(time);
		Calendar calendar = calendarConverter.convert(date);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	public void testTimestamp2Calendar() {
		Timestamp timestamp = new Timestamp(time);
		Calendar calendar = calendarConverter.convert(timestamp);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	public void testSqlDate2Calendar() {
		java.sql.Date date = new java.sql.Date(time);
		Calendar calendar = calendarConverter.convert(date);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	public void testSqlTime2Calendar() {
		java.sql.Time sqltime = new java.sql.Time(time);
		Calendar calendar = calendarConverter.convert(sqltime);
		assertEquals(time, calendar.getTimeInMillis());
	}

	@Test
	public void testJDateTime2Calendar() {
		JDateTime jdt = new JDateTime(time);
		Calendar calendar = calendarConverter.convert(jdt);
		assertEquals(time, calendar.getTimeInMillis());
	}
}
