// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Jodd JUnit TestSuite.
 */
public class TestJoddWot {

	public static Test suite() {
		TestSuite suite = new TestSuite("Jodd WOT Java Library Test Suite");
		suite.addTest(jodd.db.AllTests.suite());
		suite.addTest(jodd.db.orm.AllTests.suite());
		suite.addTest(jodd.petite.AllTests.suite());
		suite.addTest(jodd.madvoc.AllTests.suite());
		suite.addTest(jodd.proxetta.AllTests.suite());
		return suite;
	}
}
