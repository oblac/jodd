// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Jodd JUnit TestSuite.
 */
public class TestJodd {

	public static Test suite() {
		TestSuite suite = TestJoddFast.suite();
		suite.addTest(jodd.datetime.AllTests.suite());
		suite.addTest(jodd.util.AllTests.suite());
		return suite;
	}
}
