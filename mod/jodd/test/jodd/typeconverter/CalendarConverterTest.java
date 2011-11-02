// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.CalendarConverter;
import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class CalendarConverterTest extends TestCase {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	public void testNull() {
		assertNull(CalendarConverter.valueOf(null));
	}

	public void testCalendar2Calendar() {
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(time);
		Calendar calendar = CalendarConverter.valueOf(calendar2);
		assertEquals(time, calendar.getTimeInMillis());
	}

	public void testDate2Calendar() {
		Date date = new Date(time);
		Calendar calendar = CalendarConverter.valueOf(date);
		assertEquals(time, calendar.getTimeInMillis());
	}

	public void testTimestamp2Calendar() {
		Timestamp timestamp = new Timestamp(time);
		Calendar calendar = CalendarConverter.valueOf(timestamp);
		assertEquals(time, calendar.getTimeInMillis());
	}

	public void testSqlDate2Calendar() {
		java.sql.Date date = new java.sql.Date(time);
		Calendar calendar = CalendarConverter.valueOf(date);
		assertEquals(time, calendar.getTimeInMillis());
	}

	public void testSqlTime2Calendar() {
		java.sql.Time sqltime = new java.sql.Time(time);
		Calendar calendar = CalendarConverter.valueOf(sqltime);
		assertEquals(time, calendar.getTimeInMillis());
	}

	public void testJDateTime2Calendar() {
		JDateTime jdt = new JDateTime(time);
		Calendar calendar = CalendarConverter.valueOf(jdt);
		assertEquals(time, calendar.getTimeInMillis());
	}
}
