// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.format test suite");
		addTestSuite(FormatTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}