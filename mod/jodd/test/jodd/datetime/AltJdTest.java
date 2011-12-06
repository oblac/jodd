// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import junit.framework.TestCase;

public class AltJdTest extends TestCase {

	public void testReduced() {
		JDateTime jdt = new JDateTime(2454945.41707);
		assertEquals(2454945.41707, jdt.getJulianDateDouble(), 1.0e-10);
		assertEquals("2009-04-23 22:00:34.848", jdt.toString());

		JulianDateStamp r = jdt.getReducedJulianDate();
		assertEquals(54945, r.integer);
		assertEquals(0.41707, r.fraction, 1.0e-10);
		assertEquals(54945.41707, jdt.getReducedJulianDateDouble(), 1.0e-10);

		JDateTime jdt2 = new JDateTime();
		jdt2.setReducedJulianDate(54945.41707);
		assertEquals(jdt, jdt2);
	}

	public void testModified() {
		JDateTime jdt = new JDateTime(2454945.41707);

		JulianDateStamp r = jdt.getModifiedJulianDate();
		assertEquals(54944, r.integer);
		assertEquals(0.91707, r.fraction, 1.0e-10);
		assertEquals(54944.91707, jdt.getModifiedJulianDateDouble(), 1.0e-10);

		JDateTime jdt2 = new JDateTime();
		jdt2.setModifiedJulianDate(54944.91707);
		assertEquals(jdt, jdt2);
	}

	public void testTruncated() {
		JDateTime jdt = new JDateTime(2454945.41707);

		JulianDateStamp r = jdt.getTruncatedJulianDate();
		assertEquals(14944, r.integer);
		assertEquals(0.91707, r.fraction, 1.0e-10);
		assertEquals(14944.91707, jdt.getTruncatedJulianDateDouble(), 1.0e-10);

		JDateTime jdt2 = new JDateTime();
		jdt2.setTruncatedJulianDate(14944.91707);
		assertEquals(jdt, jdt2);
	}
}
