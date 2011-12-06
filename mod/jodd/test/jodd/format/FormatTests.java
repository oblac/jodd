// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FormatTests extends TestSuite {

	public FormatTests() {
		super("jodd.format test suite");
		addTestSuite(FormatTest.class);
	}

	public static Test suite() {
		return new FormatTests();
	}

}