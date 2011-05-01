// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class VtorTests extends TestSuite {

	public VtorTests() {
		super("jodd.vtor test suite");
		addTestSuite(AnnotationTest.class);
		addTestSuite(ManualTest.class);
		addTestSuite(ProfileTest.class);
		addTestSuite(ConstraintTest.class);
	}

	public static Test suite() {
		return new VtorTests();
	}

}