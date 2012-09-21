// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.debug.LoggablePreparedStatementFactory;
import jodd.log.Log;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Support for {@link DbQuery} holds all configuration, initialization and the execution code.
 */
abstract class DbQueryBase {

	private static final Log log = Log.getLogger(DbQueryBase.class);

	// ---------------------------------------------------------------- ctor

	protected final DbManager dbManager = DbManager.getInstance();

	protected DbQueryBase() {
		this.forcePreparedStatement = dbManager.forcePreparedStatement;
		this.type = dbManager.type;
		this.concurrencyType = dbManager.concurrencyType;
		this.holdability = dbManager.holdability;
		this.debug = dbManager.debug;
		this.fetchSize = dbManager.fetchSize;
		this.maxRows = dbManager.maxRows;
	}

	// ---------------------------------------------------------------- query states

	public static final int QUERY_CREATED = 1;
	public static final int QUERY_INITIALIZED = 2;
	public static final int QUERY_CLOSED = 3;

	protected int queryState = QUERY_CREATED;

	/**
	 * Returns query state.
	 */
	public int getQueryState() {
		return queryState;
	}

	/**
	 * Checks if query is not closed and throws an exception if it is.
	 */
	protected void checkNotClosed() {
		if (queryState == QUERY_CLOSED) {
			throw new DbSqlException("Query is closed. Operation may be performed only on active queries.");
		}
	}

	/**
	 * Checks if query is created (and not yet initialized or closed) and throws an exception if it is not.
	 */
	protected void checkCreated() {
		if (queryState != QUERY_CREATED) {
			String message = (queryState == QUERY_INITIALIZED ?
									"Query is already initialized." : "Query is closed.");
			throw new DbSqlException(message + " Operation may be performed only on created queries.");
		}
	}

	/**
	 * Checks if query is initialized and throws an exception if it is not.
	 */
	protected void checkInitialized() {
		if (queryState != QUERY_INITIALIZED) {
			String message = (queryState == QUERY_CREATED ?
									"Query is just created and not yet initialized." : "Query is closed.");
			throw new DbSqlException(message + " Operation may be performed only on initialized queries.");
		}
	}



	/**
	 * Returns <code>true</code> if query is closed.
	 */
	public boolean isClosed() {
		return queryState == QUERY_CLOSED;
	}

	/**
	 * Returns <code>true</code> if query is active: created and possibly initialized.
	 * Opened query may be not initialized.
	 */
	public boolean isActive() {
		return queryState < QUERY_CLOSED;
	}

	/**
	 * Returns <code>true</code> if query is initialized. Initialized query is the one that has
	 * created JDBC statements. 
	 */
	public boolean isInitialized() {
		return queryState == QUERY_INITIALIZED;
	}

	// ---------------------------------------------------------------- input

	protected Connection connection;
	protected DbSession session;
	protected String sqlString;

	// ---------------------------------------------------------------- attributes

	protected Statement statement;
	protected PreparedStatement preparedStatement;
	protected Set<ResultSet> resultSets;
	protected DbQueryParser query;

