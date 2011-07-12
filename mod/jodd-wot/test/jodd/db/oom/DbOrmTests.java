// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import junit.framework.TestSuite;
import junit.framework.Test;
import jodd.db.oom.sqlgen.DbEntitySqlTest;

public class DbOrmTests extends TestSuite {

	public DbOrmTests() {
		super("jodd.db.orm test suite");
		addTestSuite(DbNameUtilTest.class);
		addTestSuite(DbOrmTest.class);
		addTestSuite(DbOrm2Test.class);
		addTestSuite(DbSqlTemplateTest.class);
		addTestSuite(DbSlqBuilderTest.class);
		addTestSuite(JointHintResolverTest.class);
		addTestSuite(DbEntitySqlTest.class);
		addTestSuite(MappingTest.class);
	}

	public static Test suite() {
		return new DbOrmTests();
	}

}
