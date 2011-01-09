// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.paramo test suite");
		addTestSuite(ParamoTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}