	/**
	 * Stores result set.
	 */
	protected void saveResultSet(ResultSet rs) {
		if (resultSets == null) {
			resultSets = new HashSet<ResultSet>();
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
	public void forcePreparedStatement() {
		checkCreated();
		forcePreparedStatement = true;
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
		if (queryState == QUERY_INITIALIZED) {
			return;
		}
		initializeJdbc();
		queryState = QUERY_INITIALIZED;
		prepareQuery();
	}

	/**
	 * Performs JDBC initialization of the query. Obtains connection, parses the SQL query string
	 * and creates statements. Initialization is performed only once, when switching to initialized state.
	 */
	@SuppressWarnings("MagicConstant")
	protected void initializeJdbc() {
		// connection
		if (connection == null) {
			if (session == null) {
				session = dbManager.sessionProvider.getDbSession();
			}
			connection = session.getConnection();
		}

		this.query = new DbQueryParser(sqlString);

		// statement
		if ((forcePreparedStatement == false) && (query.prepared == false)) {
			try {
				if (holdability != DEFAULT_HOLDABILITY) {
					statement = connection.createStatement(type, concurrencyType, holdability);
				} else {
					statement = connection.createStatement(type, concurrencyType);
				}
			} catch (SQLException sex) {
				throw new DbSqlException("Unable to create statement.", sex);
			}
			return;
		}

		// prepared statement
		try {
			if (debug == true) {
				if (generatedColumns != null) {
					if (generatedColumns.length == 0) {
						statement = LoggablePreparedStatementFactory.create(connection, query.sql, Statement.RETURN_GENERATED_KEYS);
					} else {
						statement = LoggablePreparedStatementFactory.create(connection, query.sql, generatedColumns);
					}
				} else {
					if (holdability != DEFAULT_HOLDABILITY) {
						statement = LoggablePreparedStatementFactory.create(connection, query.sql, type, concurrencyType, holdability);
					} else {
						statement = LoggablePreparedStatementFactory.create(connection, query.sql, type, concurrencyType);
					}
				}
			} else {
				if (generatedColumns != null) {
					if (generatedColumns.length == 0) {
						statement = connection.prepareStatement(query.sql, Statement.RETURN_GENERATED_KEYS);
					} else {
						statement = connection.prepareStatement(query.sql, generatedColumns);
					}
				} else {
					if (holdability != DEFAULT_HOLDABILITY) {
						statement = connection.prepareStatement(query.sql, type, concurrencyType, holdability);
					} else {
						statement = connection.prepareStatement(query.sql, type, concurrencyType);
					}
				}
			}
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to create prepared statement.", sex);
		}
		preparedStatement = (PreparedStatement) statement;
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

	/**
	 * Closes all result sets opened by this query. Query remains active.
	 */
	private List<SQLException> closeQueryResultSets() {
		List<SQLException> sexs = null;
		if (resultSets != null) {
			for (ResultSet rs : resultSets) {
				try {
					rs.close();
				} catch (SQLException sex) {
					if (sexs == null) {
						sexs = new ArrayList<SQLException>();
					}
					sexs.add(sex);
				} finally {
					totalOpenResultSetCount--;
				}
			}
			resultSets.clear();
			resultSets = null;
		}
		return sexs;
	}

	/**
	 * Closes all result sets created by this query. Query remains active.
	 */
	public void closeAllResultSets() {
		List<SQLException> sexs = closeQueryResultSets();
		if (sexs != null) {
			throw new DbSqlException("Unable to close associated ResultSets.", sexs);
		}
	}

	/**
	 * Closes all assigned result sets and then closes the query. Query becomes closed.
	 */
	protected List<SQLException> closeQuery() {
		List<SQLException> sexs = closeQueryResultSets();
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException sex) {
				if (sexs == null) {
					sexs = new ArrayList<SQLException>();
				}
				sexs.add(sex);
			}
			statement = null;
		}
		query = null;
		queryState = QUERY_CLOSED;
		return sexs;
	}

	/**
	 * Closes the query and all created results sets and detaches itself from the session.
	 */
	@SuppressWarnings({"ClassReferencesSubclass"})
	public void close() {
		List<SQLException> sexs = closeQuery();
		connection = null;
		if (this.session != null) {
			this.session.detachQuery(this);
		}
		if (sexs != null) {
			throw new DbSqlException("Unable to close query.", sexs);
		}
	}

	/**
	 * Closes single result set that was created by this query, It is not necessary to close result sets
	 * explicitly, since {@link DbQueryBase#close()} method closes all created result sets.
	 * Query remains active.
	 */
	public void closeResultSet(ResultSet rs) {
		if (rs == null) {
			return;
		}
		if (resultSets.remove(rs) == false) {
			throw new DbSqlException("Provided ResultSet is not created by this query and should be not closed in this way.");
		}
		try {
			rs.close();
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to close the result set.", sex);
		} finally {
			totalOpenResultSetCount--;
		}
	}

	// ---------------------------------------------------------------- result set type

	/**
	 * @see ResultSet#TYPE_FORWARD_ONLY
	 */
	public static final int TYPE_FORWARD_ONLY = ResultSet.TYPE_FORWARD_ONLY;
	/**
	 * @see ResultSet#TYPE_SCROLL_SENSITIVE
	 */
	public static final int TYPE_SCROLL_SENSITIVE = ResultSet.TYPE_SCROLL_SENSITIVE;
	/**
	 * @see ResultSet#TYPE_SCROLL_INSENSITIVE
	 */
	public static final int TYPE_SCROLL_INSENSITIVE = ResultSet.TYPE_SCROLL_INSENSITIVE;

	protected int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		checkCreated();
		this.type = type;
	}
	public void typeForwardOnly() {
		setType(TYPE_FORWARD_ONLY);
	}
	public void typeScrollSensitive() {
		setType(TYPE_SCROLL_SENSITIVE);
	}
	public void typeScrollInsensitive() {
		setType(TYPE_SCROLL_SENSITIVE);
	}

	// ---------------------------------------------------------------- concurrency

	/**
	 * @see ResultSet#CONCUR_READ_ONLY
	 */
	public static final int CONCUR_READ_ONLY = ResultSet.CONCUR_READ_ONLY;
	/**
	 * @see ResultSet#CONCUR_UPDATABLE
	 */
	public static final int CONCUR_UPDATABLE = ResultSet.CONCUR_UPDATABLE;

	protected int concurrencyType;

	public int getConcurrencyType() {
		return concurrencyType;
	}

	public void setConcurrencyType(int concurrencyType) {
		checkCreated();
		this.concurrencyType = concurrencyType;
	}
	public void concurReadOnly() {
		setConcurrencyType(CONCUR_READ_ONLY);
	}
	public void concurUpdatable() {
		setConcurrencyType(CONCUR_UPDATABLE);
	}

	// ---------------------------------------------------------------- holdability

	/**
	 * Default holdability. JDBC specification does not specifies default value for holdability.
	 */
	public static final int DEFAULT_HOLDABILITY = -1;

	/**
	 * @see ResultSet#CLOSE_CURSORS_AT_COMMIT
	 */
	public static final int CLOSE_CURSORS_AT_COMMIT = ResultSet.CLOSE_CURSORS_AT_COMMIT;

