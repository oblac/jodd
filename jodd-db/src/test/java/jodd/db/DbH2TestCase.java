// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;
import org.junit.After;
import org.junit.Before;

public abstract class DbH2TestCase {

	protected DbJtxTransactionManager dbtxm;
	protected CoreConnectionPool cp;


	@Before
	public void setUp() throws Exception {
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

	@After
	public void tearDown() throws Exception {
		dbtxm.close();
		cp.close();
		dbtxm = null;
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

