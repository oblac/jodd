// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import junit.framework.TestCase;

public class JulianDateStampTest extends TestCase {

	public void testSet() {
		JDateTime jdt = new JDateTime(2008, 12, 20, 10, 44, 55, 0);
		JulianDateStamp jds = jdt.getJulianDate();
		int i = jds.integer;

		jds.set(i - 1, jds.fraction);
		JDateTime jdt2 = new JDateTime(jds);

		assertEquals(jdt.getYear(), jdt2.getYear());
		assertEquals(jdt.getMonth(), jdt2.getMonth());
		assertEquals(jdt.getDay() - 1, jdt2.getDay());
		assertEquals(jdt.getHour(), jdt2.getHour());
		assertEquals(jdt.getMinute(), jdt2.getMinute());
		assertEquals(jdt.getSecond(), jdt2.getSecond(), 0.0001);

	}

	public void testbetween() {
		JDateTime jdt = new JDateTime(2008, 12, 20, 0, 0, 0, 0);
		JDateTime jdt2 = new JDateTime(2008, 12, 20, 0, 0, 0, 0);
		assertEquals(0, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));

		jdt2.setTime(23, 59, 59, 0);
		assertEquals(0, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));

		jdt2.addSecond(1);
		assertEquals(1, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));

		jdt2.subDay(2);
		assertEquals(1, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));

		jdt2.subYear(1);
		assertEquals(367, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));		// 2008 is leap year

		jdt2.addDay(1);
		assertEquals(366, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));

	}


	public void testDecimalFloating() {

		DateTimeStamp dts = new DateTimeStamp(1970, 1, 13, 14, 24, 0, 0);
		JDateTime jdt = new JDateTime(new JulianDateStamp(2440600, 0.1));
		assertEquals(dts, jdt.getDateTimeStamp());

		JDateTime jdt2 = new JDateTime(new JulianDateStamp(2440600, 0.09999999991));
		assertEquals(dts, jdt2.getDateTimeStamp());
		jdt2 = new JDateTime(new JulianDateStamp(2440600, 0.10000001));
		assertEquals(dts, jdt2.getDateTimeStamp());

		jdt.addMillisecond(1);
		jdt.subMillisecond(1);
		assertEquals(dts, jdt.getDateTimeStamp());
	}
}
