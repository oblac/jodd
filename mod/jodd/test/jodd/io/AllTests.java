// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.io test suite");
		addTestSuite(StreamUtilTest.class);
		addTestSuite(FileUtilTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}