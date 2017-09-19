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

public class PeriodTest {

	@Test
	public void testClose() {
		JDateTime jdt1 = new JDateTime(2013, 1, 28, 0, 0, 0, 0);
		JDateTime jdt2 = new JDateTime(2013, 1, 28, 1, 0, 0, 0);

		Period period = new Period(jdt1, jdt2);

		assertEquals(0, period.getDays());
		assertEquals(1, period.getHours());
		assertEquals(0, period.getMinutes());
		assertEquals(0, period.getSeconds());

		jdt1 = new JDateTime(2013, 1, 27, 23, 0, 0, 0);
		jdt2 = new JDateTime(2013, 1, 28, 1, 0, 0, 0);

		period = new Period(jdt1, jdt2);

		assertEquals(0, period.getDays());
		assertEquals(2, period.getHours());
		assertEquals(0, period.getMinutes());
		assertEquals(0, period.getSeconds());
	}

	@Test
	public void testMinuses() {
		JDateTime jdt1 = new JDateTime(2013, 1, 27, 23, 59, 59, 999);
		JDateTime jdt2 = new JDateTime(2013, 1, 28, 0, 0, 0, 0);

		Period period = new Period(jdt2, jdt1);

		assertEquals(0, period.getDays());
		assertEquals(0, period.getHours());
		assertEquals(0, period.getMinutes());
		assertEquals(0, period.getSeconds());
		assertEquals(1, period.getMilliseconds());
	}

	@Test
	public void testSame() {
		JDateTime jdt = new JDateTime();

		Period period = new Period(jdt, jdt);
		assertEquals(0, period.getDays());
		assertEquals(0, period.getHours());
		assertEquals(0, period.getMinutes());
		assertEquals(0, period.getSeconds());
		assertEquals(0, period.getMilliseconds());
	}

	@Test
	public void testOneDay() {
		JDateTime jdt1 = new JDateTime();
		JDateTime jdt2 = jdt1.clone();
		jdt2.addDay(1);

		Period period = new Period(jdt2, jdt1);
		assertEquals(1, period.getDays());
		assertEquals(0, period.getHours());
		assertEquals(0, period.getMinutes());
		assertEquals(0, period.getSeconds());
		assertEquals(0, period.getMilliseconds());
	}

	@Test
	public void testYear() {
		JDateTime jdt1 = new JDateTime(2013, 1, 1);
		JDateTime jdt2 = new JDateTime(2012, 1, 1);

		Period period = new Period(jdt2, jdt1);
		assertEquals(366, period.getDays());
		assertEquals(0, period.getHours());
		assertEquals(0, period.getMinutes());
		assertEquals(0, period.getSeconds());
		assertEquals(0, period.getMilliseconds());
	}
}
