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
