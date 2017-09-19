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

package jodd.datetime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class JDateTimeMoreTest {

	@Test
	public void test1582() {
		JDateTime jdt1582 = new JDateTime(1582, 10, 4);
		assertEquals(4, jdt1582.getDayOfWeek());
		jdt1582.addDay(1);
		assertEquals(1582, jdt1582.getYear());
		assertEquals(10, jdt1582.getMonth());
		assertEquals(15, jdt1582.getDay());
		assertEquals(5, jdt1582.getDayOfWeek());
	}

	@Test
	public void testCompareToAndAdd() {
		JDateTime gt1 = new JDateTime();
		if (gt1.getDay() > 28) {        // back and forth adds works without corrections
			gt1.setDay(28);                // for days that exists in all months
		}

		// check for year 1582
		if (gt1.getMonth() == 10) {
			if ((gt1.getDay() > 4) && ((gt1.getDay() < 15))) {
				gt1.setDay(4);
			}
		}

		JDateTime gt2 = gt1.clone();
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
		for (int i = 1; i < 5000; i++) {                // Year 1582, months moving
			gt2.add(0, i, 0);                            // before it still doesn't work
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
			gt2.addSecond(-7 * i);
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


	@Test
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
						continue;        // skip this year due to specific daylight savings
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


	@Test
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


	@Test
	public void test1() {
		DateTimeStamp dts = new DateTimeStamp(-2310, 3, 24, 7, 6, 16, 171);

		JDateTime jdt = new JDateTime(dts);

		DateTimeStamp dts2 = jdt.getDateTimeStamp();
		assertNotSame(dts, dts2);
		assertEquals(dts, dts2);

		DateTimeStamp dts3 = TimeUtil.fromJulianDate(TimeUtil.toJulianDate(dts));
		assertNotSame(dts, dts3);
		assertEquals(dts, dts3);
	}


	@Test
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


	@Test
	public void testMillis0() {
		JDateTime jdt = new JDateTime(0);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(0);
		assertEquals(gc.get(Calendar.HOUR), jdt.getHour());
	}

	// ---------------------------------------------------------------- additional

	@Test
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

	@Test
	public void testWeeks() {
		JDateTime jdt = new JDateTime(2011, 1, 1);
		assertEquals(0, jdt.getWeekOfMonth());    // in previous year!
		assertEquals(52, jdt.getWeekOfYear());    // in previous year!

		jdt.setDate(2011, 1, 2);
		assertEquals(0, jdt.getWeekOfMonth());    // in previous year!
		assertEquals(52, jdt.getWeekOfYear());    // in previous year!

		jdt.setDate(2011, 1, 3);
		assertEquals(JDateTime.MONDAY, jdt.getDayOfWeek());
		assertEquals(1, jdt.getWeekOfMonth());
		assertEquals(1, jdt.getWeekOfYear());

		jdt.setDate(2011, 1, 9);
		assertEquals(1, jdt.getWeekOfMonth());
		assertEquals(1, jdt.getWeekOfYear());

		jdt.setDate(2011, 1, 10);
		assertEquals(2, jdt.getWeekOfMonth());
		assertEquals(2, jdt.getWeekOfYear());

		jdt.setDate(2011, 1, 30);
		assertEquals(4, jdt.getWeekOfMonth());
		assertEquals(4, jdt.getWeekOfYear());

		jdt.setDate(2011, 1, 31);
		assertEquals(5, jdt.getWeekOfMonth());    // ?
		assertEquals(5, jdt.getWeekOfYear());

		jdt.setDate(2011, 2, 1);
		assertEquals(1, jdt.getWeekOfMonth());
		assertEquals(5, jdt.getWeekOfYear());

		jdt.setDate(2011, 3, 27);
		assertEquals(4, jdt.getWeekOfMonth());
		assertEquals(12, jdt.getWeekOfYear());


		jdt.setDate(2011, 12, 31);
		assertEquals(52, jdt.getWeekOfYear());
		jdt.setDate(2012, 1, 1);
		assertEquals(52, jdt.getWeekOfYear());
	}

	@Test
	public void testNow() {
		long time = System.currentTimeMillis();
		JDateTime jdt = new JDateTime(time);

		assertEquals(time, jdt.getTimeInMillis());
	}

	@Test
	public void testMillisPrecision() {
		JDateTimeDefault.timeZone = TimeZone.getTimeZone("CET");

		JDateTime jdt = new JDateTime(new JulianDateStamp(2456223, 0.42596945));
		JDateTime jdt0 = new JDateTime(1350936803760L);
		JDateTime jdt1 = new JDateTime(2012, 10, 22, 22, 13, 23, 760);

		assertEquals(1350936803760L, jdt1.getTimeInMillis());
		assertEquals(1350936803760L, jdt0.getTimeInMillis());
		assertEquals(1350936803760L, jdt.getTimeInMillis());
		assertEquals(42596945, jdt.getJulianDate().getSignificantFraction());
		assertEquals(42596944, jdt0.getJulianDate().getSignificantFraction());
		assertEquals(42596944, jdt1.getJulianDate().getSignificantFraction());

		assertEquals(jdt0, jdt1);
		assertEquals(jdt, jdt0);
		assertEquals(jdt, jdt1);

		JDateTimeDefault.timeZone = null;
	}
}
