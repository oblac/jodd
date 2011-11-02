// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.sql.Timestamp;

import jodd.typeconverter.Convert;
import jodd.typeconverter.impl.CalendarConverter;
import jodd.typeconverter.impl.DateConverter;
import jodd.typeconverter.impl.JDateTimeConverter;
import jodd.typeconverter.impl.SqlDateConverter;
import jodd.typeconverter.impl.SqlTimestampConverter;
import junit.framework.TestCase;

public class JDateTimeTest extends TestCase {

	public void testSetGetMillis() {
		JDateTime jdt = new JDateTime(2003, 2, 28, 23, 59, 59, 0);
		
		for (int i = 0; i < 1000; i++) {
			jdt.setMillisecond(i);
			assertEquals(i, jdt.getMillisecond());
		}
	}


	public void testSet999Millis() {

		JDateTime jdt = new JDateTime();

		jdt.set(2003, 2, 28, 23, 59, 59, 999);
		assertEquals("2003-02-28 23:59:59.999", jdt.toString());

		jdt.set(2003, 2, 28, 23, 59, 60, 0);
		assertEquals("2003-03-01 00:00:00.000", jdt.toString());

		// this used to be a problem
		jdt.set(2003, 2, 28, 23, 59, 59, 999);		// 12 fraction digits  - last working
		assertEquals("2003-02-28 23:59:59.999", jdt.toString());

	}


	public void testDaysInMonth() {
		JDateTime jdt = new JDateTime(2003, 1, 1);
		assertEquals(31, jdt.getMonthLength());
		assertEquals(28, jdt.getMonthLength(2));

		jdt = new JDateTime(2000, 1, 1);
		assertEquals(31, jdt.getMonthLength());
		assertEquals(29, jdt.getMonthLength(2));
	}


	public void testToString() {

		JDateTime jdt = new JDateTime(2003, 1, 1, 1, 1, 1, 1);
		assertEquals("2003-01-01 01:01:01.001", jdt.toString());

		jdt.set(200, 10, 10, 10, 10, 10, 12);
		assertEquals("0200-10-10 10:10:10.012", jdt.toString());

		jdt.set(2003, 10, 10, 10, 10, 10, 123);
		assertEquals("2003-10-10 10:10:10.123", jdt.toString());
	}

	public void testAddMonths() {
		GregorianCalendar gc = new GregorianCalendar(2003, 0, 31);
		gc.add(Calendar.MONTH, 1);
		assertEquals(1, gc.get(Calendar.MONTH));
		assertEquals(28, gc.get(Calendar.DAY_OF_MONTH));

		JDateTime gt = new JDateTime(2003, 1, 31);
		gt.setMonthFix(false);
		gt.addMonth(1);
		assertEquals("2003-03-03 00:00:00.000", gt.toString());

		gt.setDate(2003, 1, 31);
		gt.addMonth(1, true);
		assertEquals("2003-02-28 00:00:00.000", gt.toString());

		gt.setDate(2004, 1, 31);
		gt.addMonth(1);
		assertEquals("2004-03-02 00:00:00.000", gt.toString());

		gt.setDate(2004, 1, 31);
		gt.addMonth(1, true);
		assertEquals("2004-02-29 00:00:00.000", gt.toString());

		gt.setDate(2003, 1, 25);
		gt.add(0, 1, 6, true);
		assertEquals("2003-02-28 00:00:00.000", gt.toString());

		gt.setDate(2003, 1, 20);
		gt.add(0, 10, 11, true);
		assertEquals("2003-11-30 00:00:00.000", gt.toString());

		gt.setDate(2004, 2, 29);
		gt.addYear(1, true);
		assertEquals("2005-02-28 00:00:00.000", gt.toString());



		gt.setDate(2004, 2, 29);
		gt.addYear(-1, true);
		assertEquals("2003-02-28 00:00:00.000", gt.toString());

		gt.setDate(2003, 11, 31);		// == 2003-12-01
		gt.add(0, -8, -31, true);
		assertEquals("2003-02-28 00:00:00.000", gt.toString());
		gt.setDate(2003, 11, 31);
		gt.add(0, -8, -31, false);
		assertEquals("2003-03-01 00:00:00.000", gt.toString());

		gt.setDate(2004, 5, 31);
		gt.addMonth(-3, true);
		assertEquals("2004-02-29 00:00:00.000", gt.toString());
		gt.setDate(2003, 11, 31);
		gt.addMonth(-10, true);
		assertEquals("2003-02-01 00:00:00.000", gt.toString());

	}


