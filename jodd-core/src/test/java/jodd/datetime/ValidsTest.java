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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ValidsTest {

	@Test
	public void testValidDateTime() {
		assertTrue(TimeUtil.isValidDate(2002, 1, 31));
		assertFalse(TimeUtil.isValidDate(2002, 1, 32));
		assertFalse(TimeUtil.isValidDate(2002, 2, 29));
		assertFalse(TimeUtil.isValidDate(2002, 2, 0));
		assertFalse(TimeUtil.isValidDate(2002, 0, 1));
		assertFalse(TimeUtil.isValidDate(2002, 13, 29));
		assertTrue(TimeUtil.isValidDate(2002, 12, 29));
		assertTrue(TimeUtil.isValidDate(2000, 2, 29));
		assertFalse(TimeUtil.isValidDate(1900, 2, 29));

		assertTrue(TimeUtil.isValidTime(0, 0, 0, 0));
		assertFalse(TimeUtil.isValidTime(0, 0, 60, 0));
		assertFalse(TimeUtil.isValidTime(0, 60, 0, 0));
		assertTrue(TimeUtil.isValidTime(0, 59, 0, 0));
		assertFalse(TimeUtil.isValidTime(24, 0, 0, 0));
		assertTrue(TimeUtil.isValidTime(23, 0, 0, 0));
		assertTrue(TimeUtil.isValidTime(23, 59, 0, 0));
		assertTrue(TimeUtil.isValidTime(23, 59, 59, 0));
		assertTrue(TimeUtil.isValidTime(23, 59, 59, 999));

		assertTrue(TimeUtil.isValidDateTime(2000, 2, 29, 23, 59, 59, 999));
		assertFalse(TimeUtil.isValidDateTime(2001, 2, 29, 23, 59, 59, 999));
		assertFalse(TimeUtil.isValidDateTime(2000, -1, 79, 23, 59, 59, 999));
		assertFalse(TimeUtil.isValidDateTime(2000, 1, 79, 23, 59, 59, 999));
	}


	@Test
	public void testIsValid() {
		JDateTime jdt = new JDateTime();
		assertTrue(jdt.isValid("2002-01-31"));
		assertTrue(jdt.isValid("2002-1-31"));
		assertFalse(jdt.isValid("2002-1-32"));
		assertFalse(jdt.isValid("2002-2-29"));
		assertFalse(jdt.isValid("2002-02-29"));
		assertFalse(jdt.isValid("2002-02-0"));
		assertFalse(jdt.isValid("2002-2-0"));
		assertFalse(jdt.isValid("2002-0-01"));
		assertFalse(jdt.isValid("2002-00-01"));
		assertFalse(jdt.isValid("2002-13-29"));
		assertTrue(jdt.isValid("2002-12-29"));
		assertTrue(jdt.isValid("2000-2-29"));
		assertTrue(jdt.isValid("2000-02-29"));
		assertFalse(jdt.isValid("1900-2-29"));

		assertTrue(jdt.isValid("2002-1-1"));
		assertTrue(jdt.isValid("2002-01-01"));
		assertTrue(jdt.isValid("2002-1-01"));
		assertTrue(jdt.isValid("2002-01-1"));

		assertTrue(jdt.isValid("0-1-1"));

		assertTrue(jdt.isValid("0-1-1 12"));
		assertTrue(jdt.isValid("0-1-1 12:23"));
		assertTrue(jdt.isValid("0-1-1 12:23:34"));
		assertTrue(jdt.isValid("0-1-1 12:23:00"));
		assertTrue(jdt.isValid("0-1-1 12:23:01"));
		assertTrue(jdt.isValid("0-1-1 12:23:0"));
		assertTrue(jdt.isValid("0-1-1 12:23:1"));
		assertTrue(jdt.isValid("0-1-1 12:23:34.567"));
		assertTrue(jdt.isValid("0-1-1 02:03:04.007"));
		assertTrue(jdt.isValid("0-1-1 2:3:4.007"));
		assertFalse(jdt.isValid("0-1-1 2:3:60.000"));
		assertTrue(jdt.isValid("0-1-1 2:3:59.999"));

		assertFalse(jdt.isValid("a-a-a a:a:a"));
		assertFalse(jdt.isValid("z-1-1 2:3:4.007"));
		assertFalse(jdt.isValid("2-A-1 2:3:4.007"));
		assertFalse(jdt.isValid("2-3-1 2:3:  .4.007"));
		assertTrue(jdt.isValid("2-3-1 2:3:  4.007"));
	}

	@Test
	public void testIsValid2() {
		JDateTime jdt = new JDateTime();
		String date = jdt.toString("YYYY-MM-DD");

		for (int sec = 0; sec < 60; sec++) {
			for (int ms = 0; ms < 1000; ms++) {
				String mss;
				if (ms < 10) {
					mss = "00" + ms;
				} else if (ms < 100) {
					mss = "0" + ms;
				} else {
					mss = String.valueOf(ms);
				}
				String s1 = date + " 00:00:" + sec + '.' + mss;
				assertTrue(jdt.isValid(s1));
			}
		}


	}

}

