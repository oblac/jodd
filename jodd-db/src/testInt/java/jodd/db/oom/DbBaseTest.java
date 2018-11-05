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

import jodd.db.DbOom;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.pool.CoreConnectionPool;
import jodd.exception.UncheckedException;
import jodd.log.LoggerFactory;
import jodd.log.impl.NOPLogger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract common DB integration test class.
 */
public abstract class DbBaseTest {

	public static final String DB_NAME = "jodd_test";

	protected CoreConnectionPool connectionPool;
	protected DbOom dbOom;

	protected void init(DbAccess db) {
		initDbOom();
		db.initConnectionPool(connectionPool);
		dbOom.connect();
		db.configureAfterConnection();
	}

	private void initDbOom() {
		LoggerFactory.setLoggerProvider(name -> new NOPLogger("") {
			@Override
			public boolean isWarnEnabled() {
				return true;
			}

			@Override
			public void warn(final String message) {
				throw new UncheckedException("NO WARNINGS ALLOWED: " + message);
			}

			@Override
			public void warn(final String message, final Throwable throwable) {
				throw new UncheckedException("NO WARNINGS ALLOWED: " + message);
			}
		});
		connectionPool = new CoreConnectionPool();
		dbOom = DbOom.create().withConnectionProvider(connectionPool).get();
	}

	// ---------------------------------------------------------------- dbaccess

	public abstract class DbAccess {
		public abstract void initConnectionPool(CoreConnectionPool connectionPool);
		public abstract String createTableSql();
		public abstract String getTableName();

		public final void createTables() {
			DbSession session = new DbSession(connectionPool);

			DbQuery query = new DbQuery(DbOom.get(), session, createTableSql());
			query.executeUpdate();

			session.closeSession();
			assertTrue(query.isClosed());
		}

		protected void close() {
			DbSession session = new DbSession(connectionPool);

			DbQuery query = new DbQuery(DbOom.get(), session, "drop table " + getTableName());
			query.executeUpdate();

			session.closeSession();
			assertTrue(query.isClosed());

			DbOom.get().shutdown();
			connectionPool.close();
		}

		public void configureAfterConnection() {}
	}

	public static String dbhost() {
		return "localhost";
	}

	/**
	 * MySql.
	 */
	public abstract class MySqlDbAccess extends DbAccess {

		@Override
		public final void initConnectionPool(final CoreConnectionPool connectionPool) {
			connectionPool.setDriver("com.mysql.cj.jdbc.Driver");
			connectionPool.setUrl("jdbc:mysql://" + dbhost() + ":3306/" + DB_NAME);
			connectionPool.setUser("root");
			connectionPool.setPassword("root!");
		}

		@Override
		public void configureAfterConnection() {
			DbOom.get().config().getTableNames().setUppercase(true);
			DbOom.get().config().getColumnNames().setUppercase(true);
		}
	}

	/**
	 * PostgreSql.
	 */
	public abstract class PostgreSqlDbAccess extends DbAccess {

		@Override
		public void initConnectionPool(final CoreConnectionPool connectionPool) {
			connectionPool.setDriver("org.postgresql.Driver");
			connectionPool.setUrl("jdbc:postgresql://" + dbhost() + "/" + DB_NAME);
			connectionPool.setUser("postgres");
			connectionPool.setPassword("root!");
		}
	}

	/**
	 * MS SQL.
	 */
	public abstract class MsSqlDbAccess extends DbAccess {

		@Override
		public void initConnectionPool(final CoreConnectionPool connectionPool) {
			connectionPool.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connectionPool.setUrl("jdbc:sqlserver://" + dbhost() + ":1433;" + "databaseName=" + DB_NAME);
			connectionPool.setUser("sa");
			connectionPool.setPassword("root!R00t!");
		}
	}

	/**
	 * HsqlDB.
	 */
	public abstract class HsqlDbAccess extends DbAccess {

		@Override
		public final void initConnectionPool(final CoreConnectionPool connectionPool) {
			connectionPool.setDriver("org.hsqldb.jdbcDriver");
			connectionPool.setUrl("jdbc:hsqldb:mem:test");
			connectionPool.setUser("sa");
			connectionPool.setPassword("");

			DbOom.get().config().getTableNames().setUppercase(true);
			DbOom.get().config().getColumnNames().setUppercase(true);
		}


	}

}
