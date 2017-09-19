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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static java.util.Calendar.HOUR;
import static org.junit.jupiter.api.Assertions.*;

public class DstTest {

	@Test
	public void testSpringForward() {
		TimeZone englandTZ = TimeZone.getTimeZone("Europe/London");
		TimeZone.setDefault(englandTZ);

		// set time BEFORE dst
		JDateTime jdt = new JDateTime(2009, 3, 29, 0, 31, 0, 0);
		jdt.setTrackDST(true);
		assertEquals(englandTZ, jdt.getTimeZone());
		GregorianCalendar gc = new GregorianCalendar(2009, 2, 29, 0, 31, 0);

		Date date = jdt.convertToDate();
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
		date = jdt.convertToDate();
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

	@Test
	public void testFallBack() {
		TimeZone englandTZ = TimeZone.getTimeZone("Europe/London");
		TimeZone.setDefault(englandTZ);

		// set time IN dst
		JDateTime jdt = new JDateTime(2009, 10, 25, 0, 31, 0, 0);
		jdt.setTrackDST(true);
		assertEquals(englandTZ, jdt.getTimeZone());
		GregorianCalendar gc = new GregorianCalendar(2009, 9, 25, 0, 31, 0);

		Date date = jdt.convertToDate();
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
		date = jdt.convertToDate();
		assertEquals(gc.getTime(), date);

		assertFalse(englandTZ.inDaylightTime(date));
		assertFalse(englandTZ.inDaylightTime(gc.getTime()));

		assertEquals(1, gc.get(HOUR));
		assertEquals(1, jdt.getHour());
	}

}
