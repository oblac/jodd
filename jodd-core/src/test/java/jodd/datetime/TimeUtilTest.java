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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TimeUtilTest {

	@Test
	public void testFromToJulian() {

		DateTimeStamp time;
		JulianDateStamp jds;

		time = TimeUtil.fromJulianDate(0.0);
		assertEquals("-4712-1-1 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(0.0, jds.doubleValue(), 1.0e-8);
		assertEquals(0, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(59.0);
		assertEquals("-4712-2-29 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(59.0, jds.doubleValue(), 1.0e-8);
		assertEquals(59, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(366.0);
		assertEquals("-4711-1-1 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(366.0, jds.doubleValue(), 1.0e-8);
		assertEquals(366, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(731.0);
		assertEquals("-4710-1-1 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(731.0, jds.doubleValue(), 1.0e-8);
		assertEquals(731, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(1721058.0);
		assertEquals("0-1-1 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(1721058.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721058, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(1721057.0);
		assertEquals("-1-12-31 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(1721057.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721057, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(1721117.0);
		assertEquals("0-2-29 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(1721117.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721117, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(1721118.0);
		assertEquals("0-3-1 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(1721118.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721118, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(1721423.0);
		assertEquals("0-12-31 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(1721423.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721423, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(1721424.0);
		assertEquals("1-1-1 12:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(1721424.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721424, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(2440587.5);
		assertEquals("1970-1-1 0:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2440587.5, jds.doubleValue(), 1.0e-8);
		assertEquals(2440588, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(2451774.726007);
		assertEquals("2000-8-18 5:25:27.4", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2451774.726007, jds.doubleValue(), 1.0e-8);
		assertEquals(2451775, jds.getJulianDayNumber());
		DateTimeStamp time2 = TimeUtil.fromJulianDate(jds.doubleValue());
		assertEquals(time2, time);

		time = TimeUtil.fromJulianDate(2451774.72600701);
		assertEquals("2000-8-18 5:25:27.5", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2451774.72600701, jds.doubleValue(), 1.0e-8);
		assertEquals(2451775, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(2451774.72600702);
		assertEquals("2000-8-18 5:25:27.6", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2451774.72600702, jds.doubleValue(), 1.0e-8);
		assertEquals(2451775, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(2299160.49998901);
		assertEquals("1582-10-4 23:59:59.50", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2299160.49998901, jds.doubleValue(), 1.0e-8);
		assertEquals(2299160, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(2299160.5);
		assertEquals("1582-10-15 0:0:0.0", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2299160.5, jds.doubleValue(), 1.0e-8);
		assertEquals(2299161, jds.getJulianDayNumber());

		time = TimeUtil.fromJulianDate(2147438064.499989);
		assertEquals("5874773-8-15 23:59:59.52", time.toString());
		jds = TimeUtil.toJulianDate(time);
		assertEquals(2147438064.499989, jds.doubleValue(), 1.0e-8);
		assertEquals(2147438064, jds.getJulianDayNumber());
	}

	@Test
	public void testFix() {
		DateTimeStamp t = new DateTimeStamp();
		t.year = 2003;
		t.month = 7;
		t.day = 26;
		t.hour = 0;
		t.minute = 0;
		t.second = 2;
		double jd = TimeUtil.toJulianDate(t).doubleValue();
		DateTimeStamp t2 = TimeUtil.fromJulianDate(jd);
		assertFalse(t.toString().equals(t2.toString()));

		JulianDateStamp jds = TimeUtil.toJulianDate(t);
		t2 = TimeUtil.fromJulianDate(jds);
		assertEquals(t.toString(), t2.toString());
	}

	@Test
	public void testMonteCarlo() {
		Random r = new Random();
		for (int i = 0; i < 5000000; i++) {
			double jd = r.nextFloat() * 3.0e6;
			DateTimeStamp t = TimeUtil.fromJulianDate(jd);
			JulianDateStamp jds = TimeUtil.toJulianDate(t);
			DateTimeStamp t2 = TimeUtil.fromJulianDate(jds);
			assertEquals(t, t2);
		}
	}

	@Test
	public void testDates() {
		DateTimeStamp t = new DateTimeStamp();
		GregorianCalendar gc = new GregorianCalendar();
		for (int y = 1970; y < 2100; y++) {
			gc.set(y, 0, 1);
			t.year = y;
			for (int i = 0; i < 365; i++) {
				t.month = gc.get(Calendar.MONTH) + 1;
				t.day = gc.get(Calendar.DAY_OF_MONTH);
				t.hour = t.minute = 0;
				t.second = 0;
				t.millisecond = 0;
				double jd = TimeUtil.toJulianDate(t).doubleValue();
				DateTimeStamp t2 = TimeUtil.fromJulianDate(jd);
				assertEquals(t.toString(), t2.toString());
				gc.add(Calendar.DAY_OF_YEAR, 1);
			}
		}
	}


	@Test
	public void testTimes() {
		DateTimeStamp t = new DateTimeStamp();
		GregorianCalendar gc = new GregorianCalendar();
		t.year = gc.get(Calendar.YEAR);
		gc.set(t.year, gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		for (int day = 0; day < 1; day++) {
			t.month = gc.get(Calendar.MONTH) + 1;
			t.day = gc.get(Calendar.DAY_OF_MONTH);
			for (int i = 0; i < 86400; i++) {
				t.hour = gc.get(Calendar.HOUR_OF_DAY);
				t.minute = gc.get(Calendar.MINUTE);
				t.second = gc.get(Calendar.SECOND);
				JulianDateStamp jds = TimeUtil.toJulianDate(t);
				DateTimeStamp t2 = TimeUtil.fromJulianDate(jds);
				assertEquals(t.toString(), t2.toString());
				gc.add(Calendar.SECOND, 1);
			}
			gc.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	@Test
	public void testDayOfYear() {

		int doy = TimeUtil.dayOfYear(2003, 1, 1);
		assertEquals(1, doy);

		doy = TimeUtil.dayOfYear(2003, 2, 1);
		assertEquals(32, doy);

		doy = TimeUtil.dayOfYear(2003, 12, 31);
		assertEquals(365, doy);

		doy = TimeUtil.dayOfYear(2004, 12, 31);
		assertEquals(366, doy);

		doy = TimeUtil.dayOfYear(2000, 12, 31);
		assertEquals(366, doy);

	}

	@Test
	public void testSetAccumulation() {
		DateTimeStamp gts1 = new DateTimeStamp(2003, 11, 24, 21, 40, 38, 173);
		DateTimeStamp gts2 = new DateTimeStamp(2003, 11, 24, 21, 40, 38, 173);
		assertEquals(0, gts1.compareTo(gts2));
		assertEquals(0, gts2.compareTo(gts1));

		for (int i = 0; i < 100; i++) {
			JulianDateStamp jds = TimeUtil.toJulianDate(gts2);
			gts2 = TimeUtil.fromJulianDate(jds);
		}
		assertEquals(0, gts1.compareTo(gts2));
		assertEquals(0, gts2.compareTo(gts1));
	}


	@Test
	public void testSetDateAccumulation() {
		DateTimeStamp gts1 = new DateTimeStamp(2003, 11, 24, 21, 40, 38, 173);
		DateTimeStamp gts2 = new DateTimeStamp(2003, 11, 24, 21, 40, 38, 173);
		assertEquals(0, gts1.compareTo(gts2));
		assertEquals(0, gts2.compareTo(gts1));

		for (int i = 0; i < 100; i++) {
			JulianDateStamp jds = TimeUtil.toJulianDate(2003, 11, 24, gts2.hour, gts2.minute, gts2.second, gts2.millisecond);
			gts2 = TimeUtil.fromJulianDate(jds);
		}
		assertEquals(0, gts1.compareTo(gts2));
		assertEquals(0, gts2.compareTo(gts1));
	}

	@Test
	public void testToCalendar() {
		assertEquals(Calendar.JANUARY, TimeUtil.toCalendarMonth(JDateTime.JANUARY));
		assertEquals(Calendar.DECEMBER, TimeUtil.toCalendarMonth(JDateTime.DECEMBER));

		assertEquals(Calendar.MONDAY, TimeUtil.toCalendarDayOfWeek(JDateTime.MONDAY));
		assertEquals(Calendar.TUESDAY, TimeUtil.toCalendarDayOfWeek(JDateTime.TUESDAY));
		assertEquals(Calendar.SATURDAY, TimeUtil.toCalendarDayOfWeek(JDateTime.SATURDAY));
		assertEquals(Calendar.SUNDAY, TimeUtil.toCalendarDayOfWeek(JDateTime.SUNDAY));
	}

	@Test
	public void testHttpTime() {
		long millis = System.currentTimeMillis();

		millis = (millis / 1000) * 1000;

		String time = TimeUtil.formatHttpDate(millis);

		long millisBack = TimeUtil.parseHttpTime(time);

		assertEquals(millis, millisBack);
	}


}
