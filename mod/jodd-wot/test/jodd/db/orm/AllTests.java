// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.db.orm test suite");
		addTestSuite(DbNameUtilTest.class);
		addTestSuite(DbOrmTest.class);
		addTestSuite(DbOrm2Test.class);
		addTestSuite(DbSqlTemplateTest.class);
		addTestSuite(DbSlqBuilderTest.class);
		addTestSuite(JointHintResolverTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}
