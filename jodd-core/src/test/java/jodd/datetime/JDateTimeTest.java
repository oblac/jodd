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

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

public class JDateTimeTest {

	@Test
	public void testSetGetMillis() {
		JDateTime jdt = new JDateTime(2003, 2, 28, 23, 59, 59, 0);

		for (int i = 0; i < 1000; i++) {
			jdt.setMillisecond(i);
			assertEquals(i, jdt.getMillisecond());
		}
	}


	@Test
	public void testSet999Millis() {

		JDateTime jdt = new JDateTime();

		jdt.set(2003, 2, 28, 23, 59, 59, 999);
		assertEquals("2003-02-28 23:59:59.999", jdt.toString());

		jdt.set(2003, 2, 28, 23, 59, 60, 0);
		assertEquals("2003-03-01 00:00:00.000", jdt.toString());

		// this used to be a problem
		jdt.set(2003, 2, 28, 23, 59, 59, 999);        // 12 fraction digits  - last working
		assertEquals("2003-02-28 23:59:59.999", jdt.toString());

	}


	@Test
	public void testDaysInMonth() {
		JDateTime jdt = new JDateTime(2003, 1, 1);
		assertEquals(31, jdt.getMonthLength());
		assertEquals(28, jdt.getMonthLength(2));

		jdt = new JDateTime(2000, 1, 1);
		assertEquals(31, jdt.getMonthLength());
		assertEquals(29, jdt.getMonthLength(2));
	}


	@Test
	public void testToString() {

		JDateTime jdt = new JDateTime(2003, 1, 1, 1, 1, 1, 1);
		assertEquals("2003-01-01 01:01:01.001", jdt.toString());

		jdt.set(200, 10, 10, 10, 10, 10, 12);
		assertEquals("0200-10-10 10:10:10.012", jdt.toString());

		jdt.set(2003, 10, 10, 10, 10, 10, 123);
		assertEquals("2003-10-10 10:10:10.123", jdt.toString());
	}

	@Test
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

		gt.setDate(2003, 11, 31);        // == 2003-12-01
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


	@Test
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

	@Test
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
		assertFalse(gt.isLeapYear());            // not a leap year
		assertEquals("1900-03-01 00:00:00.000", gt.toString());

		gt.set(2000, 2, 29);
		assertTrue(gt.isLeapYear());            // a leap year
		assertEquals("2000-02-29 00:00:00.000", gt.toString());

		gt.set(1600, 2, 29);
		assertTrue(gt.isLeapYear());            // a leap year
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


	@Test
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


	@Test
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
