// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.props test suite");
		addTestSuite(PropsTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}