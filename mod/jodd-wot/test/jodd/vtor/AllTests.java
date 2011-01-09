// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.vtor test suite");
		addTestSuite(AnnotationTest.class);
		addTestSuite(ManualTest.class);
		addTestSuite(ProfileTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}