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

package jodd.db;

import jodd.db.connection.ConnectionProvider;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.DbOomConfig;
import jodd.db.oom.DbOomQuery;
import jodd.db.oom.DbSqlGenerator;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.EmptyQueryMap;
import jodd.db.querymap.QueryMap;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * Starting class that all DBOOM starts from.
 * It encapsulate the database and works like a factory for all
 * db-related classes. This way you can have multiple connections.
 */
public class DbOom {

	private static final Logger log = LoggerFactory.getLogger(DbOom.class);

	/**
	 * The first DbOom is the default one, when only one is set.
	 * When you have two or more DbOom's the default dbOom is deregistered.
	 */
	private static DbOom defaultDbOom;

	/**
	 * Returns default DbOom instance. If multiple DbOoms are in use,
	 * an exception is thrown.
	 */
	public static DbOom get() {
		if (defaultDbOom == null) {
			throw new DbSqlException(
				"No default DbOom available. Use DbOom to create one.\n" +
				"If more then one DbOom is in use, there is no default instance.");
		}
		return defaultDbOom;
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Creates a new DbOom builder.
	 */
	public static Builder create() {
		return new Builder();
	}

	public static class Builder {
		private ConnectionProvider connectionProvider;
		private DbSessionProvider dbSessionProvider;
		private QueryMap queryMap;

		public Builder withSessionProvider(final DbSessionProvider sessionProvider) {
			this.dbSessionProvider = sessionProvider;
			return this;
		}

		public Builder withConnectionProvider(final ConnectionProvider connectionProvider) {
			this.connectionProvider = connectionProvider;
			return this;
		}

		public Builder withQueryMap(final QueryMap queryMap) {
			this.queryMap = queryMap;
			return this;
		}

		/**
		 * Creates new DbOom.
		 */
		public DbOom get() {
			if (connectionProvider == null) {
				connectionProvider = new CoreConnectionPool();
			}
			if (dbSessionProvider == null) {
				dbSessionProvider = new ThreadDbSessionProvider();
			}
			if (queryMap == null) {
				queryMap = new EmptyQueryMap();
			}
			return new DbOom(connectionProvider, dbSessionProvider, queryMap);
		}
	}

	private final ConnectionProvider connectionProvider;
	private final DbSessionProvider dbSessionProvider;
	private final QueryMap queryMap;

	private final DbQueryConfig dbQueryConfig;
	private final DbOomConfig dbOomConfig;
	private final DbEntityManager dbEntityManager;
	private final DbEntitySql dbEntitySql;

	// ---------------------------------------------------------------- class

	/**
	 * Creates new DbOom.
	 */
	public DbOom(
			final ConnectionProvider connectionProvider,
			final DbSessionProvider dbSessionProvider,
			final QueryMap queryMap) {
		this.connectionProvider = connectionProvider;
		this.dbSessionProvider = dbSessionProvider;
		this.queryMap = queryMap;

		this.dbOomConfig = new DbOomConfig();
		this.dbQueryConfig = new DbQueryConfig();
		this.dbEntityManager = new DbEntityManager(dbOomConfig);
		this.dbEntitySql = new DbEntitySql(this);

		// static init

		if (defaultDbOom == null) {
			log.info("Default DbOom is created.");
			defaultDbOom = this;
		}
		else {
			log.warn("Multiple DbOom detected.");
			defaultDbOom = null;
		}
	}

	/**
	 * Initializes the DbOom by connecting to the database. Database will be detected
	 * and DbOom will be configured to match it.
	 */
	public DbOom connect() {
		connectionProvider.init();

		final DbDetector dbDetector = new DbDetector();

		dbDetector.detectDatabaseAndConfigureDbOom(connectionProvider, dbOomConfig);

		return this;
	}

	/**
	 * Closes the DbOom.
	 */
	public void shutdown() {
		if (defaultDbOom == this) {
			defaultDbOom = null;
		}
		connectionProvider.close();
	}

	// ---------------------------------------------------------------- instances

	/**
	 * Returns DbOom configuration.
	 */
	public DbOomConfig config() {
		return dbOomConfig;
	}

	/**
	 * Returns default query configuration.
	 */
	public DbQueryConfig queryConfig() {
		return dbQueryConfig;
	}

	/**
	 * Returns DbOoom entity manager.
	 */
	public DbEntityManager entityManager() {
		return dbEntityManager;
	}

	/**
	 * Returns DbSession provider.
	 */
	public DbSessionProvider sessionProvider() {
		return dbSessionProvider;
	}

	/**
	 * Returns connection provider.
	 */
	public ConnectionProvider connectionProvider() {
		return connectionProvider;
	}

	/**
	 * Returns a query map.
	 */
	public QueryMap queryMap() {
		return queryMap;
	}

	// ---------------------------------------------------------------- entity

	/**
	 * Returns Entity SQL factory.
	 */
	public DbEntitySql entities() {
		return dbEntitySql;
	}

	// ---------------------------------------------------------------- factories

	/**
	 * Creates new T-SQL builder.
	 */
	public DbSqlBuilder sql(final String sql) {
		return new DbSqlBuilder(this, sql);
	}

	/**
	 * Creates new empty T-SQL builder.
	 */
	public DbSqlBuilder sql() {
		return new DbSqlBuilder(this);
	}

	/**
	 * Creates a query.
	 */
	public DbOomQuery query(final String sql) {
		return new DbOomQuery(this, sessionProvider().getDbSession(), sql);
	}

	/**
	 * Creates a query from a sql generator.
	 */
	public DbOomQuery query(final DbSqlGenerator sql) {
		return new DbOomQuery(this, sessionProvider().getDbSession(), sql);
	}

}