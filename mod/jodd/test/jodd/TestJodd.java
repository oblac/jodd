// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Jodd JUnit TestSuite.
 */
public class TestJodd {

	public static Test suite() {
		TestSuite suite = new TestSuite("Jodd Java Library Test Suite");
		suite.addTest(jodd.bean.AllTests.suite());
		suite.addTest(jodd.cache.AllTests.suite());
		suite.addTest(jodd.introspector.AllTests.suite());
		suite.addTest(jodd.io.AllTests.suite());
		suite.addTest(jodd.format.AllTests.suite());
		suite.addTest(jodd.datetime.AllTests.suite());
		suite.addTest(jodd.servlet.AllTests.suite());
		suite.addTest(jodd.util.AllTests.suite());
		return suite;
	}
}
