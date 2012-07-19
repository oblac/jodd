// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.tst.Tester;
import jodd.db.pool.CoreConnectionPool;
import junit.framework.TestCase;

import java.util.List;

/**
 * Live database test. Requires database services to be started.
 */
public class LiveDatabaseTest extends TestCase {

	public static final String DB_NAME = "jodd-test";

	CoreConnectionPool cp;
	DbOomManager dboom;

	protected void init() {
		dboom = DbOomManager.getInstance();

		dboom.setTableNameUppercase(false);

		dboom.reset();
		dboom.registerEntity(Tester.class);
	}

	// ---------------------------------------------------------------- mysql

	protected void mysql() {
		initMySql();
		createMySqlTables();
	}

	protected void initMySql() {
		cp = new CoreConnectionPool();
		cp.setDriver("com.mysql.jdbc.Driver");
		cp.setUrl("jdbc:mysql://localhost:3306/" + DB_NAME);
		cp.setUser("root");
		cp.setPassword("root!");
		cp.init();
	}

	protected void createMySqlTables() {
		DbSession session = new DbSession(cp);

		String sql = "create table TESTER (" +
							"ID			INT UNSIGNED NOT NULL AUTO_INCREMENT," +
							"NAME		VARCHAR(20)	not null," +
							"VALUE		INT NULL," +
							"primary key (ID)" +
							')';

		DbQuery query = new DbQuery(session, sql);
		query.executeUpdateAndClose();

		session.closeSession();
		assertTrue(query.isClosed());
	}

	// ---------------------------------------------------------------- drop

	protected void dropAllTables() {
		DbSession session = new DbSession(cp);

		DbQuery query = new DbQuery(session, "drop table TESTER");
		query.executeUpdateAndClose();

		session.closeSession();
		assertTrue(query.isClosed());
	}

	// ---------------------------------------------------------------- test

	public void testDb() {
		init();

		mysql();

		try {
			workoutEntity();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		} finally {
			dropAllTables();
		}
	}

	// ---------------------------------------------------------------- workout

	protected void workoutEntity() {
		DbSession session = new DbSession(cp);

		Tester tester = new Tester();
		tester.setName("one");
		tester.setValue(Integer.valueOf(7));


		DbOomQuery query = new DbOomQuery(session, DbEntitySql.insert(tester));
		query.executeUpdateAndClose();

		assertDb(session, "{1,one,7}");

		session.closeSession();
	}

	// ---------------------------------------------------------------- util

	protected void assertDb(DbSession dbSession, String expected) {
		DbOomQuery query = new DbOomQuery(dbSession, "select * from TESTER order by ID");
		List<Tester> testerList = query.list(Tester.class);

		StringBuilder sb = new StringBuilder();
		for (Tester tester : testerList) {
			sb.append(tester.toString());
		}

		assertEquals(expected, sb.toString());
	}

}
