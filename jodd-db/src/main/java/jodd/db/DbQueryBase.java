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

import jodd.db.debug.LogabbleStatementFactory;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jodd.db.DbQueryBase.State.CLOSED;
import static jodd.db.DbQueryBase.State.CREATED;
import static jodd.db.DbQueryBase.State.INITIALIZED;

/**
 * Support for {@link DbQuery} holds all configuration, initialization and the execution code.
 */
abstract class DbQueryBase<Q extends DbQueryBase> implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(DbQueryBase.class);

	protected final DbOom dbOom;

	// ---------------------------------------------------------------- ctor

	protected DbQueryBase(final DbOom dbOom) {
		this.dbOom = dbOom;
		this.forcePreparedStatement = dbOom.queryConfig().isForcePreparedStatement();
		this.type = dbOom.queryConfig().getType();
		this.concurrencyType = dbOom.queryConfig().getConcurrencyType();
		this.holdability = dbOom.queryConfig().getHoldability();
		this.debug = dbOom.queryConfig().isDebug();
		this.fetchSize = dbOom.queryConfig().getFetchSize();
		this.maxRows = dbOom.queryConfig().getMaxRows();
	}

	@SuppressWarnings("unchecked")
	protected Q _this() {
		return (Q) this;
	}


	// ---------------------------------------------------------------- query states

	/**
	 * Query states.
	 */
	public enum State {
		CREATED, INITIALIZED, CLOSED
	}
	protected State queryState = CREATED;

	/**
	 * Returns query state.
	 */
	public State getQueryState() {
		return queryState;
	}

	/**
	 * Checks if query is not closed and throws an exception if it is.
	 */
	protected void checkNotClosed() {
		if (queryState == CLOSED) {
			throw new DbSqlException(this, "Query is closed. Operation may be performed only on active queries.");
		}
	}

	/**
	 * Checks if query is created (and not yet initialized or closed) and throws an exception if it is not.
	 */
	protected void checkCreated() {
		if (queryState != CREATED) {
			final String message = (queryState == INITIALIZED ?
									"Query is already initialized." : "Query is closed.");
			throw new DbSqlException(this, message + " Operation may be performed only on created queries.");
		}
	}

	/**
	 * Checks if query is initialized and throws an exception if it is not.
	 */
	protected void checkInitialized() {
		if (queryState != INITIALIZED) {
			final String message = (queryState == CREATED ?
									"Query is created but not yet initialized." : "Query is closed.");
			throw new DbSqlException(this, message + " Operation may be performed only on initialized queries.");
		}
	}



	/**
	 * Returns <code>true</code> if query is closed.
	 */
	public boolean isClosed() {
		return queryState == CLOSED;
	}

	/**
	 * Returns <code>true</code> if query is active: created and possibly initialized.
	 * Opened query may be not initialized.
	 */
	public boolean isActive() {
		return queryState != CLOSED;
	}

	/**
	 * Returns <code>true</code> if query is initialized. Initialized query is the one that has
	 * created JDBC statements. 
	 */
	public boolean isInitialized() {
		return queryState == INITIALIZED;
	}

	// ---------------------------------------------------------------- input

	protected Connection connection;
	protected DbSession session;
	protected String sqlString;

	/**
	 * Returns used {@link DbSession}.
	 */
	public DbSession getSession() {
		return session;
	}

	// ---------------------------------------------------------------- statement

	protected Statement statement;
	protected PreparedStatement preparedStatement;
	protected CallableStatement callableStatement;
	protected Set<ResultSet> resultSets;
	protected DbQueryParser query;

	/**
	 * Stores result set.
	 */
	protected void saveResultSet(final ResultSet rs) {
		if (resultSets == null) {
			resultSets = new HashSet<>();
		}
		resultSets.add(rs);
	}

	// ---------------------------------------------------------------- configuration

	/**
	 * If set to <code>true</code> all created statements will be prepared.
	 */
	protected boolean forcePreparedStatement;

	/**
	 * Forces creation of prepared statements.
	 */
	public Q forcePreparedStatement() {
		checkCreated();
		forcePreparedStatement = true;
		return _this();
	}

	// ---------------------------------------------------------------- initialization

	/**
	 * Initializes the query if not already initialized.
	 * Usually, user doesn't have to invoke it at all, since it will
	 * be called before all methods that deals directly with JDBC statement.
	 * Throws an exception if called on closed query.
	 */
	public final void init() {
		checkNotClosed();
		if (queryState == INITIALIZED) {
			return;
		}
		initializeJdbc();
		queryState = INITIALIZED;
		prepareQuery();
	}

	/**
	 * Initializes session. When not specified (i.e. is <code>null</code>),
	 * session is fetched from session provider.
	 */
	protected void initSession(final DbSession session) {
		if (session != null) {
			this.session = session;
			return;
		}

		final DbSessionProvider dbSessionProvider = dbOom.sessionProvider();

		this.session = dbSessionProvider.getDbSession();
	}

	/**
	 * Performs JDBC initialization of the query. Obtains connection, parses the SQL query string
	 * and creates statements. Initialization is performed only once, when switching to initialized state.
	 */
	@SuppressWarnings("MagicConstant")
	protected void initializeJdbc() {
		// connection
		if (connection == null) {
			initSession(session);

			connection = session.getConnection();
		}

		this.query = new DbQueryParser(sqlString);

		// callable statement

		if (query.callable) {
			try {
				if (debug) {
					if (holdability != QueryHoldability.DEFAULT) {
						callableStatement = LogabbleStatementFactory.callable().prepareCall(
							connection, query.sql, type.value(), concurrencyType.value(), holdability.value());
					} else {
						callableStatement = LogabbleStatementFactory.callable().prepareCall(
							connection, query.sql, type.value(), concurrencyType.value());
					}
				}
				else {
					if (holdability != QueryHoldability.DEFAULT) {
						callableStatement = connection.prepareCall(
							query.sql, type.value(), concurrencyType.value(), holdability.value());
					} else {
						callableStatement = connection.prepareCall(
							query.sql, type.value(), concurrencyType.value());
					}
				}
			}
			catch (SQLException sex) {
				throw new DbSqlException(this, "Error creating callable statement", sex);
			}

			preparedStatement = callableStatement;
			statement = callableStatement;

			return;
		}

		// prepared statement

		if (query.prepared || forcePreparedStatement) {
			try {
				if (debug) {
					if (generatedColumns != null) {
						if (generatedColumns.length == 0) {
							preparedStatement = LogabbleStatementFactory.prepared().create(connection, query.sql, Statement.RETURN_GENERATED_KEYS);
						} else {
							preparedStatement = LogabbleStatementFactory.prepared().create(connection, query.sql, generatedColumns);
						}
					} else {
						if (holdability != QueryHoldability.DEFAULT) {
							preparedStatement = LogabbleStatementFactory.prepared().create(
								connection, query.sql, type.value(), concurrencyType.value(), holdability.value());
						} else {
							preparedStatement = LogabbleStatementFactory.prepared().create(
								connection, query.sql, type.value(), concurrencyType.value());
						}
					}
				} else {
					if (generatedColumns != null) {
						if (generatedColumns.length == 0) {
							preparedStatement = connection.prepareStatement(query.sql, Statement.RETURN_GENERATED_KEYS);
						} else {
							preparedStatement = connection.prepareStatement(query.sql, generatedColumns);
						}
					} else {
						if (holdability != QueryHoldability.DEFAULT) {
							preparedStatement = connection.prepareStatement(
								query.sql, type.value(), concurrencyType.value(), holdability.value());
						} else {
							preparedStatement = connection.prepareStatement(
								query.sql, type.value(), concurrencyType.value());
						}
					}
				}
			}
			catch (SQLException sex) {
				throw new DbSqlException(this, "Error creating prepared statement", sex);
			}

			statement = preparedStatement;

			return;
		}

		// statement

		try {
			if (holdability != QueryHoldability.DEFAULT) {
				statement = connection.createStatement(type.value(), concurrencyType.value(), holdability.value());
			} else {
				statement = connection.createStatement(type.value(), concurrencyType.value());
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Error creating statement", sex);
		}
	}

	/**
	 * Prepares the query just after the initialization.
	 * Query is fully set and ready.
	 */
	protected void prepareQuery() {
		if (fetchSize != 0) {
			setFetchSize(fetchSize);
		}
		if (maxRows != 0) {
			setMaxRows(maxRows);
		}
	}


	// ---------------------------------------------------------------- close

	protected boolean autoClose;

	/**
	 * Defines that query should be automatically closed immediately after using.
	 * Should be called before actual statement execution.
	 */
	public Q autoClose() {
		autoClose = true;
		return _this();
	}

	/**
	 * Closes all result sets opened by this query. Query remains active.
	 * Returns <code>SQLException</code> (stacked with all exceptions)
	 * or <code>null</code>.
	 */
	private SQLException closeQueryResultSets() {
		SQLException sqlException = null;

		if (resultSets != null) {
			for (final ResultSet rs : resultSets) {
				try {
					rs.close();
				} catch (SQLException sex) {
					if (sqlException == null) {
						sqlException = sex;
					} else {
						sqlException.setNextException(sex);
					}
				} finally {
					totalOpenResultSetCount--;
				}
			}
			resultSets.clear();
			resultSets = null;
		}
		return sqlException;
	}

	/**
	 * Closes all result sets created by this query. Query remains active.
	 */
	public Q closeAllResultSets() {
		final SQLException sex = closeQueryResultSets();
		if (sex != null) {
			throw new DbSqlException("Close associated ResultSets error", sex);
		}
		return _this();
	}

	/**
	 * Closes all assigned result sets and then closes the query. Query becomes closed.
	 */
	protected SQLException closeQuery() {
		SQLException sqlException = closeQueryResultSets();
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException sex) {
				if (sqlException == null) {
					sqlException = sex;
				} else {
					sqlException.setNextException(sex);
				}
			}
			statement = null;
		}
		query = null;
		queryState = CLOSED;
		return sqlException;
	}

	/**
	 * Closes the query and all created results sets and detaches itself from the session.
	 */
	@Override
	@SuppressWarnings({"ClassReferencesSubclass"})
	public void close() {
		final SQLException sqlException = closeQuery();
		connection = null;
		if (this.session != null) {
			this.session.detachQuery(this);
		}
		if (sqlException != null) {
			throw new DbSqlException("Close query error", sqlException);
		}
	}

	/**
	 * Closes single result set that was created by this query. It is not necessary to close result sets
	 * explicitly, since {@link DbQueryBase#close()} method closes all created result sets.
	 * Query remains active.
	 */
	public void closeResultSet(final ResultSet rs) {
		if (rs == null) {
			return;
		}
		if (!resultSets.remove(rs)) {
			throw new DbSqlException(this, "ResultSet is not created by this query");
		}
		try {
			rs.close();
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Close result set error", sex);
		} finally {
			totalOpenResultSetCount--;
		}
	}

	// ---------------------------------------------------------------- result set type

	protected QueryScrollType type;

	public QueryScrollType getType() {
		return type;
	}

	public Q setType(final QueryScrollType type) {
		checkCreated();
		this.type = type;
		return _this();
	}
	public Q typeForwardOnly() {
		setType(QueryScrollType.FORWARD_ONLY);
		return _this();
	}
	public Q typeScrollSensitive() {
		setType(QueryScrollType.SCROLL_SENSITIVE);
		return _this();
	}
	public Q typeScrollInsensitive() {
		setType(QueryScrollType.SCROLL_INSENSITIVE);
		return _this();
	}

	// ---------------------------------------------------------------- concurrency

	protected QueryConcurrencyType concurrencyType;

	public QueryConcurrencyType getConcurrencyType() {
		return concurrencyType;
	}

	public Q setConcurrencyType(final QueryConcurrencyType concurrencyType) {
		checkCreated();
		this.concurrencyType = concurrencyType;
		return _this();
	}
	public Q concurrentReadOnly() {
		setConcurrencyType(QueryConcurrencyType.READ_ONLY);
		return _this();
	}
	public Q concurrentUpdatable() {
		setConcurrencyType(QueryConcurrencyType.UPDATABLE);
		return _this();
	}

	// ---------------------------------------------------------------- holdability

	protected QueryHoldability holdability;

	public QueryHoldability getHoldability() {
		return holdability;
	}

	public Q setHoldability(final QueryHoldability holdability) {
		checkCreated();
		this.holdability = holdability;
		return _this();
	}

	public Q holdCursorsOverCommit() {
		setHoldability(QueryHoldability.HOLD_CURSORS_OVER_COMMIT);
		return _this();
	}

	public Q closeCursorsAtCommit() {
		setHoldability(QueryHoldability.CLOSE_CURSORS_AT_COMMIT);
		return _this();
	}

	// ---------------------------------------------------------------- debug mode

	protected boolean debug;

	public boolean isInDebugMode() {
		return debug;
	}

	public Q setDebug(final boolean debug) {
		checkCreated();
		this.debug = debug;
		return _this();
	}
	public Q setDebugMode() {
		setDebug(true);
		return _this();
	}


	// ---------------------------------------------------------------- generated keys

	protected String[] generatedColumns;

	/**
	 * Returns generated column names.
	 */
	public String[] getGeneratedColumnNames() {
		return generatedColumns;
	}

	/**
	 * Specifies columns which values will be generated by database.
	 */
	public Q setGeneratedColumns(final String... columns) {
		checkCreated();
		generatedColumns = columns;
		return _this();
	}

	/**
	 * Specifies that database will generate some columns values,
	 * usually the single id.
	 */
	public Q setGeneratedKey() {
		setGeneratedColumns();
		return _this();
	}

	/**
	 * Resets creating generated columns.
	 */
	public Q resetGeneratedColumns() {
		checkCreated();
		generatedColumns = null;
		return _this();
	}


	// ---------------------------------------------------------------- performance hints

	protected int fetchSize;

	/**
	 * Returns fetch size.
	 * @see #setFetchSize(int)
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database when
	 * more rows are needed. The number of rows specified affects only result sets created using this statement.
	 * If the value specified is zero, then the hint is ignored. The default value is zero.
	 * @see Statement#setFetchSize(int)
	 */
	public Q setFetchSize(final int rows) {
		checkNotClosed();
		this.fetchSize = rows;
		if (statement != null) {
			try {
				statement.setFetchSize(fetchSize);
			} catch (SQLException sex) {
				throw new DbSqlException(this, "Unable to set fetch size: " + fetchSize, sex);
			}
		}
		return _this();
	}

	protected int maxRows;

	/**
	 * Returns max rows.
	 * @see #setMaxRows(int)
	 */
	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Sets the limit for the maximum number of rows that any ResultSet object can contain to the given number.
	 * If the limit is exceeded, the excess rows are silently dropped. Zero means there is no limit.
	 * @see Statement#setMaxRows(int)
	 */
	public Q setMaxRows(final int maxRows) {
		checkNotClosed();
		this.maxRows = maxRows;
		if (statement != null) {
			try {
				statement.setMaxRows(maxRows);
			} catch (SQLException sex) {
				throw new DbSqlException(this, "Unable to set max rows: " + maxRows, sex);
			}
		}
		return _this();
	}

	// ---------------------------------------------------------------- execute
	protected long start;
	protected long elapsed = -1;

	/**
	 * Returns query execution elapsed time in milliseconds.
	 * Returns <code>-1</code> if query is still not executed.
	 */
	public long getExecutionTime() {
		return elapsed;
	}

	/**
	 * Executes the query. If this method is invoked at least once, the query or
	 * all created ResultSets must be explicitly closed at the end of query usage.
	 * This can be done explicitly by calling {@link DbQueryBase#closeResultSet(ResultSet)}
	 * оr implicitly, during {@link DbQueryBase#close()}.
	 * @see Statement#execute(String)
	 */
	public ResultSet execute() {
		start = System.currentTimeMillis();

		init();
		ResultSet rs = null;
		if (log.isDebugEnabled()) {
			log.debug("Executing statement: " + getQueryString());
		}
		try {
			if (preparedStatement == null) {
				rs = statement.executeQuery(query.sql);
			} else {
				rs = preparedStatement.executeQuery();
			}
			rs.setFetchSize(fetchSize);
		} catch (SQLException sex) {
			DbUtil.close(rs);
			throw new DbSqlException(this, "Query execution failed", sex);
		}
		saveResultSet(rs);
		totalOpenResultSetCount++;

		elapsed = System.currentTimeMillis() - start;
		if (log.isDebugEnabled()) {
			log.debug("execution time: " + elapsed + "ms");
		}
		return rs;
	}

	public DbCallResult executeCall() {
		start = System.currentTimeMillis();

		init();
		if (log.isDebugEnabled()) {
			log.debug("Calling statement: " + getQueryString());
		}
		try {
			callableStatement.execute();
		} catch (SQLException sex) {
			DbUtil.close(callableStatement);
			throw new DbSqlException(this, "Query execution failed", sex);
		}

		elapsed = System.currentTimeMillis() - start;
		if (log.isDebugEnabled()) {
			log.debug("execution time: " + elapsed + "ms");
		}
		return new DbCallResult(query, callableStatement);
	}

	/**
	 * Executes UPDATE, INSERT or DELETE queries. Query is not closed afterwards
	 * unless {@link #autoClose() auto close mode} is set.
	 * @see Statement#executeUpdate(String)
	 */
	public int executeUpdate() {
		return executeUpdate(autoClose);
	}

	/**
	 * Executes UPDATE, INSERT or DELETE queries and optionally closes the query.
	 * @see Statement#executeUpdate(String)
	 */
	protected int executeUpdate(final boolean closeQuery) {
		start = System.currentTimeMillis();

		init();
		final int result;
		if (log.isDebugEnabled()) {
			log.debug("Executing update: " + getQueryString());
		}
		try {
			if (preparedStatement == null) {
				if (generatedColumns != null) {
					if (generatedColumns.length == 0) {
						result = statement.executeUpdate(query.sql, Statement.RETURN_GENERATED_KEYS);
					} else {
						result = statement.executeUpdate(query.sql, generatedColumns);
					}
				} else {
					result = statement.executeUpdate(query.sql);
				}
			} else {
				result = preparedStatement.executeUpdate();
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Query execution failed", sex);
		}
		if (closeQuery) {
			close();
		}

		elapsed = System.currentTimeMillis() - start;
		if (log.isDebugEnabled()) {
			log.debug("execution time: " + elapsed + "ms");
		}
		return result;
	}

	/**
	 * Special execute() for 'select count(*)' queries. Query is not closed after the execution
	 * unless {@link #autoClose() auto-close mode} is set.
	 * Doesn't check if query is really a count query, so it would work for any
	 * query that has number in the first column of result.
	 * If result set returns zero rows (very unlikely), returns <code>-1</code>.
	 */
	public long executeCount() {
		return executeCount(autoClose);
	}

	/**
	 * Executes count queries and optionally closes query afterwards.
	 */
	protected long executeCount(final boolean close) {
		start = System.currentTimeMillis();

		init();
		ResultSet rs = null;
		if (log.isDebugEnabled()) {
			log.debug("Executing prepared count: " + getQueryString());
		}
		try {
			if (preparedStatement == null) {
				rs = statement.executeQuery(query.sql);
			} else {
				rs = preparedStatement.executeQuery();
			}

			final long firstLong = DbUtil.getFirstLong(rs);

			elapsed = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug("execution time: " + elapsed + "ms");
			}

			return firstLong;
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Count query failed", sex);
		} finally {
			DbUtil.close(rs);
			if (close) {
				close();
			}
		}
	}

	// ---------------------------------------------------------------- result set mapper

	/**
	 * {@link #execute() Executes} the query, iterates result set and
	 * {@link QueryMapper maps} each row.
	 */
	public <T> List<T> list(final QueryMapper<T> queryMapper) {
		final ResultSet resultSet = execute();

		final List<T> list = new ArrayList<>();

		try {
			while (resultSet.next()) {
				final T t = queryMapper.process(resultSet);
				if (t == null) {
					break;
				}
				list.add(t);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(sex);
		} finally {
			DbUtil.close(resultSet);
		}

		return list;
	}

	/**
	 * {@link #execute() Executes} the query and {@link QueryMapper maps} single result row.
	 */
	public <T> T find(final QueryMapper<T> queryMapper) {
		final ResultSet resultSet = execute();

		try {
			if (resultSet.next()) {
				return queryMapper.process(resultSet);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(sex);
		} finally {
			DbUtil.close(resultSet);
		}
		return null;
	}

	/**
	 * {@link #execute() Executes} the query, iterates all rows and
	 * {@link QueryMapper maps} them.
	 */
	public <T> Set<T> listSet(final QueryMapper<T> queryMapper) {
		final ResultSet resultSet = execute();

		final Set<T> set = new HashSet<>();

		try {
			while (resultSet.next()) {
				final T t = queryMapper.process(resultSet);
				if (t == null) {
					break;
				}
				set.add(t);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(sex);
		} finally {
			DbUtil.close(resultSet);
		}
		return set;
	}

	// ---------------------------------------------------------------- generated keys

	/**
	 * Returns generated columns.
	 */
	public ResultSet getGeneratedColumns() {
		checkInitialized();
		if (generatedColumns == null) {
			throw new DbSqlException(this, "No column is specified as auto-generated");
		}
		final ResultSet rs;
		try {
			rs = statement.getGeneratedKeys();
		} catch (SQLException sex) {
			throw new DbSqlException(this, "No generated keys", sex);
		}
		saveResultSet(rs);
		totalOpenResultSetCount++;
		return rs;
	}

	/**
	 * Returns generated key i.e. first generated column as <code>long</code>.
	 */
	public long getGeneratedKey() {
		checkInitialized();
		final ResultSet rs = getGeneratedColumns();
		try {
			return DbUtil.getFirstLong(rs);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "No generated key as long", sex);
		} finally {
			DbUtil.close(rs);
			resultSets.remove(rs);
			totalOpenResultSetCount--;
		}
	}

	public Object getGeneratedKeyObject() {
		checkInitialized();
		final ResultSet rs = getGeneratedColumns();
		try {
			return DbUtil.getFirstObject(rs);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "No generated key as long", sex);
		} finally {
			DbUtil.close(rs);
			resultSets.remove(rs);
			totalOpenResultSetCount--;
		}
	}


	// ---------------------------------------------------------------- query string

	/**
	 * Returns query SQL string. For prepared statements, returned sql string with quick-and-dirty replaced values.
	 */
	public String getQueryString() {
		if (debug) {
			if ((callableStatement != null)) {
				return LogabbleStatementFactory.callable().getQueryString(callableStatement);
			}
			if (preparedStatement != null) {
				return LogabbleStatementFactory.prepared().getQueryString(preparedStatement);
			}
		}
		if (query != null) {
			return query.sql;
		}
		return sqlString;
	}

	/**
	 * @see #getQueryString()
	 */
	@Override
	public String toString() {
		return getQueryString();
	}



	// ---------------------------------------------------------------- statistics


	protected static int totalOpenResultSetCount;

	/**
	 * Returns total number of open result sets.
	 * @see #getOpenResultSetCount()
	 */
	public static int getTotalOpenResultSetCount() {
		return totalOpenResultSetCount;
	}

	/**
	 * Returns number of created result sets that are still not explicitly closed.
	 * @see #getTotalOpenResultSetCount()
	 */
	public int getOpenResultSetCount() {
		return resultSets == null ? 0 : resultSets.size();
	}
}
