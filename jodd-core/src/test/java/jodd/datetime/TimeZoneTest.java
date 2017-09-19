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

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeZoneTest {

	@Test
	public void testTimeZones() {
		GregorianCalendar gc = new GregorianCalendar();
		JDateTime jdt1 = new JDateTime();
		gc.setTimeInMillis(jdt1.getTimeInMillis());

		TimeZone tz = TimeZone.getTimeZone("GMT+01:00");
		jdt1.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt1.getTimeInMillis());

		JDateTime jdt2 = jdt1.clone();
		assertEquals(jdt1, jdt2);

		tz = TimeZone.getTimeZone("GMT+02:00");
		jdt2.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt1.getTimeInMillis());

		if (jdt2.getHour() != 0) {
			assertEquals(jdt1.getHour() + 1, jdt2.getHour());
			assertEquals(jdt1.getMinute(), jdt2.getMinute());
			assertEquals(jdt1.getSecond(), jdt2.getSecond());
			assertEquals(jdt1.getMillisecond(), jdt2.getMillisecond());
		}

		tz = TimeZone.getTimeZone("GMT-12:00");
		jdt1.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt1.getTimeInMillis());

		tz = TimeZone.getTimeZone("GMT+10:00");
		jdt2.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt2.getTimeInMillis());
		assertEquals(jdt1.getTimeInMillis(), jdt2.getTimeInMillis());

		java.util.Date date = jdt1.convertToDate();
		assertEquals(date.getTime(), jdt1.getTimeInMillis());

		GregorianCalendar cal = (GregorianCalendar) jdt1.convertToCalendar();
		assertEquals(cal.getTimeInMillis(), jdt1.getTimeInMillis());
		assertEquals(cal.getTimeZone(), jdt1.getTimeZone());

		java.sql.Date sqlDate = jdt1.convertToSqlDate();
		assertEquals(sqlDate.getTime(), jdt1.getTimeInMillis());

		Timestamp sqlTimestamp = jdt1.convertToSqlTimestamp();
		assertEquals(sqlTimestamp.getTime(), jdt1.getTimeInMillis());
	}

	@Test
	public void testTzOffset() {
		JDateTime now = new JDateTime(2009, 5, 1, 23, 45, 1, 0);
		now.changeTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));
		TimeZone tz1 = now.getTimeZone();
		TimeZone tz2 = TimeZone.getTimeZone("GMT+01:00");
		TimeZone tz3 = TimeZone.getTimeZone("Japan");

		assertEquals(0, TimeZoneUtil.getRawOffsetDifference(tz1, tz2));
		assertEquals(-3600000, TimeZoneUtil.getOffsetDifference(now.getTimeInMillis(), tz1, tz2));
		assertEquals(-3600000, TimeZoneUtil.getOffsetDifference(now, tz1, tz2));

		assertEquals(8 * 3600000, TimeZoneUtil.getRawOffsetDifference(tz1, tz3));
		assertEquals(7 * 3600000, TimeZoneUtil.getOffsetDifference(now.getTimeInMillis(), tz1, tz3));
		assertEquals(7 * 3600000, TimeZoneUtil.getOffsetDifference(now, tz1, tz3));
	}

	@Test
	public void testDlt() {
		TimeZone cetTimeZone = TimeZone.getTimeZone("CET");
		TimeZone cestTimeZone = TimeZone.getTimeZone("CEST");

		JDateTime jDateTime = new JDateTime(2012, 6, 1, 11, 44, 55, 0);
		jDateTime.setTimeZone(cetTimeZone);
		Date date = jDateTime.convertToDate();

		assertEquals(cetTimeZone.inDaylightTime(date), jDateTime.isInDaylightTime());

		jDateTime.setTimeZone(cestTimeZone);
		assertEquals(cestTimeZone.inDaylightTime(date), jDateTime.isInDaylightTime());
	}

	@Test
	public void testChangeTimezones() {
		TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
		TimeZone gmt3TZ = TimeZone.getTimeZone("GMT+3");

		JDateTime jdt = new JDateTime(2010, 3, 27, 12, 11, 21, 0);
		jdt.changeTimeZone(gmtTZ, gmt3TZ);
		assertEquals(15, jdt.getHour());
	}

}
