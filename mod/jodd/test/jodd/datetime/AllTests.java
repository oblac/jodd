// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.datetime test suite");
		addTestSuite(TimeUtilTest.class);
		addTestSuite(JDateTimeTest.class);
		addTestSuite(JDateTimeMoreTest.class);
		addTestSuite(ValidsTest.class);
		addTestSuite(JulianDateStampTest.class);
		addTestSuite(LocaleTest.class);
		addTestSuite(AltJdTest.class);
		addTestSuite(FormatterTest.class);
		addTestSuite(DstTest.class);
		addTestSuite(TimeZoneTest.class);
		addTestSuite(BeforeAfterTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}
}