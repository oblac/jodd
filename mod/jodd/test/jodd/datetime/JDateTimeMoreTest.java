// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import java.util.GregorianCalendar;
import java.util.Calendar;

import junit.framework.TestCase;

public class JDateTimeMoreTest extends TestCase {

	public void test1582() {
		JDateTime jdt1582 = new JDateTime(1582, 10, 4);
		assertEquals(4, jdt1582.getDayOfWeek());
		jdt1582.addDay(1);
		assertEquals(1582, jdt1582.getYear());
		assertEquals(10, jdt1582.getMonth());
		assertEquals(15, jdt1582.getDay());
		assertEquals(5, jdt1582.getDayOfWeek());
	}

	public void testCompareToAndAdd() {
		JDateTime gt1 = new JDateTime();
		if (gt1.getDay() > 28) {		// back and forth adds works without corrections
			gt1.setDay(28);				// for days that exists in all months
		}

		// check for year 1582
		if (gt1.getMonth() == 10) {
			if ((gt1.getDay() > 4) && ((gt1.getDay() < 15))) {
				gt1.setDay(4);
			}
		}

		JDateTime gt2 = (JDateTime) gt1.clone();
		assertEquals(0, gt1.compareTo(gt2));

		for (int i = 1; i < 1000; i++) {
			gt2.add(i, 0, 0);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addYear(-2 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addYear(i);
			assertEquals(0, gt1.compareTo(gt2));
		}
		
		for (int i = 1; i < 60000; i++) {
			gt2.add(0, i, 0);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addMonth(-i);
			assertEquals(0, gt1.compareTo(gt2));
		}
		for (int i = 1; i < 5000; i++) {				// Year 1582, months moving
			gt2.add(0, i, 0);							// before it still doesn't work
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addMonth(-2 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addMonth(i);
			assertEquals(0, gt1.compareTo(gt2));
		}

		for (int i = 1; i < 10000; i++) {
			gt2.add(0, 0, i);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addDay(-3 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addDay(2 * i);
			assertEquals(0, gt1.compareTo(gt2));
		}

		for (int i = 1; i < 100000; i++) {
			gt2.addTime(i, 0, 0, 0);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addHour(-4 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addHour(3 * i);
			assertEquals(0, gt1.compareTo(gt2));
		}

		for (int i = 1; i < 100000; i++) {
			gt2.addTime(0, i, 0, 0);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addMinute(-5 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addMinute(4 * i);
			assertEquals(0, gt1.compareTo(gt2));
		}

		for (int i = 1; i < 1000000; i++) {
			gt2.addTime(0, 0, i, 0);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addSecond(- 7 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addSecond(6 * i);
			assertEquals(0, gt1.compareTo(gt2));
		}

		for (int i = 1; i < 1000000; i++) {
			gt2.addTime(0, 0, 0, i);
			assertEquals(-1, gt1.compareTo(gt2));
			gt2.addMillisecond(-8 * i);
			assertEquals(1, gt1.compareTo(gt2));
			gt2.addMillisecond(7 * i);
			assertEquals(0, gt1.compareTo(gt2));
		}
	}
	



	public void testWeekOfYear() {
		JDateTime gt = new JDateTime();
		GregorianCalendar gc = new GregorianCalendar();
		int[] _fdiw = {0, GregorianCalendar.MONDAY, GregorianCalendar.TUESDAY, GregorianCalendar.WEDNESDAY, GregorianCalendar.THURSDAY, GregorianCalendar.FRIDAY, GregorianCalendar.SATURDAY, GregorianCalendar.SUNDAY};		
		
		// test all starting dates (first day in week)
		for (int fdiw = 1; fdiw <= 7; fdiw++) {
			gc.setFirstDayOfWeek(_fdiw[fdiw]);

			// test all minimal days in first week
			for (int min = 1; min <= 7; min++) {
				gc.setMinimalDaysInFirstWeek(min);
				gt.setWeekDefinitionAlt(fdiw, min);

				// test many years
				for (int y = 1800; y < 3000; y++) {

					if (y == 1916) {
						continue;		// skip this year due to specific daylight savings
					}

					gt.set(y, 1, 1);
					gc.set(y, 0, 1);
		
					int total = gt.isLeapYear() ? 366 : 365;
					
					// test all days
					for (int i = 0; i < total; i++) {
						assertEquals(gc.get(GregorianCalendar.DAY_OF_MONTH), gt.getDay());
						assertEquals(gc.get(GregorianCalendar.MONTH) + 1, gt.getMonth());
						assertEquals(gc.get(GregorianCalendar.YEAR), gt.getYear());
						assertEquals(gc.get(GregorianCalendar.DAY_OF_YEAR), gt.getDayOfYear());
						int dow = gc.get(GregorianCalendar.DAY_OF_WEEK) - 1;
						if (dow == 0) {
							dow = 7;
						}
						assertEquals(dow, gt.getDayOfWeek());
						assertEquals(gc.get(GregorianCalendar.WEEK_OF_YEAR), gt.getWeekOfYear());
						assertEquals(gc.get(GregorianCalendar.WEEK_OF_MONTH), gt.getWeekOfMonth());

						gt.addDay(1);
						gc.roll(GregorianCalendar.DAY_OF_YEAR, true);
					}
				}
			}
		}
	}

	// ---------------------------------------------------------------- specific problems


	public void testMillisProblems() {
		GregorianCalendar gc = new GregorianCalendar();
		JDateTime jdt = new JDateTime();

		long now = 1183243766625L;
		gc.setTimeInMillis(now);
		jdt.setTimeInMillis(now);
		assertEquals(now, jdt.getTimeInMillis());
		assertEquals(now, jdt.getTimeInMillis());

		JDateTime jdt2 = new JDateTime();
		jdt2.setTimeInMillis(jdt.getTimeInMillis());
		assertEquals(jdt.toString(), jdt2.toString());
		assertEquals(gc.getTimeInMillis(), jdt2.getTimeInMillis());
		assertEquals(now, jdt2.getTimeInMillis());


		int year = 1970;
		jdt.setYear(year);
		jdt.setMillisecond(0);
		gc.set(Calendar.YEAR, year);
		gc.set(Calendar.MILLISECOND, 0);
		int month = 1;

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


	public void test1() {
		DateTimeStamp dts = new DateTimeStamp(-2310, 3, 24, 7, 6, 16, 171);

		JDateTime jdt = new JDateTime(dts);
		System.out.println(jdt);
		DateTimeStamp dts2 = jdt.getDateTimeStamp();
		assertNotSame(dts, dts2);
		assertEquals(dts, dts2);

		DateTimeStamp dts3 = TimeUtil.fromJulianDate(TimeUtil.toJulianDate(dts));
		assertNotSame(dts, dts3);
		assertEquals(dts, dts3);
	}


	public void testCtor() {
		JDateTime jdt = new JDateTime("2011-04-01 12:32:22.123");
		assertEquals(2011, jdt.getYear());
		assertEquals(4, jdt.getMonth());
		assertEquals(1, jdt.getDay());
		assertEquals(12, jdt.getHour());
		assertEquals(32, jdt.getMinute());
		assertEquals(22, jdt.getSecond());
		assertEquals(123, jdt.getMillisecond());

		jdt = new JDateTime("01.04.2011/12-32*22+123", "DD.MM.YYYY/hh-mm*ss+mss");
		assertEquals(2011, jdt.getYear());
		assertEquals(4, jdt.getMonth());
		assertEquals(1, jdt.getDay());
		assertEquals(12, jdt.getHour());
		assertEquals(32, jdt.getMinute());
		assertEquals(22, jdt.getSecond());
		assertEquals(123, jdt.getMillisecond());
	}


	public void testMillis0() {
		JDateTime jdt = new JDateTime(0);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(0);
		assertEquals(gc.get(Calendar.HOUR), jdt.getHour());
	}

	// ---------------------------------------------------------------- additional

	public void testAddMonthNoFix() {
		JDateTime jdt;

		// January, no fix

		jdt = new JDateTime("2010-01-31");
        jdt.addMonth(1, false);
		assertEquals("2010-03-03", jdt.toString("YYYY-MM-DD"));
		jdt.subMonth(1, false);
		assertEquals("2010-02-03", jdt.toString("YYYY-MM-DD"));

		// January, fix

		jdt = new JDateTime("2010-01-31");
        jdt.addMonth(1);
		assertEquals("2010-02-28", jdt.toString("YYYY-MM-DD"));
		jdt.subMonth(1);
		assertEquals("2010-01-28", jdt.toString("YYYY-MM-DD"));

		// January, fix, Gregorian calendar
		GregorianCalendar gc = new GregorianCalendar(2010, 0, 31);
		gc.add(Calendar.MONTH, 1);
		assertEquals(1, gc.get(Calendar.MONTH));
		assertEquals(28, gc.get(Calendar.DAY_OF_MONTH));

		gc.add(Calendar.MONTH, -1);
		assertEquals(0, gc.get(Calendar.MONTH));
		assertEquals(28, gc.get(Calendar.DAY_OF_MONTH));


		// days, no month fix

		jdt = new JDateTime("2010-01-31");
        jdt.addDay(31, false);
		assertEquals("2010-03-03", jdt.toString("YYYY-MM-DD"));
		jdt.subDay(31, false);
		assertEquals("2010-01-31", jdt.toString("YYYY-MM-DD"));

		jdt = new JDateTime("2010-01-31");
        jdt.addDay(31);
		assertEquals("2010-03-03", jdt.toString("YYYY-MM-DD"));
		jdt.subDay(31);
		assertEquals("2010-01-31", jdt.toString("YYYY-MM-DD"));

		// March

		jdt = new JDateTime("2010-03-31");
        jdt.addMonth(1, false);
		assertEquals("2010-05-01", jdt.toString("YYYY-MM-DD"));
		jdt.subMonth(1, false);
		assertEquals("2010-04-01", jdt.toString("YYYY-MM-DD"));

		jdt = new JDateTime("2010-03-31");
        jdt.addMonth(1);
		assertEquals("2010-04-30", jdt.toString("YYYY-MM-DD"));
		jdt.subMonth(1);
		assertEquals("2010-03-30", jdt.toString("YYYY-MM-DD"));

	}

}
