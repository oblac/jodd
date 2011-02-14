// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.datetime.DateTimeTests;
import jodd.util.UtilTests;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Jodd JUnit TestSuite.
 */
public class TestJodd {

	public static Test suite() {
		TestSuite suite = TestJoddFast.suite();
		suite.addTest(DateTimeTests.suite());
		suite.addTest(UtilTests.suite());
		return suite;
	}
}
