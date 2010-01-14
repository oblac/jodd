// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.pool.CoreConnectionPool;
import jodd.jtx.db.DbJtxTransactionManager;

import junit.framework.TestCase;

public abstract class DbHsqldbTestCase extends TestCase {

	protected DbJtxTransactionManager dbtxm;
	protected CoreConnectionPool cp;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
