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
import jodd.db.querymap.QueryMap;

/**
 * Db manager. Holds default Db configuration.
 */
@SuppressWarnings("RedundantFieldInitialization")
public class DbManager {

	// ---------------------------------------------------------------- singleton

	private static DbManager dbManager = new DbManager();

	/**
	 * Returns instance of DbManager.
	 */
	public static DbManager getInstance() {
		return dbManager;
	}

	/**
	 * Sets the DbManager instance.
	 */
	public static void setInstance(DbManager manager) {
		dbManager = manager;
	}

	/**
	 * Resets all settings to default by creating a new DbManager instance.
	 */
	public static void resetAll() {
		dbManager = new DbManager();
	}

	// ---------------------------------------------------------------- providers

	protected ConnectionProvider connectionProvider = null;
	protected DbSessionProvider sessionProvider = new ThreadDbSessionProvider();

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	/**
	 * Sets connection provider.
	 */
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public DbSessionProvider getSessionProvider() {
		return sessionProvider;
	}

	/**
	 * Sets default session provider.
	 */
	public void setSessionProvider(DbSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	// ---------------------------------------------------------------- query

	protected boolean forcePreparedStatement = false;
	protected int type = DbQuery.TYPE_FORWARD_ONLY;
	protected int concurrencyType = DbQuery.CONCUR_READ_ONLY;
	protected int holdability = DbQuery.DEFAULT_HOLDABILITY;
	protected int fetchSize = 0;
	protected int maxRows = 0;

	public boolean isForcePreparedStatement() {
		return forcePreparedStatement;
	}

	/**
	 * Enables creation of prepared statements for all queries.
	 */
	public void setForcePreparedStatement(boolean forcePreparedStatement) {
		this.forcePreparedStatement = forcePreparedStatement;
	}

	public int getType() {
		return type;
	}

	/**
	 * Sets default type.
	 * @see DbQuery#setType(int)
	 */
	public void setType(int type) {
		this.type = type;
	}

	public int getConcurrencyType() {
		return concurrencyType;
	}

	/**
	 * Sets default concurrency type.
	 * @see DbQuery#setConcurrencyType(int)
	 */
	public void setConcurrencyType(int concurrencyType) {
		this.concurrencyType = concurrencyType;
	}

	public int getHoldability() {
		return holdability;
	}

	/**
	 * Sets default holdability.
	 * @see DbQuery#setHoldability(int)
	 */
	public void setHoldability(int holdability) {
		this.holdability = holdability;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * Sets default value for fetch size.
	 * @see DbQuery#setFetchSize(int)
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Returns default value for max rows.
	 */
	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Sets default value for max rows.
	 * @see DbQuery#setMaxRows(int)
	 */
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	// ---------------------------------------------------------------- debug

	protected boolean debug = false;

	public boolean isDebug() {
		return debug;
	}

	/**
	 * Enables debug mode.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	// ---------------------------------------------------------------- tx

	protected DbTransactionMode transactionMode = new DbTransactionMode();

	public DbTransactionMode getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(DbTransactionMode transactionMode) {
		this.transactionMode = transactionMode;
	}

	// ---------------------------------------------------------------- sql map

	protected QueryMap queryMap;

	/**
	 * Returns {@link jodd.db.querymap.QueryMap} instance. May be <code>null</code>.
	 */
	public QueryMap getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
	}
}
