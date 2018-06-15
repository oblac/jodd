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

package jodd.time;

import jodd.time.JulianDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JulianDateTest {

	@Test
	void testSet() {
		JulianDate jdt = JulianDate.of(2008, 12, 20, 10, 44, 55, 0);
		JulianDate jdt2 = JulianDate.of(jdt.integer - 1, jdt.fraction);

		assertEquals(jdt.toLocalDateTime().getYear(), jdt2.toLocalDateTime().getYear());
		assertEquals(jdt.toLocalDateTime().getMonth(), jdt2.toLocalDateTime().getMonth());
		assertEquals(jdt.toLocalDateTime().getDayOfMonth() - 1, jdt2.toLocalDateTime().getDayOfMonth());
		assertEquals(jdt.toLocalDateTime().getHour(), jdt2.toLocalDateTime().getHour());
		assertEquals(jdt.toLocalDateTime().getMinute(), jdt2.toLocalDateTime().getMinute());
		assertEquals(jdt.toLocalDateTime().getSecond(), jdt2.toLocalDateTime().getSecond(), 0.0001);
	}

	@Test
	void testDecimalFloating() {
		LocalDateTime ldt = LocalDateTime.of(1970, 1, 13, 14, 24, 0, 0);
		JulianDate jdt = new JulianDate(2440600, 0.1);

		assertTrue(ldt.isEqual(jdt.toLocalDateTime()));

		JulianDate jdt2 = new JulianDate(2440600, 0.09999999991);
		assertTrue(ldt.isEqual(jdt2.toLocalDateTime()));

		JulianDate jdt3 = new JulianDate(2440600, 0.10000001);
		assertTrue(ldt.isEqual(jdt3.toLocalDateTime()));
	}
}
