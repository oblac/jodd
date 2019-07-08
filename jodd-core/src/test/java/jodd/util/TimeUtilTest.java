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

package jodd.util;

import jodd.time.JulianDate;
import jodd.time.TimeUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilTest {

	@Test
	void testFromToJulian() {
		JulianDate jds;

		jds = JulianDate.of(0.0);
		assertEquals("-4712-01-01T12:00", jds.toLocalDateTime().toString());
		assertEquals(0.0, jds.doubleValue(), 1.0e-8);
		assertEquals(0, jds.getJulianDayNumber());

		jds = JulianDate.of(59.0);
		assertEquals("-4712-02-29T12:00", jds.toLocalDateTime().toString());
		assertEquals(59.0, jds.doubleValue(), 1.0e-8);
		assertEquals(59, jds.getJulianDayNumber());

		jds = JulianDate.of(366.0);
		assertEquals("-4711-01-01T12:00", jds.toLocalDateTime().toString());
		assertEquals(366.0, jds.doubleValue(), 1.0e-8);
		assertEquals(366, jds.getJulianDayNumber());

		jds = JulianDate.of(731.0);
		assertEquals("-4710-01-01T12:00", jds.toLocalDateTime().toString());
		assertEquals(731.0, jds.doubleValue(), 1.0e-8);
		assertEquals(731, jds.getJulianDayNumber());

		jds = JulianDate.of(1721058.0);
		assertEquals("0000-01-01T12:00", jds.toLocalDateTime().toString());
		assertEquals(1721058.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721058, jds.getJulianDayNumber());

		jds = JulianDate.of(1721057.0);
		assertEquals("-0001-12-31T12:00", jds.toLocalDateTime().toString());
		assertEquals(1721057.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721057, jds.getJulianDayNumber());

		jds = JulianDate.of(1721117.0);
		assertEquals("0000-02-29T12:00", jds.toLocalDateTime().toString());
		assertEquals(1721117.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721117, jds.getJulianDayNumber());

		jds = JulianDate.of(1721118.0);
		assertEquals("0000-03-01T12:00", jds.toLocalDateTime().toString());
		assertEquals(1721118.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721118, jds.getJulianDayNumber());

		jds = JulianDate.of(1721423.0);
		assertEquals("0000-12-31T12:00", jds.toLocalDateTime().toString());
		assertEquals(1721423.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721423, jds.getJulianDayNumber());

		jds = JulianDate.of(1721424.0);
		assertEquals("0001-01-01T12:00", jds.toLocalDateTime().toString());
		assertEquals(1721424.0, jds.doubleValue(), 1.0e-8);
		assertEquals(1721424, jds.getJulianDayNumber());

		jds = JulianDate.of(2440587.5);
		assertEquals("1970-01-01T00:00", jds.toLocalDateTime().toString());
		assertEquals(2440587.5, jds.doubleValue(), 1.0e-8);
		assertEquals(2440588, jds.getJulianDayNumber());

		jds = JulianDate.of(2451774.726007);
		assertEquals("2000-08-18T05:25:27.004", jds.toLocalDateTime().toString());
		assertEquals(2451774.726007, jds.doubleValue(), 1.0e-8);
		assertEquals(2451775, jds.getJulianDayNumber());
		JulianDate time2 = new JulianDate(jds.doubleValue());
		assertEquals(time2, jds);

		jds = JulianDate.of(2451774.72600701);
		assertEquals("2000-08-18T05:25:27.005", jds.toLocalDateTime().toString());
		assertEquals(2451774.72600701, jds.doubleValue(), 1.0e-8);
		assertEquals(2451775, jds.getJulianDayNumber());

		jds = JulianDate.of(2451774.72600702);
		assertEquals("2000-08-18T05:25:27.006", jds.toLocalDateTime().toString());
		assertEquals(2451774.72600702, jds.doubleValue(), 1.0e-8);
		assertEquals(2451775, jds.getJulianDayNumber());

		jds = JulianDate.of(2299160.49998901);
		assertEquals("1582-10-04T23:59:59.050", jds.toLocalDateTime().toString());
		assertEquals(2299160.49998901, jds.doubleValue(), 1.0e-8);
		assertEquals(2299160, jds.getJulianDayNumber());

		jds = JulianDate.of(2299160.5);
		assertEquals("1582-10-15T00:00", jds.toLocalDateTime().toString());
		assertEquals(2299160.5, jds.doubleValue(), 1.0e-8);
		assertEquals(2299161, jds.getJulianDayNumber());

		jds = JulianDate.of(2147438064.499989);
		assertEquals("+5874773-08-15T23:59:59.052", jds.toLocalDateTime().toString());
		assertEquals(2147438064.499989, jds.doubleValue(), 1.0e-8);
		assertEquals(2147438064, jds.getJulianDayNumber());
	}

	@Test
	void testHttpTime() {
		long millis = System.currentTimeMillis();

		millis = (millis / 1000) * 1000;

		String time = TimeUtil.formatHttpDate(millis);

		long millisBack = TimeUtil.parseHttpTime(time);

		assertEquals(millis, millisBack);
	}

}
