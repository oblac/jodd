// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AsmTests extends TestSuite {

	public AsmTests() {
		super("jodd.asm test suite");
		addTestSuite(AsmUtilTest.class);
	}

	public static Test suite() {
		return new AsmTests();
	}

}
