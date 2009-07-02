// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.jtx.db.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;

import junit.framework.TestCase;

public abstract class DbHsqldbTestCase extends TestCase {

	protected DbJtxTransactionManager dbtxm;
	protected CoreConnectionPool cp;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dbtxm != null) {
			return;
		}
		cp = new CoreConnectionPool();
		cp.setDriver("org.hsqldb.jdbcDriver");
		cp.setUrl("jdbc:hsqldb:mem:test");

		cp.setUser("sa");
		cp.setPassword("");
		cp.init();
		dbtxm = new DbJtxTransactionManager(cp);
	}

	@Override
	protected void tearDown() throws Exception {
		dbtxm.close();
		cp.close();
		dbtxm = null;
		super.tearDown();
	}

	// ---------------------------------------------------------------- helpers

	protected int executeUpdate(DbSession session, String s) {
		return new DbQuery(session, s).executeUpdateAndClose();
	}

	protected void executeUpdate(String sql) {
		new DbQuery(sql).executeUpdateAndClose();
	}

	protected void executeCount(DbSession session, String s) {
		new DbQuery(session, s).executeCountAndClose();
	}


}
