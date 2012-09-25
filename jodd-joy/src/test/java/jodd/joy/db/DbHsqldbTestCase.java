// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;
import org.junit.After;
import org.junit.Before;

public abstract class DbHsqldbTestCase {

	protected DbJtxTransactionManager dbtxm;
	protected CoreConnectionPool cp;


	@Before
	public void setUp() throws Exception {
		cp = new CoreConnectionPool();
		cp.setDriver("org.hsqldb.jdbcDriver");
		cp.setUrl("jdbc:hsqldb:mem:test");

		cp.setUser("sa");
		cp.setPassword("");
		cp.init();
		dbtxm = new DbJtxTransactionManager(cp);

		// initial data
		DbSession session = new DbSession(cp);

		executeUpdate(session, "drop table BOY if exists");
		executeUpdate(session, "drop table GIRL if exists");

		String sql = "create table GIRL (" +
				"ID			integer		not null," +
				"NAME		varchar(20)	not null," +
				"SPECIALITY	varchar(20)	null," +
				"primary key (ID)" +
				')';

		executeUpdate(session, sql);

		sql = "create table BOY (" +
				"ID			integer	not null," +
				"GIRL_ID		integer	null," +
				"NAME	varchar(20)	null," +
				"primary key (ID)," +
				"FOREIGN KEY (GIRL_ID) REFERENCES GIRL (ID)" +
				')';

		executeUpdate(session, sql);
		session.closeSession();
	}

	@After
	public void tearDown() throws Exception {
		dbtxm.close();
//		cp.close();
		dbtxm = null;
	}

	// ---------------------------------------------------------------- helpers

	protected int executeUpdate(DbSession session, String s) {
		return new DbQuery(session, s).executeUpdateAndClose();
	}

	protected void executeUpdate(String sql) {
		new DbQuery(sql).executeUpdateAndClose();
	}

	protected long executeCount(DbSession session, String s) {
		return new DbQuery(session, s).executeCountAndClose();
	}


}
