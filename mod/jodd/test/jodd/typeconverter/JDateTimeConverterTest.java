// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.JDateTimeConverter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class JDateTimeConverterTest extends BaseTestCase {
	
	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	public void testNull() {
		assertNull(JDateTimeConverter.valueOf(null));
	}

	public void testCalendar2JDateTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		JDateTime jdt = JDateTimeConverter.valueOf(calendar);
		assertEquals(time, jdt.getTimeInMillis());
	}

	public void testDate2JDateTime() {
		Date date = new Date(time);
		JDateTime jdt = JDateTimeConverter.valueOf(date);
		assertEquals(time, jdt.getTimeInMillis());
	}

	public void testTimestamp2JDateTime() {
		Timestamp timestamp = new Timestamp(time);
		JDateTime jdt = JDateTimeConverter.valueOf(timestamp);
		assertEquals(time, jdt.getTimeInMillis());
	}

	public void testSqlDate2JDateTime () {
		java.sql.Date date = new java.sql.Date(time);
		JDateTime jdt = JDateTimeConverter.valueOf(date);
		assertEquals(time, jdt.getTimeInMillis());
	}

	public void testSqlTime2JDateTime () {
		Time sqltime = new Time(time);
		JDateTime jdt = JDateTimeConverter.valueOf(sqltime);
		assertEquals(time, jdt.getTimeInMillis());
	}

	public void testJDateTime2JDateTime () {
		JDateTime jdt2 = new JDateTime(time);
		JDateTime jdt = JDateTimeConverter.valueOf(jdt2);
		assertEquals(time, jdt.getTimeInMillis());
	}
	

	public void testConversion() {
		assertNull(JDateTimeConverter.valueOf(null));

		assertEquals(new JDateTime(2010, 10, 10), JDateTimeConverter.valueOf(new JDateTime(2010, 10, 10)));
		assertEquals(new JDateTime(123456), JDateTimeConverter.valueOf(Integer.valueOf(123456)));
		assertEquals(new JDateTime(2010, 10, 20, 10, 11, 12, 456), JDateTimeConverter.valueOf("2010-10-20 10:11:12.456"));
	}
}
