// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import junit.framework.TestSuite;

/**
 * Faster test suite test.
 */
public class TestJoddFast {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Jodd Java Library Test Suite");
		suite.addTest(jodd.bean.AllTests.suite());
		suite.addTest(jodd.cache.AllTests.suite());
		suite.addTest(jodd.introspector.AllTests.suite());
		suite.addTest(jodd.io.AllTests.suite());
		suite.addTest(jodd.format.AllTests.suite());
		suite.addTest(jodd.servlet.AllTests.suite());
		suite.addTest(jodd.util.AllTestsFast.suite());
		suite.addTest(jodd.typeconverter.AllTests.suite());
		return suite;
	}
}