	/**
	 * @see ResultSet#HOLD_CURSORS_OVER_COMMIT
	 */
	public static final int HOLD_CURSORS_OVER_COMMIT = ResultSet.HOLD_CURSORS_OVER_COMMIT;

	protected int holdability;

	public int getHoldability() {
		return holdability;
	}

	public void setHoldability(int holdability) {
		checkCreated();
		this.holdability = holdability;
	}

	public void holdCursorsOverCommit() {
		setHoldability(HOLD_CURSORS_OVER_COMMIT);
	}

	public void closeCursorsAtCommit() {
		setHoldability(CLOSE_CURSORS_AT_COMMIT);
	}

	// ---------------------------------------------------------------- debug mode

	protected boolean debug;

	public boolean isInDebugMode() {
		return debug;
	}

	public void setDebug(boolean debug) {
		checkCreated();
		this.debug = debug;
	}
	public void setDebugMode() {
		setDebug(true);
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
	public void setGeneratedColumns(String... columns) {
		checkCreated();
		generatedColumns = columns;
	}

	/**
	 * Specifies that database will generate some columns values,
	 * usually the single id.
	 */
	public void setGeneratedKey() {
		setGeneratedColumns();
	}

	/**
	 * Resets creating generated columns.
	 */
	public void resetGeneratedColumns() {
		checkCreated();
		generatedColumns = null;
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
	public void setFetchSize(int rows) {
		checkNotClosed();
		this.fetchSize = rows;
		if (statement != null) {
			try {
				statement.setFetchSize(fetchSize);
			} catch (SQLException sex) {
				throw new DbSqlException("Unable to set fetch size of: " + fetchSize, sex);
			}
		}
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
	public void setMaxRows(int maxRows) {
		checkNotClosed();
		this.maxRows = maxRows;
		if (statement != null) {
			try {
				statement.setMaxRows(maxRows);
			} catch (SQLException sex) {
				throw new DbSqlException("Unable to set max rows of: " + maxRows, sex);
			}
		}
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
			ResultSetUtil.close(rs);
			throw new DbSqlException("Unable to execute query.", sex);
		}
		saveResultSet(rs);
		totalOpenResultSetCount++;

		elapsed = System.currentTimeMillis() - start;
		if (log.isDebugEnabled()) {
			log.debug("execution time: " + elapsed + "ms");
		}
		return rs;
	}

	/**
	 * Executes UPDATE, INSERT or DELETE queries. Query is not closed afterwards.
	 * @see Statement#executeUpdate(String)
	 */
	public int executeUpdate() {
		return executeUpdate(false);
	}

	/**
	 * Executes UPDATE, INSERT or DELETE queries and closes query afterwards.
	 * @see Statement#executeUpdate(String)
	 */
	public int executeUpdateAndClose() {
		return executeUpdate(true);
	}

	/**
	 * Executes UPDATE, INSERT or DELETE queries and optionally closes the query.
	 * @see Statement#executeUpdate(String)
	 */
	protected int executeUpdate(boolean closeQuery) {
		start = System.currentTimeMillis();

		init();
		int result;
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
			throw new DbSqlException("Unable to execute the query.", sex);
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
	 * Special execute() for 'select count(*)' queries. Query <b>is not</b> closed after the execution.
	 * It doesn't check if query is really a count query. If result set returns zero rows, (what is very unlikely),
	 * it returns <code>-1</code>.
	 */
	public long executeCount() {
		return executeCount(false);
	}

	/**
	 * Executes count queries and closes afterwards.
	 */
	public long executeCountAndClose() {
		return executeCount(true);
	}

	/**
	 * Executes count queries and optionally closes query afterwards.
	 */
	protected long executeCount(boolean close) {
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

			long firstLong = ResultSetUtil.getFirstLong(rs);

			elapsed = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug("execution time: " + elapsed + "ms");
			}

			return firstLong;
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to execute count query.", sex);
		} finally {
			ResultSetUtil.close(rs);
			if (close) {
				close();
			}
		}
	}

	// ---------------------------------------------------------------- generated keys

	/**
	 * Returns generated columns.
	 */
	public ResultSet getGeneratedColumns() {
		checkInitialized();
		if (generatedColumns == null) {
			throw new DbSqlException("No column is specified as auto-generated.");
		}
		ResultSet rs;
		try {
			rs = statement.getGeneratedKeys();
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to return generated keys.", sex);
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
		ResultSet rs = getGeneratedColumns();
		try {
			return ResultSetUtil.getFirstLong(rs);
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to get generated key as long value.", sex);
		} finally {
			ResultSetUtil.close(rs);
			resultSets.remove(rs);
			totalOpenResultSetCount--;
		}
	}


	// ---------------------------------------------------------------- query string

	/**
	 * Returns query SQL string. For prepared statements, returned sql string with quick-and-dirty replaced values.
	 */
	public String getQueryString() {
		if ((preparedStatement != null) && debug) {
			return LoggablePreparedStatementFactory.getQueryString(preparedStatement);
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
