// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.typeconverter.impl.DateConverter;
import junit.framework.TestCase;

import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import static java.util.Calendar.HOUR;

public class DstTest extends TestCase {

	public void testSpringForward() {
		TimeZone englandTZ = TimeZone.getTimeZone("Europe/London");
		TimeZone.setDefault(englandTZ);

		// set time BEFORE dst
		JDateTime jdt = new JDateTime(2009, 3, 29, 0, 31, 0, 0);
		jdt.setTrackDST(true);
		assertEquals(englandTZ, jdt.getTimeZone());
		GregorianCalendar gc = new GregorianCalendar(2009, 2, 29, 0, 31, 0);

		Date date = DateConverter.valueOf(jdt);
		assertEquals(gc.getTime(), date);
		assertFalse(englandTZ.inDaylightTime(date));
		assertFalse(englandTZ.inDaylightTime(gc.getTime()));

		assertEquals(0, gc.get(HOUR));
		assertEquals(0, jdt.getHour());

		assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());
		assertEquals(0, TimeZoneUtil.getOffset(jdt, englandTZ));

		// add one hour!
		jdt.addHour(1);
		gc.add(HOUR, 1);


		// in the DST
		assertEquals(3600000, TimeZoneUtil.getOffset(jdt, englandTZ));
		assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());
		date = DateConverter.valueOf(jdt);
		assertEquals(gc.getTime(), date);

		assertTrue(englandTZ.inDaylightTime(date));
		assertTrue(englandTZ.inDaylightTime(gc.getTime()));

		assertEquals(2, gc.get(HOUR));
		assertEquals(2, jdt.getHour());
	}

/*
	public void testOnSpringFormward() {
		TimeZone englandTZ = TimeZone.getTimeZone("Europe/London");
		TimeZone.setDefault(englandTZ);

		// set time BEFORE dst
		JDateTime jdt = new JDateTime(2009, 3, 29, 1, 31, 0, 0);
		assertEquals(englandTZ, jdt.getTimeZone());
		GregorianCalendar gc = new GregorianCalendar(2009, 2, 29, 1, 31, 0);

//		assertEquals(gc.getTime(), jdt.convertToDate());
//		assertTrue(englandTZ.inDaylightTime(jdt.convertToDate()));
		assertTrue(englandTZ.inDaylightTime(gc.getTime()));

		assertEquals(2, gc.get(HOUR));
		assertEquals(2, jdt.getHour());

		assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());
		assertEquals(0, TimeZoneUtil.getOffset(jdt, englandTZ));
	}
*/

	public void testFallBack() {
		TimeZone englandTZ = TimeZone.getTimeZone("Europe/London");
		TimeZone.setDefault(englandTZ);

		// set time IN dst
		JDateTime jdt = new JDateTime(2009, 10, 25, 0, 31, 0, 0);
		jdt.setTrackDST(true);
		assertEquals(englandTZ, jdt.getTimeZone());
		GregorianCalendar gc = new GregorianCalendar(2009, 9, 25, 0, 31, 0);

		Date date = DateConverter.valueOf(jdt);
		assertEquals(gc.getTime(), date);
		assertTrue(englandTZ.inDaylightTime(gc.getTime()));
		assertTrue(englandTZ.inDaylightTime(date));

		assertEquals(0, gc.get(HOUR));
		assertEquals(0, jdt.getHour());

		assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());
		assertEquals(3600000, TimeZoneUtil.getOffset(jdt, englandTZ));

		// add two hours!
		jdt.addHour(2);
		gc.add(HOUR, 2);


		// after DST
		assertEquals(0, TimeZoneUtil.getOffset(jdt, englandTZ));
		assertEquals(gc.getTimeInMillis(), jdt.getTimeInMillis());
		date = DateConverter.valueOf(jdt);
		assertEquals(gc.getTime(), date);

		assertFalse(englandTZ.inDaylightTime(date));
		assertFalse(englandTZ.inDaylightTime(gc.getTime()));

		assertEquals(1, gc.get(HOUR));
		assertEquals(1, jdt.getHour());
	}

}
