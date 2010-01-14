// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import junit.framework.TestCase;


public class ValidsTest extends TestCase {

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

