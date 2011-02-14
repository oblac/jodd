// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PetiteTests extends TestSuite {

	public PetiteTests() {
		super("jodd.petite test suite");
		addTestSuite(WireTest.class);
		addTestSuite(ScopeTest.class);
		addTestSuite(MiscTest.class);
		addTestSuite(ManualTest.class);
		addTestSuite(PropertyTest.class);
		addTestSuite(ParamTest.class);
	}

	public static Test suite() {
		return new PetiteTests();
	}

}
