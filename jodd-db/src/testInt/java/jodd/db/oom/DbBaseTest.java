// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
			query.executeUpdate();

			session.closeSession();
			assertTrue(query.isClosed());
		}

		protected void close() {
			DbSession session = new DbSession();

			DbQuery query = new DbQuery(session, "drop table " + getTableName());
			query.executeUpdate();

			session.closeSession();
			assertTrue(query.isClosed());

			connectionPool.close();
		}
	}

	public static String dbhost() {
		return "localhost";
	}

	/**
	 * MySql.
	 */
	public abstract class MySqlDbAccess extends DbAccess {

		public final void initDb() {
			connectionPool.setDriver("com.mysql.jdbc.Driver");
			connectionPool.setUrl("jdbc:mysql://" + dbhost() + ":3306");
			connectionPool.setUser("root");
			connectionPool.setPassword("root!");

			dboom.getTableNames().setUppercase(true);
			dboom.getColumnNames().setUppercase(true);

			//dboom.getTableNames().setLowercase(true);
			//dboom.getColumnNames().setLowercase(true);

			connectionPool.init();

			DbSession session = new DbSession(connectionPool);
			DbQuery query = new DbQuery(session, "create database IF NOT EXISTS `jodd-test` CHARACTER SET utf8 COLLATE utf8_general_ci;");
			query.executeUpdate();
			session.closeSession();

			connectionPool.close();
			connectionPool.setUrl("jdbc:mysql://" + dbhost() + ":3306/" + DB_NAME);
		}
	}

	/**
	 * PostgreSql.
	 */
	public abstract class PostgreSqlDbAccess extends DbAccess {

		public void initDb() {
			connectionPool.setDriver("org.postgresql.Driver");
			connectionPool.setUrl("jdbc:postgresql://" + dbhost() + "/" + DB_NAME);
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