	public void testMiscSetsGets() {
		JDateTime gt = new JDateTime(2003, 11, 26, 21, 8, 25, 173);
		
		gt.setYear(2002);
		assertEquals(2002, gt.getYear());

		gt.setMonth(10);
		assertEquals(10, gt.getMonth());
		
		gt.setDay(27);
		assertEquals(27, gt.getDay());

		gt.setHour(22);
		assertEquals(22, gt.getHour());

		gt.setMinute(8);
		assertEquals(8, gt.getMinute());

		gt.setSecond(24);
		assertEquals(24, gt.getSecond());
		assertEquals(173, gt.getMillisecond());

		gt.setSecond(25, 371);
		assertEquals(25, gt.getSecond());
		assertEquals(371, gt.getMillisecond());

		gt.setMillisecond(173);
		assertEquals(173, gt.getMillisecond());

		assertEquals("2002-10-27 22:08:25.173", gt.toString());

	}

	public void testLeapYears() {

		JDateTime gt = new JDateTime(1984, 2, 29);
		assertTrue(gt.isLeapYear());
		assertEquals("1984-02-29 00:00:00.000", gt.toString());

		gt.set(1985, 2, 29);
		assertFalse(gt.isLeapYear());
		assertEquals("1985-03-01 00:00:00.000", gt.toString());

		gt.set(2004, 2, 29);
		assertTrue(gt.isLeapYear());
		assertEquals("2004-02-29 00:00:00.000", gt.toString());

		gt.set(1900, 2, 29);
		assertFalse(gt.isLeapYear());			// not a leap year
		assertEquals("1900-03-01 00:00:00.000", gt.toString());

		gt.set(2000, 2, 29);
		assertTrue(gt.isLeapYear());			// a leap year
		assertEquals("2000-02-29 00:00:00.000", gt.toString());

		gt.set(1600, 2, 29);
		assertTrue(gt.isLeapYear());			// a leap year
		assertEquals("1600-02-29 00:00:00.000", gt.toString());

		for (int y = -4700; y < 5000; y++) {
			gt.set(y, 2, 29);
			assertEquals(31 + 29, gt.getDayOfYear());
			if (gt.isLeapYear()) {
				assertEquals(29, gt.getDay());
			} else {
				assertEquals(1, gt.getDay());
			}
		}
	}



	
	public void testLoadFromStoreTo() {
		Calendar c = Calendar.getInstance();
		c.set(2001, 0, 1, 2, 3, 4);
		c.set(Calendar.MILLISECOND, 500);
		JDateTime jdt = JDateTimeConverter.valueOf(c);
		assertEquals("2001-01-01 02:03:04.500", jdt.toString());
		Calendar c1 = CalendarConverter.valueOf(jdt);
		assertEquals(2001, c1.get(Calendar.YEAR));
		assertEquals(0, c1.get(Calendar.MONTH));
		assertEquals(1, c1.get(Calendar.DAY_OF_MONTH));
		assertEquals(2, c1.get(Calendar.HOUR_OF_DAY));
		assertEquals(3, c1.get(Calendar.MINUTE));
		assertEquals(4, c1.get(Calendar.SECOND));
		assertEquals(500, c1.get(Calendar.MILLISECOND));


		GregorianCalendar gc = new GregorianCalendar(2002, 5, 2, 3, 4, 5);
		gc.set(GregorianCalendar.MILLISECOND, 600);
		jdt = JDateTimeConverter.valueOf(gc);
		assertEquals("2002-06-02 03:04:05.600", jdt.toString());
		GregorianCalendar gc1 = (GregorianCalendar) CalendarConverter.valueOf(jdt);

		assertEquals(2002, gc1.get(GregorianCalendar.YEAR));
		assertEquals(5, gc1.get(GregorianCalendar.MONTH));
		assertEquals(2, gc1.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(3, gc1.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(4, gc1.get(GregorianCalendar.MINUTE));
		assertEquals(5, gc1.get(GregorianCalendar.SECOND));
		assertEquals(600, gc1.get(GregorianCalendar.MILLISECOND));

		
		Date d = new Date(101, 2, 3, 4, 5, 6);
		jdt = JDateTimeConverter.valueOf(d);
		assertEquals("2001-03-03 04:05:06.000", jdt.toString());
		Date d2 = DateConverter.valueOf(jdt);
		assertEquals(101, d2.getYear());
		assertEquals(2, d2.getMonth());
		assertEquals(3, d2.getDate());
		assertEquals(4, d2.getHours());
		assertEquals(5, d2.getMinutes());
		assertEquals(6, d2.getSeconds());

		
		JDateTime gt_new = new JDateTime(2003, 6, 5, 4, 3, 2, 100);
		jdt.setJulianDate(gt_new.getJulianDate());
		assertEquals("2003-06-05 04:03:02.100",	jdt.toString());
		JDateTime gt2 = jdt.clone();
		assertEquals(2003, gt2.getYear());
		assertEquals(6, gt2.getMonth());
		assertEquals(5, gt2.getDay());
		assertEquals(4, gt2.getHour());
		assertEquals(3, gt2.getMinute());
		assertEquals(2, (int)gt2.getSecond());
		assertEquals(100, gt2.getMillisecond());

		
		java.sql.Date sd = new java.sql.Date(123, 4, 5);
		jdt = JDateTimeConverter.valueOf(sd);
		assertEquals("2023-05-05 00:00:00.000",	jdt.toString());
		java.sql.Date sd2 = new java.sql.Date(1, 2, 3);
		sd2 = SqlDateConverter.valueOf(jdt);
		assertEquals(123, sd2.getYear());
		assertEquals(4, sd2.getMonth());
		assertEquals(5, sd2.getDate());

		
		Timestamp st = new Timestamp(123, 4, 5, 6, 7, 8, 500000000);
		jdt = Convert.toJDateTime(st);
		assertEquals("2023-05-05 06:07:08.500",	jdt.toString());
		Timestamp st2 = SqlTimestampConverter.valueOf(jdt);
		assertEquals(123, st2.getYear());
		assertEquals(4, st2.getMonth());
		assertEquals(5, st2.getDate());
		assertEquals(6, st2.getHours());
		assertEquals(7, st2.getMinutes());
		assertEquals(8, st2.getSeconds());
		assertEquals(500, st2.getNanos()/1000000);
	}

	public void testMillis() {
		GregorianCalendar gc = new GregorianCalendar();
		JDateTime jdt = new JDateTime();

		long delta = 0;
		if (jdt.getHour() == 1) {
			delta = 60 * 60 * 1000;
		}

		long now = System.currentTimeMillis() + delta;
		gc.setTimeInMillis(now);
		jdt.setTimeInMillis(now);
		assertEquals(now, jdt.getTimeInMillis());
		assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());

		JDateTime jdt2 = new JDateTime();
		jdt2.setTimeInMillis(jdt.getTimeInMillis());
		assertEquals(jdt.toString(), jdt2.toString());
		assertEquals(gc.getTimeInMillis(), jdt2.getTimeInMillis());


		for (int year = 1000; year < 3000; year++) {
			if (year == 1582) {
				continue;
			}
			jdt.setYear(year);
			jdt.setMillisecond(0);
			gc.set(Calendar.YEAR, year);
			gc.set(Calendar.MILLISECOND, 0);
			for (int month = 1; month <= 12; month++) {
				jdt.setMonth(month);
				gc.set(Calendar.MONTH, month - 1);
				for (int sec = 0; sec < 60; sec++) {
					jdt.setSecond(sec);
					gc.set(Calendar.SECOND, sec);
					assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());

					jdt.setTimeInMillis(gc.getTimeInMillis());
					gc.setTimeInMillis(gc.getTimeInMillis());
					assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());
				}
			}
		}
	}


	public void testClone() {
		JDateTime now = new JDateTime(2009, 5, 1, 23, 45, 1, 0);
		JulianDateStamp now3 = now.getJulianDate().clone();
		JDateTime now2 = now.clone();

		assertEquals(now.time, now2.time);

		assertEquals(now.jdate, now3);
		assertEquals(now.jdate, now2.jdate);
		assertEquals(now, now2);
	}

}
