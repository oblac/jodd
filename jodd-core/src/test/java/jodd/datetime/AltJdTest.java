// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AltJdTest {

	@Test
	public void testReduced() {
		JDateTime jdt = new JDateTime(2454945.41707);
		assertEquals(2454945.41707, jdt.getJulianDateDouble(), 1.0e-10);
		assertEquals("2009-04-23 22:00:34.848", jdt.toString());

		JulianDateStamp jds = jdt.getJulianDate().getReducedJulianDate();
		assertEquals(54945, jds.integer);
		assertEquals(0.41707, jds.fraction, 1.0e-10);
		assertEquals(54945.41707, jds.doubleValue(), 1.0e-10);

		JulianDateStamp jds2 = new JulianDateStamp();
		jds2.setReducedJulianDate(54945.41707);

		assertEquals(jdt.getJulianDate(), jds2);
	}

	@Test
	public void testModified() {
		JDateTime jdt = new JDateTime(2454945.41707);

		JulianDateStamp jds = jdt.getJulianDate().getModifiedJulianDate();
		assertEquals(54944, jds.integer);
		assertEquals(0.91707, jds.fraction, 1.0e-10);
		assertEquals(54944.91707, jds.doubleValue(), 1.0e-10);

		JulianDateStamp jds2 = new JulianDateStamp();
		jds2.setModifiedJulianDate(54944.91707);

		assertEquals(jdt.getJulianDate(), jds2);
	}

	@Test
	public void testTruncated() {
		JDateTime jdt = new JDateTime(2454945.41707);

		JulianDateStamp jds = jdt.getJulianDate().getTruncatedJulianDate();
		assertEquals(14944, jds.integer);
		assertEquals(0.91707, jds.fraction, 1.0e-10);
		assertEquals(14944.91707, jds.doubleValue(), 1.0e-10);

		JulianDateStamp jds2 = new JulianDateStamp();
		jds2.setTruncatedJulianDate(14944.91707);

		assertEquals(jdt.getJulianDate(), jds2);
	}

}
