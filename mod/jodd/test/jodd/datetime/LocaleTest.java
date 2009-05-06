// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import junit.framework.TestCase;
import jodd.util.LocaleUtil;

public class LocaleTest extends TestCase {

	public void testFrench() {
		JDateTime jdt = new JDateTime(2012, 12, 21);
		jdt.setLocale(LocaleUtil.getLocale("fr"));
		assertEquals("décembre", jdt.toString("MML"));
	}

}
