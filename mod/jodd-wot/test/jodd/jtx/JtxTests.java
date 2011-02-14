// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import jodd.db.DbTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class JtxTests extends TestSuite {

	public JtxTests() {
		super("jodd.jtx test suite");
		addTestSuite(JtxManagerTest.class);
	}

	public static Test suite() {
		return new DbTests();
	}
}