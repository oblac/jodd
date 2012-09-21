// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import junit.framework.TestCase;
import jodd.jtx.db.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;

public abstract class DbH2TestCase extends TestCase {

	protected DbJtxTransactionManager dbtxm;
	protected CoreConnectionPool cp;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dbtxm != null) {
			return;
		}
		cp = new CoreConnectionPool();
		cp.setDriver("org.h2.Driver");
		cp.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

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

