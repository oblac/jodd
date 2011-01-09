// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Encapsulates db connection. Initially works in auto-commit mode.
 * May start and work with transactions, after commiting/rolling back
 * DbSession goes back to auto-commit mode.
 * <p>
 * All invoked queries are stored within one session and closed implicitly
 * on session closing.
 * <p>
 * For managed transaction see {@link jodd.jtx.db.DbJtxTransactionManager}.
 */
public class DbSession {

	private static final Logger log = LoggerFactory.getLogger(DbSession.class);

	// ---------------------------------------------------------------- init & close

	protected final ConnectionProvider connectionProvider;
	protected Connection connection;

	/**
	 * Creates new database session using default connection provider.
	 */
	public DbSession() {
		this(DbDefault.connectionProvider);
	}

	/**
	 * Creates new database session with default transaction mode and in autocommit mode.
	 */
	public DbSession(ConnectionProvider connectionProvider) {
		log.debug("Creating new db session");
		if (connectionProvider == null) {
			throw new DbSqlException("Connection provider is not availiable.");
		}
		this.connectionProvider = connectionProvider;
		txActive = false;
		txMode = DbDefault.transactionMode;
		queries = new HashSet<DbQueryBase>();
	}


	/**
	 * Closes current session and all allocated resources.
	 * All attached queries are closed. If a transaction is still active, exception occurs.
	 * Database connection is returned to the {@link ConnectionProvider}.
	 * Closed session is no longer available for usage.
	 */
	public void closeSession() {
		log.debug("Closing db session");
		List<SQLException> allsexs = null;
		for (DbQueryBase query : queries) {
            List<SQLException> sexs = query.closeQuery();
			if (sexs != null) {
				if (allsexs == null) {
					allsexs = new ArrayList<SQLException>();
				}
				allsexs.addAll(sexs);
			}
		}
		if (connection != null) {
			if (txActive == true) {
				throw new DbSqlException("Transaction was not closed before closing the session.");
			}
			connectionProvider.closeConnection(connection);
			connection = null;
		}
		queries = null;
		if (allsexs != null) {
			throw new DbSqlException("Unable to close DbSession.", allsexs);
		}
	}

	/**
	 * Indicates whether a session is open.
	 */
	public boolean isSessionClosed() {
		return queries == null;
	}


	// ---------------------------------------------------------------- query

	/**
	 * Bag of all queries attached to this session. Explicitly closed queries
	 * remains in the bag. If <code>null</code> session is closed;
	 */
	protected Set<DbQueryBase> queries;

	/**
	 * Returns total number of queries assigned to this session.
	 */
	public int getTotalQueries() {
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
				throw new DbSqlException("Unable to open non-transactional connection.", sex);
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
			throw new DbSqlException("Unable to open and prepare transaction.", sex);
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
			throw new DbSqlException("Unable to prepare connection", sex);
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
		beginTransaction(DbDefault.transactionMode);
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
			throw new DbSqlException("Unable to commit transaction regulary.", sex);
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
			throw new DbSqlException("Unable to rollback transaction regulary.", sex);
		} finally {
			closeTx();
		}
	}


	// ---------------------------------------------------------------- checking

	protected void checkOpenSession() {
		if (queries == null) {
			throw new DbSqlException("Session is already closed.");
		}
	}

	protected void checkClosedTx() {
		checkOpenSession();
		if (txActive == true) {
			throw new DbSqlException("Transaction already started for this session.");
		}
	}

	protected void checkActiveTx() {
		checkOpenSession();
		if (txActive == false) {
			throw new DbSqlException("Transaction not available for this session.");
		}
	}
}