// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbManager;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.pool.CoreConnectionPool;
import jodd.exception.UncheckedException;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.log.impl.NOPLogger;
import jodd.log.impl.NOPLoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * Abstract common DB integration test class.
 */
public abstract class DbBaseTest {

	public static final String DB_NAME = "jodd-test";

	protected CoreConnectionPool connectionPool;
	protected DbOomManager dboom;

	protected void init() {
		LoggerFactory.setLoggerFactory(new NOPLoggerFactory() {
			@Override
			public Logger getLogger(String name) {
				return new NOPLogger("") {
					@Override
					public boolean isWarnEnabled() {
						return true;
					}

					@Override
					public void warn(String message) {
						throw new UncheckedException("NO WARNINGS ALLOWED: " + message);
					}

					@Override
					public void warn(String message, Throwable throwable) {
						throw new UncheckedException("NO WARNINGS ALLOWED: " + message);
					}
				};
			}
		});
		DbOomManager.resetAll();

		dboom = DbOomManager.getInstance();

		connectionPool = new CoreConnectionPool();
	}

	protected void connect() {
		connectionPool.init();
		DbManager.getInstance().setConnectionProvider(connectionPool);
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

			// doesn't matter for mysql
			dboom.getTableNames().setLowercase(true);
			dboom.getColumnNames().setLowercase(true);
		}

	}

	/**
	 * PostgreSql.
	 */
	public abstract class PostgreSqlDbAccess extends DbAccess {

		public void initDb() {
			connectionPool.setDriver("org.postgresql.Driver");
			connectionPool.setUrl("jdbc:postgresql://localhost/" + DB_NAME);
			connectionPool.setUser("postgres");
			connectionPool.setPassword("root!");

			dboom.getTableNames().setLowercase(true);
			dboom.getColumnNames().setLowercase(true);
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

			dboom.getTableNames().setUppercase(true);
			dboom.getColumnNames().setUppercase(true);
		}
	}

}