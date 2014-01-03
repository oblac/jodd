// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbManager;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.oom.tst.Tester;
import jodd.db.pool.CoreConnectionPool;
import jodd.util.SystemUtil;

import static org.junit.Assert.assertTrue;

public abstract class DbBaseTest {

	public static final String DB_NAME = "jodd-test";

	protected CoreConnectionPool connectionPool;
	protected DbOomManager dboom;

	protected void init(boolean strictCompare) {
		DbOomManager.resetAll();

		dboom = DbOomManager.getInstance();
		dboom.setStrictCompare(strictCompare);

		connectionPool = new CoreConnectionPool();
	}

	protected void connect() {
		connectionPool.init();

		DbManager.getInstance().setConnectionProvider(connectionPool);
		dboom.registerEntity(Tester.class);
	}

	// ---------------------------------------------------------------- dbaccess

	public abstract class DbAccess {
		public abstract void initDb();
		public abstract String getCreateTableSql();
		public abstract String getTableName();

		public final void createTables() {
			DbSession session = new DbSession();

			String sql = getCreateTableSql();

			DbQuery query = new DbQuery(session, sql);
			query.executeUpdateAndClose();

			session.closeSession();
			assertTrue(query.isClosed());
		}

		protected void close() {
			DbSession session = new DbSession();

			DbQuery query = new DbQuery(session, "drop table " + getTableName());
			query.executeUpdateAndClose();

			session.closeSession();
			assertTrue(query.isClosed());

			connectionPool.close();
		}

	}

	/**
	 * MySql.
	 */
	public abstract class MySqlDbAccess extends DbAccess {

		public final void initDb() {
			connectionPool.setDriver("com.mysql.jdbc.Driver");
			connectionPool.setUrl("jdbc:mysql://localhost:3306/" + DB_NAME);
			connectionPool.setUser("root");
			connectionPool.setPassword("root!");

			if (dboom.isStrictCompare()) {
				if (SystemUtil.isHostWindows() || SystemUtil.isHostMac()) {
					dboom.getTableNames().setLowercase(true);
				} else {
					dboom.getTableNames().setUppercase(true);
				}
				dboom.getColumnNames().setUppercase(true);
			}
		}

	}

	/**
	 * PostgreSql.
	 */
	public abstract class PostgreSqlDbAccess extends DbAccess {

		public final void initDb() {
			connectionPool.setDriver("org.postgresql.Driver");
			connectionPool.setUrl("jdbc:postgresql://localhost/" + DB_NAME);
			connectionPool.setUser("postgres");
			connectionPool.setPassword("root!");

			if (dboom.isStrictCompare()) {
				dboom.getTableNames().setLowercase(true);
				dboom.getColumnNames().setLowercase(true);
			}
		}
	}

	/**
	 * HsqlDB.
	 */
	public abstract class HsqlDbAccess extends DbAccess {

		public final void initDb() {
			connectionPool = new CoreConnectionPool();
			connectionPool.setDriver("org.hsqldb.jdbcDriver");
			connectionPool.setUrl("jdbc:hsqldb:mem:test");
			connectionPool.setUser("sa");
			connectionPool.setPassword("");

			if (dboom.isStrictCompare()) {
				dboom.getTableNames().setUppercase(true);
				dboom.getColumnNames().setUppercase(true);
			}
		}
	}

}