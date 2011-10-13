// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DecoraTests extends TestSuite {

	public DecoraTests() {
		super("jodd.decora test suite");
		addTestSuite(TestContentTypeHeaderResolver.class);
	}

	public static Test suite() {
		return new DecoraTests();
	}
}
