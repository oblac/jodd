// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.bean test suite");
		addTestSuite(BeanUtilTest.class);
		addTestSuite(BeanUtilGenericsTest.class);
		addTestSuite(BeanCopyTest.class);
		addTestSuite(BeanToolTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}
