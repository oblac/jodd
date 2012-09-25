// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.util.LocaleUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocaleTest {

	@Test
	public void testFrench() {
		JDateTime jdt = new JDateTime(2012, 12, 21);
		jdt.setLocale(LocaleUtil.getLocale("fr"));
		assertEquals("décembre", jdt.toString("MML"));
	}

}
