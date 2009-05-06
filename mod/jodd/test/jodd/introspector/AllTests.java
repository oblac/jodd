// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.introspector test suite");
		addTestSuite(IntrospectorTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}
