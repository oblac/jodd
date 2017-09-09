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
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates db connection. Initially works in auto-commit mode.
 * May start and work with transactions, after committing/rolling back
 * DbSession goes back to auto-commit mode.
 * <p>
 * All invoked queries are stored within one session and closed implicitly
 * on session closing.
 * <p>
 * For managed transaction see <code>DbJtxTransactionManager</code> from <b>jodd-tx</b>.
 */
public class DbSession implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(DbSession.class);

	// ---------------------------------------------------------------- init & close

	protected final DbManager dbManager = DbManager.getInstance();
	protected final ConnectionProvider connectionProvider;
	protected Connection connection;

	/**
	 * Creates new database session using default connection provider.
	 */
	public DbSession() {
		this(null);
	}

	/**
	 * Creates new database session with default transaction mode and in autocommit mode.
	 */
	public DbSession(ConnectionProvider connectionProvider) {
		log.debug("Creating new db session");

		if (connectionProvider == null) {
			connectionProvider = dbManager.connectionProvider;
			if (connectionProvider == null) {
				throw new DbSqlException("Connection provider is not available");
			}
		}
		this.connectionProvider = connectionProvider;
		txActive = false;
		txMode = dbManager.transactionMode;
		queries = new HashSet<>();
	}


	/**
	 * Closes current session and all allocated resources.
	 * All attached queries are closed. If a transaction is still active, exception occurs.
	 * Database connection is returned to the {@link ConnectionProvider}.
	 * Closed session is no longer available for usage.
	 */
	public void closeSession() {
		log.debug("Closing db session");

		SQLException sqlException = null;

		if (queries != null) {
			for (DbQueryBase query : queries) {
				SQLException sex = query.closeQuery();
				if (sex != null) {
					if (sqlException == null) {
						sqlException = sex;
					} else {
						sqlException.setNextException(sex);
					}
				}
			}
		}
		if (connection != null) {
			if (txActive) {
				throw new DbSqlException("TX was not closed before closing the session");
			}
			connectionProvider.closeConnection(connection);
			connection = null;
		}
		queries = null;
		if (sqlException != null) {
			throw new DbSqlException("Closing DbSession failed", sqlException);
		}
	}

	@Override
	public void close() throws Exception {
		closeSession();
	}

	/**
	 * Indicates whether a session is closed.
	 */
	public boolean isSessionClosed() {
		return queries == null;
	}

	/**
	 * Returns <code>true</code> if session is open.
	 */
	public boolean isSessionOpen() {
		return queries != null;
	}


	// ---------------------------------------------------------------- query

	/**
	 * Bag of all queries attached to this session. Explicitly closed queries
	 * remains in the set. If <code>null</code>, session is closed.
	 * If not <code>null</code>, but empty, session is still considered as open.
	 */
	protected Set<DbQueryBase> queries;

	/**
	 * Returns total number of queries assigned to this session.
	 */
	public int getTotalQueries() {
		if (queries == null) {
			return 0;
		}
		return queries.size();
	}

	/**
	 * Returns current connection.
	 */
	public Connection getConnection() {
		return connection;
	}


	/**
	 * Attaches a new {@link DbQuery}. May be invoked both inside and outside of transaction.
	 */
	protected void attachQuery(DbQueryBase query) {
		checkOpenSession();
		openConnectionForQuery();
		queries.add(query);
	}

	/**
	 * Detach used {@link DbQuery}. Usually invoked by {@link jodd.db.DbQuery#close()}.
	 */
	protected void detachQuery(DbQueryBase query) {
		queries.remove(query);
	}


	/**
	 * Opens connection in auto-commit mode, if already not opened.
	 */
	protected void openConnectionForQuery() {
		if (connection == null) {
			connection = connectionProvider.getConnection();
			txActive = false;	// txAction should already be false
			try {
				connection.setAutoCommit(true);
			} catch (SQLException sex) {
				throw new DbSqlException("Failed to open non-TX connection", sex);
			}
		}
	}

	// ---------------------------------------------------------------- transaction

	protected boolean txActive;

	protected DbTransactionMode txMode;

	/**
	 * Indicate whether a transaction is in progress.
	 */
	public boolean isTransactionActive() {
		return txActive;
	}


	/**
	 * Opens a transaction.
	 */
	protected void openTx() {
		if (connection == null) {
			connection = connectionProvider.getConnection();
		}
		txActive = true;
		try {
			connection.setAutoCommit(false);
			if (txMode.getIsolation() != DbTransactionMode.ISOLATION_DEFAULT) {
				connection.setTransactionIsolation(txMode.getIsolation());
			}
			connection.setReadOnly(txMode.isReadOnly());
		} catch (SQLException sex) {
			throw new DbSqlException("Open TX failed", sex);
		}
	}

	/**
	 * Closes current transaction.
	 */
	protected void closeTx() {
		txActive = false;
		try {
			connection.setAutoCommit(true);
		} catch (SQLException sex) {
			throw new DbSqlException("Close TX failed", sex);
		}
	}


	/**
	 * Starts a transaction.
	 */
	public void beginTransaction(DbTransactionMode mode) {
		log.debug("Beginning transaction");

		checkClosedTx();
		this.txMode = mode;
		openTx();
	}

	/**
	 * Starts transaction with default transaction mode.
	 */
	public void beginTransaction() {
		beginTransaction(dbManager.transactionMode);
	}

	/**
	 * Commit the current transaction, writing any unflushed changes to the database.
	 * Transaction mode is closed.
	 */
	public void commitTransaction() {
		log.debug("Committing transaction");

		checkActiveTx();
		try {
			connection.commit();
		} catch (SQLException sex) {
			throw new DbSqlException("Commit TX failed", sex);
		} finally {
			closeTx();
		}
	}

	/**
	 * Roll back the current transaction. Transaction mode is closed.
	 */
	public void rollbackTransaction() {
		log.debug("Rolling-back transaction");

		checkActiveTx();
		try {
			connection.rollback();
		} catch (SQLException sex) {
			throw new DbSqlException("Rollback TX failed", sex);
		} finally {
			closeTx();
		}
	}


	// ---------------------------------------------------------------- checking

	protected void checkOpenSession() {
		if (queries == null) {
			throw new DbSqlException("Session is closed");
		}
	}

	protected void checkClosedTx() {
		checkOpenSession();
		if (txActive) {
			throw new DbSqlException("TX already started for this session");
		}
	}

	protected void checkActiveTx() {
		checkOpenSession();
		if (!txActive) {
			throw new DbSqlException("TX not available for this session");
		}
	}
}