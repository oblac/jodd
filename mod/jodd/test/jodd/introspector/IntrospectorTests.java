// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IntrospectorTests extends TestSuite {

	public IntrospectorTests() {
		super("jodd.introspector test suite");
		addTestSuite(IntrospectorTest.class);
	}

	public static Test suite() {
		return new IntrospectorTests();
	}

}
