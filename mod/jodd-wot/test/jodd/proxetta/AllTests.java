// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.proxetta test suite");
		addTestSuite(SubclassTest.class);
		addTestSuite(BigClassTest.class);
		addTestSuite(AbstractsTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}