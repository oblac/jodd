// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import junit.framework.TestSuite;
import junit.framework.Test;

public class DbTests extends TestSuite {

	public DbTests() {
		super("jodd.db test suite");
		addTestSuite(DbQueryTest.class);
		addTestSuite(DbMiscTest.class);
		addTestSuite(DbTransactionTest.class);
		addTestSuite(DbJtxTransactionManagerTest.class);
		addTestSuite(DbPropagationTest.class);
	}

	public static Test suite() {
		return new DbTests();
	}

}
