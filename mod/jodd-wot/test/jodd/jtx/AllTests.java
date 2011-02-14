// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.jtx test suite");
		addTestSuite(JtxManagerTest.class);
	}

	public static Test suite() {
		return new jodd.db.AllTests();
	}
}