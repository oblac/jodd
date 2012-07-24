// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.connection.ConnectionProvider;

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
}
