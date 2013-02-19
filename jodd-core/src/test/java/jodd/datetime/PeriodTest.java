// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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
		JDateTime jdt2 = new JDateTime();
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
