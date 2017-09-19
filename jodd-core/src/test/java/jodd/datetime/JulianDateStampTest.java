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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JulianDateStampTest {

	@Test
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

	@Test
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
		assertEquals(367, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));        // 2008 is leap year

		jdt2.addDay(1);
		assertEquals(366, jdt2.getJulianDate().daysBetween(jdt.getJulianDate()));

	}


	@Test
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
