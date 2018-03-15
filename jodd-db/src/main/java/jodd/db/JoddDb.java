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
import jodd.db.oom.JoinHintResolver;
import jodd.db.oom.sqlgen.SqlGenConfig;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.EmptyQueryMap;
import jodd.db.querymap.QueryMap;

import java.util.Objects;

/**
 * Jodd DB module.
 */
public class JoddDb {

	private static final JoddDb instance = new JoddDb();

	public static JoddDb defaults() {
		return instance;
	}

	// ---------------------------------------------------------------- settings

	private ConnectionProvider connectionProvider = new CoreConnectionPool();
	private DbSessionProvider sessionProvider = new ThreadDbSessionProvider();
	private QueryMap queryMap = new EmptyQueryMap();
	private JoinHintResolver hintResolver = new JoinHintResolver();
	private DbEntityManager dbEntityManager = new DbEntityManager();
	private boolean debug = false;
	private DbTransactionMode transactionMode = new DbTransactionMode();
	private DbQueryConfig queryConfig = new DbQueryConfig();
	private SqlGenConfig sqlGenConfig = new SqlGenConfig();

	public boolean isDebug() {
		return debug;
	}

	/**
	 * Enables debug mode.
	 */
	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

	public DbTransactionMode getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(final DbTransactionMode transactionMode) {
		this.transactionMode = transactionMode;
	}

	public DbQueryConfig getQueryConfig() {
		return queryConfig;
	}

	public void setQueryConfig(final DbQueryConfig queryConfig) {
		this.queryConfig = queryConfig;
	}

	protected DbOomConfig dbOomConfig = new DbOomConfig();

	public DbOomConfig getDbOomConfig() {
		return dbOomConfig;
	}

	public void setDbOomConfig(final DbOomConfig dbOomConfig) {
		this.dbOomConfig = dbOomConfig;
	}

	/**
	 * Returns {@link SqlGenConfig}.
	 */
	public SqlGenConfig getSqlGenConfig() {
		return sqlGenConfig;
	}

	public void setSqlGenConfig(final SqlGenConfig sqlGenConfig) {
		this.sqlGenConfig = sqlGenConfig;
	}

	/**
	 * Returns hints resolver.
	 */
	public JoinHintResolver getHintResolver() {
		return hintResolver;
	}

	/**
	 * Specifies the hint resolver.
	 */
	public JoddDb setHintResolver(final JoinHintResolver hintResolver) {
		Objects.requireNonNull(hintResolver);
		this.hintResolver = hintResolver;
		return this;
	}

	/**
	 * Returns the manager for db entities.
	 */
	public DbEntityManager getDbEntityManager() {
		return dbEntityManager;
	}

	public JoddDb setDbEntityManager(final DbEntityManager dbEntityManager) {
		Objects.requireNonNull(dbEntityManager);
		this.dbEntityManager = dbEntityManager;
		return this;
	}

	/**
	 * Returns the connection provider.
	 */
	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	/**
	 * Sets the connection provider.
	 */
	public JoddDb setConnectionProvider(final ConnectionProvider connectionProvider) {
		Objects.requireNonNull(connectionProvider);
		this.connectionProvider = connectionProvider;
		return this;
	}

	public DbSessionProvider getSessionProvider() {
		return sessionProvider;
	}

	/**
	 * Sets default session provider.
	 */
	public JoddDb setSessionProvider(final DbSessionProvider sessionProvider) {
		Objects.requireNonNull(sessionProvider);
		this.sessionProvider = sessionProvider;
		return this;
	}

	/**
	 * Returns {@link jodd.db.querymap.QueryMap} instance. May be <code>null</code>.
	 */
	public QueryMap getQueryMap() {
		return queryMap;
	}

	public JoddDb setQueryMap(final QueryMap queryMap) {
		Objects.requireNonNull(queryMap);
		this.queryMap = queryMap;
		return this;
	}

}