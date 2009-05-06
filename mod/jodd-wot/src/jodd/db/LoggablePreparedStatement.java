// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.InputStream;

/**
 * A <code>LoggablePreparedStatement</code> is a {@link PreparedStatement} with added logging capability.
 * <p>
 * In addition to the methods declared in <code>PreparedStatement</code>,
 * <code>LoggablePreparedStatement</code> provides a method {@link #getQueryString} which can be used to get
 * the query string in a format suitable for logging.
 */
public class LoggablePreparedStatement implements PreparedStatement {

	/**
	 * Used for storing parameter values needed for producing log.
	 */
	private ArrayList<String> parameterValues;

	/**
	 * The query string with question marks as parameter placeholders.
	 */
	private String sqlTemplate;

	/**
	 * A statement created from a real database connection.
	 */
	private PreparedStatement wrappedStatement;

	/**
	* Constructs a LoggablePreparedStatement.
	*
	* Creates {@link java.sql.PreparedStatement PreparedStatement} with the query string <code>sql</code> using
	* the specified <code>conn</code> by calling {@link java.sql.Connection#prepareStatement(String)}.
	* <p>
	* Whenever a call is made to this <code>LoggablePreparedStatement</code> it is forwarded to the prepared statement created from
	* <code>conn</code> after first saving relevant parameters for use in logging output.
	*
	* @param connection java.sql.Connection a JDBC-conn to be used for obtaining a "real statement"
	* @param sql java.lang.String thw sql to execute
	* @exception SQLException if a <code>PreparedStatement</code> cannot be created using the supplied <code>conn</code> and <code>sql</code>
	*/
	public LoggablePreparedStatement(Connection connection, String sql) throws SQLException {
		wrappedStatement = connection.prepareStatement(sql);
		sqlTemplate = sql;
		parameterValues = new ArrayList<String>();
	}

	public LoggablePreparedStatement(Connection connection, String sql, int resultType, int resultSetConcurrency) throws SQLException {
		wrappedStatement = connection.prepareStatement(sql, resultType, resultSetConcurrency);
		sqlTemplate = sql;
		parameterValues = new ArrayList<String>();
	}

	public LoggablePreparedStatement(Connection connection, String sql, int resultType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		wrappedStatement = connection.prepareStatement(sql, resultType, resultSetConcurrency, resultSetHoldability);
		sqlTemplate = sql;
		parameterValues = new ArrayList<String>();
	}

	public LoggablePreparedStatement(Connection connection, String sql, int generateKeys) throws SQLException {
		wrappedStatement = connection.prepareStatement(sql, generateKeys);
		sqlTemplate = sql;
		parameterValues = new ArrayList<String>();
	}

	public LoggablePreparedStatement(Connection connection, String sql, String[] columnNames) throws SQLException {
		wrappedStatement = connection.prepareStatement(sql, columnNames);
		sqlTemplate = sql;
		parameterValues = new ArrayList<String>();
	}

	/**
	 * JDBC 2.0
	 *
	 * Adds a set of parameters to the batch.
	 *
	 * @exception SQLException if a database access error occurs
	 * @see java.sql.Statement#addBatch
	 */
	public void addBatch() throws SQLException {
		wrappedStatement.addBatch();
	}
	/**
	 * JDBC 2.0
	 *
	 * Adds a SQL command to the current batch of commands for the statement.
	 * This method is optional.
	 *
	 * @param sql typically this is a static SQL INSERT or UPDATE statement
	 * @exception SQLException if a database access error occurs, or the
	 * driver does not support batch statements
	 */
	public void addBatch(String sql) throws SQLException {
		wrappedStatement.addBatch(sql);
	}
	/**
	 * Cancels this <code>Statement</code> object if both the DBMS and
	 * driver support aborting an SQL statement.
	 * This method can be used by one thread to cancel a statement that
	 * is being executed by another thread.
	 *
	 * @exception SQLException if a database access error occurs
	 */
	public void cancel() throws SQLException {
		wrappedStatement.cancel();
	}
	/**
	 * JDBC 2.0
	 *
	 * Makes the set of commands in the current batch empty.
	 * This method is optional.
	 *
	 * @exception SQLException if a database access error occurs or the
	 * driver does not support batch statements
	 */
	public void clearBatch() throws SQLException {
		wrappedStatement.clearBatch();
	}
	/**
	 * Clears the current parameter values immediately.
	 * <p>
	 * In general, parameter values remain in force for repeated use of a
	 * Statement. Setting a parameter value automatically clears its
	 * previous value.  However, in some cases it is useful to immediately
	 * release the resources used by the current parameter values; this can
	 * be done by calling clearParameters.
	 *
	 * @exception SQLException if a database access error occurs
	 */
	public void clearParameters() throws SQLException {
		wrappedStatement.clearParameters();
	}
	/**
	 * Clears all the warnings reported on this <code>Statement</code>
	 * object. After a call to this method,
	 * the method <code>getWarnings</code> will return
	 * null until a new warning is reported for this Statement.
	 *
	 * @exception SQLException if a database access error occurs
	 */
	public void clearWarnings() throws SQLException {
		wrappedStatement.clearWarnings();
	}
	/**
	 * Releases this <code>Statement</code> object's database
	 * and JDBC resources immediately instead of waiting for
	 * this to happen when it is automatically closed.
	 * It is generally good practice to release resources as soon as
	 * you are finished with them to avoid tying up database
	 * resources.
	 * <p>
	 * <B>Note:</B> A Statement is automatically closed when it is
	 * garbage collected. When a Statement is closed, its current
	 * ResultSet, if one exists, is also closed.
	 *
	 * @exception SQLException if a database access error occurs
	 */
	public void close() throws SQLException {
		wrappedStatement.close();
	}
	/**
	 * Executes any kind of SQL statement.
	 * Some prepared statements return multiple results; the execute
	 * method handles these complex statements as well as the simpler
	 * form of statements handled by executeQuery and executeUpdate.
	 *
	 * @exception SQLException if a database access error occurs
	 * @see java.sql.Statement#execute
	 */
	public boolean execute() throws SQLException {
		return wrappedStatement.execute();
	}
	/**
	 * Executes a SQL statement that may return multiple results.
	 * Under some (uncommon) situations a single SQL statement may return
	 * multiple result sets and/or update counts.  Normally you can ignore
	 * this unless you are (1) executing a stored procedure that you know may
	 * return multiple results or (2) you are dynamically executing an
	 * unknown SQL string.  The  methods <code>execute</code>,
	 * <code>getMoreResults</code>, <code>getResultSet</code>,
	 * and <code>getUpdateCount</code> let you navigate through multiple results.
	 *
	 * The <code>execute</code> method executes a SQL statement and indicates the
	 * form of the first result.  You can then use getResultSet or
	 * getUpdateCount to retrieve the result, and getMoreResults to
	 * move to any subsequent result(s).
	 *
	 * @param sql any SQL statement
	 * @return true if the next result is a ResultSet; false if it is
	 * an update count or there are no more results
	 * @exception SQLException if a database access error occurs
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 */
	public boolean execute(String sql) throws SQLException {
		return wrappedStatement.execute(sql);
	}
	/**
	 * JDBC 2.0
	 *
	 * Submits a batch of commands to the database for execution.
	 * This method is optional.
	 *
	 * @return an array of update counts containing one element for each
	 * command in the batch.  The array is ordered according
	 * to the order in which commands were inserted into the batch.
	 * @exception SQLException if a database access error occurs or the
	 * driver does not support batch statements
	 */
	public int[] executeBatch() throws SQLException {
		return wrappedStatement.executeBatch();
	}
	/**
	 * Executes the SQL query in this <code>PreparedStatement</code> object
	 * and returns the result set generated by the query.
	 *
	 * @return a ResultSet that contains the data produced by the
	 * query; never null
	 * @exception SQLException if a database access error occurs
	 */
	public ResultSet executeQuery() throws SQLException {
		return wrappedStatement.executeQuery();
	}
	/**
	 * Executes a SQL statement that returns a single ResultSet.
	 *
	 * @param sql typically this is a static SQL SELECT statement
	 * @return a ResultSet that contains the data produced by the
	 * query; never null
	 * @exception SQLException if a database access error occurs
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		return wrappedStatement.executeQuery(sql);
	}
	/**
	 * Executes the SQL INSERT, UPDATE or DELETE statement
	 * in this <code>PreparedStatement</code> object.
	 * In addition,
	 * SQL statements that return nothing, such as SQL DDL statements,
	 * can be executed.
	 *
	 * @return either the row count for INSERT, UPDATE or DELETE statements;
	 * or 0 for SQL statements that return nothing
	 * @exception SQLException if a database access error occurs
	 */
	public int executeUpdate() throws SQLException {
		return wrappedStatement.executeUpdate();
	}
	/**
	 * Executes an SQL INSERT, UPDATE or DELETE statement. In addition,
	 * SQL statements that return nothing, such as SQL DDL statements,
	 * can be executed.
	 *
	 * @param sql a SQL INSERT, UPDATE or DELETE statement or a SQL
	 * statement that returns nothing
	 * @return either the row count for INSERT, UPDATE or DELETE or 0
	 * for SQL statements that return nothing
	 * @exception SQLException if a database access error occurs
	 */
	public int executeUpdate(String sql) throws SQLException {
		return wrappedStatement.executeUpdate(sql);
	}
	/**
	 * JDBC 2.0
	 *
	 * Returns the <code>Connection</code> object
	 * that produced this <code>Statement</code> object.
	 * @return the conn that produced this statement
	 * @exception SQLException if a database access error occurs
	 */
	public java.sql.Connection getConnection() throws SQLException {
		return wrappedStatement.getConnection();
	}
	/**
	 * JDBC 2.0
	 *
	 * Retrieves the direction for fetching rows from
	 * database tables that is the default for result sets
	 * generated from this <code>Statement</code> object.
	 * If this <code>Statement</code> object has not set
	 * a fetch direction by calling the method <code>setFetchDirection</code>,
	 * the return value is implementation-specific.
	 *
	 * @return the default fetch direction for result sets generated
	 *          from this <code>Statement</code> object
	 * @exception SQLException if a database access error occurs
	 */
	public int getFetchDirection() throws SQLException {
		return wrappedStatement.getFetchDirection();
	}
	/**
	 * JDBC 2.0
	 *
	 * Retrieves the number of result set rows that is the default
	 * fetch size for result sets
	 * generated from this <code>Statement</code> object.
	 * If this <code>Statement</code> object has not set
	 * a fetch size by calling the method <code>setFetchSize</code>,
	 * the return value is implementation-specific.
	 * @return the default fetch size for result sets generated
	 *          from this <code>Statement</code> object
	 * @exception SQLException if a database access error occurs
	 */
	public int getFetchSize() throws SQLException {
		return wrappedStatement.getFetchSize();
	}
	/**
	 * Returns the maximum number of bytes allowed
	 * for any column value.
	 * This limit is the maximum number of bytes that can be
	 * returned for any column value.
	 * The limit applies only to BINARY,
	 * VARBINARY, LONGVARBINARY, CHAR, VARCHAR, and LONGVARCHAR
	 * columns.  If the limit is exceeded, the excess data is silently
	 * discarded.
	 *
	 * @return the current max column size limit; zero means unlimited
	 * @exception SQLException if a database access error occurs
	 */
	public int getMaxFieldSize() throws SQLException {
		return wrappedStatement.getMaxFieldSize();
	}
	/**
	 * Retrieves the maximum number of rows that a
	 * ResultSet can contain.  If the limit is exceeded, the excess
	 * rows are silently dropped.
	 *
	 * @return the current max row limit; zero means unlimited
	 * @exception SQLException if a database access error occurs
	 */
	public int getMaxRows() throws SQLException {
		return wrappedStatement.getMaxRows();
	}
	/**
	 * JDBC 2.0
	 *
	 * Gets the number, types and properties of a ResultSet's columns.
	 *
	 * @return the description of a ResultSet's columns
	 * @exception SQLException if a database access error occurs
	 */
	public java.sql.ResultSetMetaData getMetaData() throws SQLException {
		return wrappedStatement.getMetaData();
	}
	/**
	 * Moves to a Statement's next result.  It returns true if
	 * this result is a ResultSet.  This method also implicitly
	 * closes any current ResultSet obtained with getResultSet.
	 *
	 * There are no more results when (!getMoreResults() &&
	 * (getUpdateCount() == -1)
	 *
	 * @return true if the next result is a ResultSet; false if it is
	 * an update count or there are no more results
	 * @exception SQLException if a database access error occurs
	 * @see #execute
	 */
	public boolean getMoreResults() throws SQLException {
		return wrappedStatement.getMoreResults();
	}
	/**
	 * Retrieves the number of seconds the driver will
	 * wait for a Statement to execute. If the limit is exceeded, a
	 * SQLException is thrown.
	 *
	 * @return the current query timeout limit in seconds; zero means unlimited
	 * @exception SQLException if a database access error occurs
	 */
	public int getQueryTimeout() throws SQLException {
		return wrappedStatement.getQueryTimeout();
	}
	/**
	 *  Returns the current result as a <code>ResultSet</code> object.
	 *  This method should be called only once per result.
	 *
	 * @return the current result as a ResultSet; null if the result
	 * is an update count or there are no more results
	 * @exception SQLException if a database access error occurs
	 * @see #execute
	 */
	public ResultSet getResultSet() throws SQLException {
		return wrappedStatement.getResultSet();
	}
	/**
	 * JDBC 2.0
	 *
	 * Retrieves the result set concurrency.
	 */
	public int getResultSetConcurrency() throws SQLException {
		return wrappedStatement.getResultSetConcurrency();
	}
	/**
	 * JDBC 2.0
	 *
	 * Determine the result set type.
	 */
	public int getResultSetType() throws SQLException {
		return wrappedStatement.getResultSetType();
	}
	/**
	 *  Returns the current result as an update count;
	 *  if the result is a ResultSet or there are no more results, -1
	 *  is returned.
	 *  This method should be called only once per result.
	 *
	 * @return the current result as an update count; -1 if it is a
	 * ResultSet or there are no more results
	 * @exception SQLException if a database access error occurs
	 * @see #execute
	 */
	public int getUpdateCount() throws SQLException {
		return wrappedStatement.getUpdateCount();
	}
	/**
	 * Retrieves the first warning reported by calls on this Statement.
	 * Subsequent Statement warnings will be chained to this
	 * SQLWarning.
	 *
	 * <p>The warning chain is automatically cleared each time
	 * a statement is (re)executed.
	 *
	 * <P><B>Note:</B> If you are processing a ResultSet, any
	 * warnings associated with ResultSet reads will be chained on the
	 * ResultSet object.
	 *
	 * @return the first SQLWarning or null
	 * @exception SQLException if a database access error occurs
	 */
	public java.sql.SQLWarning getWarnings() throws SQLException {
		return wrappedStatement.getWarnings();
	}
	/**
	 * JDBC 2.0
	 *
	 * Sets an Array parameter.
	 *
	 * @param i the first parameter is 1, the second is 2, ...
	 * @param x an object representing an SQL array
	 * @exception SQLException if a database access error occurs
	 */
	public void setArray(int i, java.sql.Array x) throws SQLException {
		wrappedStatement.setArray(i, x);
		saveQueryParamValue(i, x);

	}
	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes.
	 * When a very large ASCII value is input to a LONGVARCHAR
	 * parameter, it may be more practical to send it via a
	 * InputStream. JDBC will read the data from the stream
	 * as needed, until it reaches end-of-file.  The JDBC driver will
	 * do any necessary conversion from ASCII to the database char format.
	 *
	 * <P><B>Note:</B> This stream object can either be a standard
	 * Java stream object or your own subclass that implements the
	 * standard interface.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the Java input stream that contains the ASCII parameter value
	 * @param length the number of bytes in the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		wrappedStatement.setAsciiStream(parameterIndex, x, length);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * Sets the designated parameter to a java.lang.BigDecimal value.
	 * The driver converts this to an SQL NUMERIC value when
	 * it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) throws SQLException {
		wrappedStatement.setBigDecimal(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);

	}

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes.
	 * When a very large binary value is input to a LONGVARBINARY
	 * parameter, it may be more practical to send it via a
	 * InputStream. JDBC will read the data from the stream
	 * as needed, until it reaches end-of-file.
	 *
	 * <P><B>Note:</B> This stream object can either be a standard
	 * Java stream object or your own subclass that implements the
	 * standard interface.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the java input stream which contains the binary parameter value
	 * @param length the number of bytes in the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		wrappedStatement.setBinaryStream(parameterIndex, x, length);
		saveQueryParamValue(parameterIndex, x);

	}
	/**
	 * JDBC 2.0
	 *
	 * Sets a BLOB parameter.
	 *
	 * @param i the first parameter is 1, the second is 2, ...
	 * @param x an object representing a BLOB
	 * @exception SQLException if a database access error occurs
	 */
	public void setBlob(int i, java.sql.Blob x) throws SQLException {
		wrappedStatement.setBlob(i, x);
		saveQueryParamValue(i, x);
	}
	/**
	 * Sets the designated parameter to a Java boolean value.  The driver converts this
	 * to an SQL BIT value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		wrappedStatement.setBoolean(parameterIndex, x);
		saveQueryParamValue(parameterIndex, Boolean.valueOf(x));

	}
	/**
	 * Sets the designated parameter to a Java byte value.  The driver converts this
	 * to an SQL TINYINT value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		wrappedStatement.setByte(parameterIndex, x);
		saveQueryParamValue(parameterIndex, new Integer(x));
	}
	/**
	 * Sets the designated parameter to a Java array of bytes.  The driver converts
	 * this to an SQL VARBINARY or LONGVARBINARY (depending on the
	 * argument's size relative to the driver's limits on VARBINARYs)
	 * when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		wrappedStatement.setBytes(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * JDBC 2.0
	 *
	 * Sets the designated parameter to the given <code>Reader</code>
	 * object, which is the given number of characters long.
	 * When a very large UNICODE value is input to a LONGVARCHAR
	 * parameter, it may be more practical to send it via a
	 * java.io.Reader. JDBC will read the data from the stream
	 * as needed, until it reaches end-of-file.  The JDBC driver will
	 * do any necessary conversion from UNICODE to the database char format.
	 *
	 * <P><B>Note:</B> This stream object can either be a standard
	 * Java stream object or your own subclass that implements the
	 * standard interface.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param reader the java reader which contains the UNICODE data
	 * @param length the number of characters in the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) throws SQLException {
		wrappedStatement.setCharacterStream(parameterIndex, reader, length);
		saveQueryParamValue(parameterIndex, reader);

	}
	/**
	 * JDBC 2.0
	 *
	 * Sets a CLOB parameter.
	 *
	 * @param i the first parameter is 1, the second is 2, ...
	 * @param x an object representing a CLOB
	 * @exception SQLException if a database access error occurs
	 */
	public void setClob(int i, java.sql.Clob x) throws SQLException {
		wrappedStatement.setClob(i, x);
		saveQueryParamValue(i, x);

	}
	/**
	 * Defines the SQL cursor name that will be used by
	 * subsequent Statement <code>execute</code> methods. This name can then be
	 * used in SQL positioned update/delete statements to identify the
	 * current row in the ResultSet generated by this statement.  If
	 * the database doesn't support positioned update/delete, this
	 * method is a noop.  To insure that a cursor has the proper isolation
	 * level to support updates, the cursor's SELECT statement should be
	 * of the form 'select for update ...'. If the 'for update' phrase is
	 * omitted, positioned updates may fail.
	 *
	 * <P><B>Note:</B> By definition, positioned update/delete
	 * execution must be done by a different Statement than the one
	 * which generated the ResultSet being used for positioning. Also,
	 * cursor names must be unique within a conn.
	 *
	 * @param name the new cursor name, which must be unique within
	 *             a conn
	 * @exception SQLException if a database access error occurs
	 */
	public void setCursorName(String name) throws SQLException {
		wrappedStatement.setCursorName(name);

	}
	/**
	 * Sets the designated parameter to a java.sql.Date value.  The driver converts this
	 * to an SQL DATE value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
		wrappedStatement.setDate(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}

	/**
	 * JDBC 2.0
	 *
	 * Sets the designated parameter to a java.sql.Date value,
	 * using the given <code>Calendar</code> object.  The driver uses
	 * the <code>Calendar</code> object to construct an SQL DATE,
	 * which the driver then sends to the database.  With a
	 * a <code>Calendar</code> object, the driver can calculate the date
	 * taking into account a custom timezone and locale.  If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the date
	 * @exception SQLException if a database access error occurs
	 */
	public void setDate(int parameterIndex, java.sql.Date x, java.util.Calendar cal) throws SQLException {
		wrappedStatement.setDate(parameterIndex, x, cal);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * Sets the designated parameter to a Java double value.  The driver converts this
	 * to an SQL DOUBLE value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		wrappedStatement.setDouble(parameterIndex, x);
		saveQueryParamValue(parameterIndex, new Double(x));
	}
	/**
	 * Sets escape processing on or off.
	 * If escape scanning is on (the default), the driver will do
	 * escape substitution before sending the SQL to the database.
	 *
	 * Note: Since prepared statements have usually been parsed prior
	 * to making this call, disabling escape processing for prepared
	 * statements will have no effect.
	 *
	 * @param enable true to enable; false to disable
	 * @exception SQLException if a database access error occurs
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		wrappedStatement.setEscapeProcessing(enable);

	}
	/**
	 * JDBC 2.0
	 *
	 * Gives the driver a hint as to the direction in which
	 * the rows in a result set
	 * will be processed. The hint applies only to result sets created
	 * using this Statement object.  The default value is
	 * ResultSet.FETCH_FORWARD.
	 * <p>Note that this method sets the default fetch direction for
	 * result sets generated by this <code>Statement</code> object.
	 * Each result set has its own methods for getting and setting
	 * its own fetch direction.
	 * @param direction the initial direction for processing rows
	 * @exception SQLException if a database access error occurs
	 * or the given direction
	 * is not one of ResultSet.FETCH_FORWARD, ResultSet.FETCH_REVERSE, or
	 * ResultSet.FETCH_UNKNOWN
	 */
	public void setFetchDirection(int direction) throws SQLException {
		wrappedStatement.setFetchDirection(direction);
	}
	/**
	 * JDBC 2.0
	 *
	 * Gives the JDBC driver a hint as to the number of rows that should
	 * be fetched from the database when more rows are needed.  The number
	 * of rows specified affects only result sets created using this
	 * statement. If the value specified is zero, then the hint is ignored.
	 * The default value is zero.
	 *
	 * @param rows the number of rows to fetch
	 * @exception SQLException if a database access error occurs, or the
	 * condition 0 <= rows <= this.getMaxRows() is not satisfied.
	 */
	public void setFetchSize(int rows) throws SQLException {
		wrappedStatement.setFetchSize(rows);
	}
	/**
	 * Sets the designated parameter to a Java float value.  The driver converts this
	 * to an SQL FLOAT value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		wrappedStatement.setFloat(parameterIndex, x);
		saveQueryParamValue(parameterIndex, new Float(x));

	}
	/**
	 * Sets the designated parameter to a Java int value.  The driver converts this
	 * to an SQL INTEGER value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		wrappedStatement.setInt(parameterIndex, x);
		saveQueryParamValue(parameterIndex, new Integer(x));
	}
	/**
	 * Sets the designated parameter to a Java long value.  The driver converts this
	 * to an SQL BIGINT value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		wrappedStatement.setLong(parameterIndex, x);
		saveQueryParamValue(parameterIndex, new Long(x));

	}
	/**
	 * Sets the limit for the maximum number of bytes in a column to
	 * the given number of bytes.  This is the maximum number of bytes
	 * that can be returned for any column value.  This limit applies
	 * only to BINARY, VARBINARY, LONGVARBINARY, CHAR, VARCHAR, and
	 * LONGVARCHAR fields.  If the limit is exceeded, the excess data
	 * is silently discarded. For maximum portability, use values
	 * greater than 256.
	 *
	 * @param max the new max column size limit; zero means unlimited
	 * @exception SQLException if a database access error occurs
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		wrappedStatement.setMaxFieldSize(max);

	}
	/**
	 * Sets the limit for the maximum number of rows that any
	 * ResultSet can contain to the given number.
	 * If the limit is exceeded, the excess
	 * rows are silently dropped.
	 *
	 * @param max the new max rows limit; zero means unlimited
	 * @exception SQLException if a database access error occurs
	 */
	public void setMaxRows(int max) throws SQLException {
		wrappedStatement.setMaxRows(max);
	}
	/**
	 * Sets the designated parameter to SQL NULL.
	 *
	 * <P><B>Note:</B> You must specify the parameter's SQL type.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param sqlType the SQL type code defined in java.sql.Types
	 * @exception SQLException if a database access error occurs
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		wrappedStatement.setNull(parameterIndex, sqlType);
		saveQueryParamValue(parameterIndex, null);
	}
	/**
	 * JDBC 2.0
	 *
	 * Sets the designated parameter to SQL NULL.  This version of setNull should
	 * be used for user-named types and REF type parameters.  Examples
	 * of user-named types include: STRUCT, DISTINCT, JAVA_OBJECT, and
	 * named array types.
	 *
	 * <P><B>Note:</B> To be portable, applications must give the
	 * SQL type code and the fully-qualified SQL type name when specifying
	 * a NULL user-defined or REF parameter.  In the case of a user-named type
	 * the name is the type name of the parameter itself.  For a REF
	 * parameter the name is the type name of the referenced type.  If
	 * a JDBC driver does not need the type code or type name information,
	 * it may ignore it.
	 *
	 * Although it is intended for user-named and Ref parameters,
	 * this method may be used to set a null parameter of any JDBC type.
	 * If the parameter does not have a user-named or REF type, the given
	 * typeName is ignored.
	 *
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param sqlType a value from java.sql.Types
	 * @param typeName the fully-qualified name of an SQL user-named type,
	 *  ignored if the parameter is not a user-named type or REF
	 * @exception SQLException if a database access error occurs
	 */
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		wrappedStatement.setNull(parameterIndex, sqlType, typeName);
		saveQueryParamValue(parameterIndex, null);

	}
	/**
	 * <p>Sets the value of a parameter using an object; use the
	 * java.lang equivalent objects for integral values.
	 *
	 * <p>The JDBC specification specifies a standard mapping from
	 * Java Object types to SQL types.  The given argument java object
	 * will be converted to the corresponding SQL type before being
	 * sent to the database.
	 *
	 * <p>Note that this method may be used to pass datatabase-
	 * specific abstract data types, by using a Driver-specific Java
	 * type.
	 *
	 * If the object is of a class implementing SQLData,
	 * the JDBC driver should call its method <code>writeSQL</code> to write it
	 * to the SQL data stream.
	 * If, on the other hand, the object is of a class implementing
	 * Ref, Blob, Clob, Struct,
	 * or Array, then the driver should pass it to the database as a value of the
	 * corresponding SQL type.
	 *
	 * This method throws an exception if there is an ambiguity, for example, if the
	 * object is of a class implementing more than one of those interfaces.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the object containing the input parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		wrappedStatement.setObject(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
		 * Sets the value of the designated parameter with the given object.
		 * This method is like setObject above, except that it assumes a scale of zero.
		 *
		 * @param parameterIndex the first parameter is 1, the second is 2, ...
		 * @param x the object containing the input parameter value
		 * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
		 *                      sent to the database
		 * @exception SQLException if a database access error occurs
		 */
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		wrappedStatement.setObject(parameterIndex, x, targetSqlType);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * <p>Sets the value of a parameter using an object. The second
	 * argument must be an object type; for integral values, the
	 * java.lang equivalent objects should be used.
	 *
	 * <p>The given Java object will be converted to the targetSqlType
	 * before being sent to the database.
	 *
	 * If the object has a custom mapping (is of a class implementing SQLData),
	 * the JDBC driver should call its method <code>writeSQL</code> to write it
	 * to the SQL data stream.
	 * If, on the other hand, the object is of a class implementing
	 * Ref, Blob, Clob, Struct,
	 * or Array, the driver should pass it to the database as a value of the
	 * corresponding SQL type.
	 *
	 * <p>Note that this method may be used to pass database-specific abstract data types.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the object containing the input parameter value
	 * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
	 * sent to the database. The scale argument may further qualify this type.
	 * @param scale for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
	 *          this is the number of digits after the decimal point.  For all other
	 *          types, this value will be ignored.
	 * @exception SQLException if a database access error occurs
	 * @see java.sql.Types
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		wrappedStatement.setObject(parameterIndex, x, targetSqlType, scale);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * Sets the number of seconds the driver will
	 * wait for a Statement to execute to the given number of seconds.
	 * If the limit is exceeded, a SQLException is thrown.
	 *
	 * @param seconds the new query timeout limit in seconds; zero means
	 * unlimited
	 * @exception SQLException if a database access error occurs
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		wrappedStatement.setQueryTimeout(seconds);
	}
	/**
	 * JDBC 2.0
	 *
	 * Sets a REF(&lt;structured-type&gt;) parameter.
	 *
	 * @param i the first parameter is 1, the second is 2, ...
	 * @param x an object representing data of an SQL REF Type
	 * @exception SQLException if a database access error occurs
	 */
	public void setRef(int i, java.sql.Ref x) throws SQLException {
		wrappedStatement.setRef(i, x);
		saveQueryParamValue(i, x);

	}
	/**
	 * Sets the designated parameter to a Java short value.  The driver converts this
	 * to an SQL SMALLINT value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		wrappedStatement.setShort(parameterIndex, x);
		saveQueryParamValue(parameterIndex, new Integer(x));
	}
	/**
	 * Sets the designated parameter to a Java String value.  The driver converts this
	 * to an SQL VARCHAR or LONGVARCHAR value (depending on the argument's
	 * size relative to the driver's limits on VARCHARs) when it sends
	 * it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setString(int parameterIndex, String x) throws SQLException {

		wrappedStatement.setString(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * Sets the designated parameter to a java.sql.Time value.  The driver converts this
	 * to an SQL TIME value when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setTime(int parameterIndex, java.sql.Time x) throws SQLException {
		wrappedStatement.setTime(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * JDBC 2.0
	 *
	 * Sets the designated parameter to a java.sql.Time value,
	 * using the given <code>Calendar</code> object.  The driver uses
	 * the <code>Calendar</code> object to construct an SQL TIME,
	 * which the driver then sends to the database.  With a
	 * a <code>Calendar</code> object, the driver can calculate the time
	 * taking into account a custom timezone and locale.  If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the time
	 * @exception SQLException if a database access error occurs
	 */
	public void setTime(int parameterIndex, java.sql.Time x, java.util.Calendar cal) throws SQLException {
		wrappedStatement.setTime(parameterIndex, x, cal);
		saveQueryParamValue(parameterIndex, x);

	}
	/**
	 * Sets the designated parameter to a java.sql.Timestamp value.  The driver
	 * converts this to an SQL TIMESTAMP value when it sends it to the
	 * database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
		wrappedStatement.setTimestamp(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * JDBC 2.0
	 *
	 * Sets the designated parameter to a java.sql.Timestamp value,
	 * using the given <code>Calendar</code> object.  The driver uses
	 * the <code>Calendar</code> object to construct an SQL TIMESTAMP,
	 * which the driver then sends to the database.  With a
	 * a <code>Calendar</code> object, the driver can calculate the timestamp
	 * taking into account a custom timezone and locale.  If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the timestamp
	 * @exception SQLException if a database access error occurs
	 */
	public void setTimestamp(int parameterIndex, java.sql.Timestamp x, java.util.Calendar cal) throws SQLException {
		wrappedStatement.setTimestamp(parameterIndex, x, cal);
		saveQueryParamValue(parameterIndex, x);
	}
	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes.
	 * When a very large UNICODE value is input to a LONGVARCHAR
	 * parameter, it may be more practical to send it via a
	 * InputStream. JDBC will read the data from the stream
	 * as needed, until it reaches end-of-file.  The JDBC driver will
	 * do any necessary conversion from UNICODE to the database char format.
	 * The byte format of the Unicode stream must be Java UTF-8, as
	 * defined in the Java Virtual Machine Specification.
	 *
	 * <P><B>Note:</B> This stream object can either be a standard
	 * Java stream object or your own subclass that implements the
	 * standard interface.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the java input stream which contains the
	 * UNICODE parameter value
	 * @param length the number of bytes in the stream
	 * @exception SQLException if a database access error occurs
	 * @deprecated
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		//noinspection deprecation
		wrappedStatement.setUnicodeStream(parameterIndex, x, length);
		saveQueryParamValue(parameterIndex, x);
	}

	// ---------------------------------------------------------------- JDBC new

	/**
	 * Sets the designated parameter to the given <code>java.net.URL</code> value.
	 * The driver converts this to an SQL <code>DATALINK</code> value
	 * when it sends it to the database.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x			  the <code>java.net.URL</code> object to be set
	 * @throws java.sql.SQLException if a database access error occurs
	 * @since 1.4
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		wrappedStatement.setURL(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}

	/**
	 * Retrieves the number, types and properties of this
	 * <code>PreparedStatement</code> object's parameters.
	 *
	 * @return a <code>ParameterMetaData</code> object that contains information
	 *         about the number, types and properties of this
	 *         <code>PreparedStatement</code> object's parameters
	 * @throws java.sql.SQLException if a database access error occurs
	 * @see java.sql.ParameterMetaData
	 * @since 1.4
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return wrappedStatement.getParameterMetaData();
	}

	/**
	 * Retrieves the result set holdability for <code>ResultSet</code> objects
	 * generated by this <code>Statement</code> object.
	 *
	 * @return either <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @throws java.sql.SQLException if a database access error occurs
	 * @since 1.4
	 */
	public int getResultSetHoldability() throws SQLException {
		return wrappedStatement.getResultSetHoldability();
	}

	/**
	 * Moves to this <code>Statement</code> object's next result, deals with
	 * any current <code>ResultSet</code> object(s) according  to the instructions
	 * specified by the given flag, and returns
	 * <code>true</code> if the next result is a <code>ResultSet</code> object.
	 * <p>
	 * There are no more results when the following is true:
	 * <PRE>
	 * // stmt is a Statement object
	 * ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1))
	 * </PRE>
	 *
	 * @param current one of the following <code>Statement</code>
	 *                constants indicating what should happen to current
	 *                <code>ResultSet</code> objects obtained using the method
	 *                <code>getResultSet</code>:
	 *                <code>Statement.CLOSE_CURRENT_RESULT</code>,
	 *                <code>Statement.KEEP_CURRENT_RESULT</code>, or
	 *                <code>Statement.CLOSE_ALL_RESULTS</code>
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are no
	 *         more results
	 * @throws java.sql.SQLException if a database access error occurs or the argument
	 *                               supplied is not one of the following:
	 *                               <code>Statement.CLOSE_CURRENT_RESULT</code>,
	 *                               <code>Statement.KEEP_CURRENT_RESULT</code>, or
	 *                               <code>Statement.CLOSE_ALL_RESULTS</code>
	 * @see #execute
	 * @since 1.4
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return wrappedStatement.getMoreResults(current);
	}

	/**
	 * Retrieves any auto-generated keys created as a result of executing this
	 * <code>Statement</code> object. If this <code>Statement</code> object did
	 * not generate any keys, an empty <code>ResultSet</code>
	 * object is returned.
	 *
	 * @return a <code>ResultSet</code> object containing the auto-generated key(s)
	 *         generated by the execution of this <code>Statement</code> object
	 * @throws java.sql.SQLException if a database access error occurs
	 * @since 1.4
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return wrappedStatement.getGeneratedKeys();
	}

	/**
	 * Executes the given SQL statement and signals the driver with the
	 * given flag about whether the
	 * auto-generated keys produced by this <code>Statement</code> object
	 * should be made available for retrieval.
	 *
	 * @param sql			   must be an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *                          <code>DELETE</code> statement or an SQL statement that
	 *                          returns nothing
	 * @param autoGeneratedKeys a flag indicating whether auto-generated keys
	 *                          should be made available for retrieval;
	 *                          one of the following constants:
	 *                          <code>Statement.RETURN_GENERATED_KEYS</code>
	 *                          <code>Statement.NO_GENERATED_KEYS</code>
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         or <code>DELETE</code> statements, or <code>0</code> for SQL
	 *         statements that return nothing
	 * @throws java.sql.SQLException if a database access error occurs, the given
	 *                               SQL statement returns a <code>ResultSet</code> object, or
	 *                               the given constant is not one of those allowed
	 * @since 1.4
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return wrappedStatement.executeUpdate(sql, autoGeneratedKeys);
	}

	/**
	 * Executes the given SQL statement, which may return multiple results,
	 * and signals the driver that any
	 * auto-generated keys should be made available
	 * for retrieval.  The driver will ignore this signal if the SQL statement
	 * is not an <code>INSERT</code> statement.
	 * <p>
	 * In some (uncommon) situations, a single SQL statement may return
	 * multiple result sets and/or update counts.  Normally you can ignore
	 * this unless you are (1) executing a stored procedure that you know may
	 * return multiple results or (2) you are dynamically executing an
	 * unknown SQL string.
	 * <p>
	 * The <code>execute</code> method executes an SQL statement and indicates the
	 * form of the first result.  You must then use the methods
	 * <code>getResultSet</code> or <code>getUpdateCount</code>
	 * to retrieve the result, and <code>getMoreResults</code> to
	 * move to any subsequent result(s).
	 *
	 * @param sql			   any SQL statement
	 * @param autoGeneratedKeys a constant indicating whether auto-generated
	 *                          keys should be made available for retrieval using the method
	 *                          <code>getGeneratedKeys</code>; one of the following constants:
	 *                          <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *                          <code>Statement.NO_GENERATED_KEYS</code>
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no results
	 * @throws java.sql.SQLException if a database access error occurs or the second
	 *                               parameter supplied to this method is not
	 *                               <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *                               <code>Statement.NO_GENERATED_KEYS</code>.
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 * @see #getGeneratedKeys
	 * @since 1.4
	 */
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return wrappedStatement.execute(sql, autoGeneratedKeys);
	}

	/**
	 * Executes the given SQL statement and signals the driver that the
	 * auto-generated keys indicated in the given array should be made available
	 * for retrieval.  The driver will ignore the array if the SQL statement
	 * is not an <code>INSERT</code> statement.
	 *
	 * @param sql		   an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *                      <code>DELETE</code> statement or an SQL statement that returns nothing,
	 *                      such as an SQL DDL statement
	 * @param columnIndexes an array of column indexes indicating the columns
	 *                      that should be returned from the inserted row
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
	 *         or <code>DELETE</code> statements, or 0 for SQL statements
	 *         that return nothing
	 * @throws java.sql.SQLException if a database access error occurs, the SQL
	 *                               statement returns a <code>ResultSet</code> object, or the
	 *                               second argument supplied to this method is not an <code>int</code> array
	 *                               whose elements are valid column indexes
	 * @since 1.4
	 */
	public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
		return wrappedStatement.executeUpdate(sql, columnIndexes);
	}

	/**
	 * Executes the given SQL statement, which may return multiple results,
	 * and signals the driver that the
	 * auto-generated keys indicated in the given array should be made available
	 * for retrieval.  This array contains the indexes of the columns in the
	 * target table that contain the auto-generated keys that should be made
	 * available. The driver will ignore the array if the given SQL statement
	 * is not an <code>INSERT</code> statement.
	 * <p>
	 * Under some (uncommon) situations, a single SQL statement may return
	 * multiple result sets and/or update counts.  Normally you can ignore
	 * this unless you are (1) executing a stored procedure that you know may
	 * return multiple results or (2) you are dynamically executing an
	 * unknown SQL string.
	 * <p>
	 * The <code>execute</code> method executes an SQL statement and indicates the
	 * form of the first result.  You must then use the methods
	 * <code>getResultSet</code> or <code>getUpdateCount</code>
	 * to retrieve the result, and <code>getMoreResults</code> to
	 * move to any subsequent result(s).
	 *
	 * @param sql		   any SQL statement
	 * @param columnIndexes an array of the indexes of the columns in the
	 *                      inserted row that should be  made available for retrieval by a
	 *                      call to the method <code>getGeneratedKeys</code>
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there
	 *         are no results
	 * @throws java.sql.SQLException if a database access error occurs or the
	 *                               elements in the <code>int</code> array passed to this method
	 *                               are not valid column indexes
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 * @since 1.4
	 */
	public boolean execute(String sql, int columnIndexes[]) throws SQLException {
		return wrappedStatement.execute(sql, columnIndexes);
	}

	/**
	 * Executes the given SQL statement and signals the driver that the
	 * auto-generated keys indicated in the given array should be made available
	 * for retrieval.  The driver will ignore the array if the SQL statement
	 * is not an <code>INSERT</code> statement.
	 *
	 * @param sql		 an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *                    <code>DELETE</code> statement or an SQL statement that returns nothing
	 * @param columnNames an array of the names of the columns that should be
	 *                    returned from the inserted row
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
	 *         or <code>DELETE</code> statements, or 0 for SQL statements
	 *         that return nothing
	 * @throws java.sql.SQLException if a database access error occurs, the SQL
	 *                               statement returns a <code>ResultSet</code> object, or the
	 *                               second argument supplied to this method is not a <code>String</code> array
	 *                               whose elements are valid column names
	 * @since 1.4
	 */
	public int executeUpdate(String sql, String columnNames[]) throws SQLException {
		return wrappedStatement.executeUpdate(sql, columnNames);
	}

	/**
	 * Executes the given SQL statement, which may return multiple results,
	 * and signals the driver that the
	 * auto-generated keys indicated in the given array should be made available
	 * for retrieval. This array contains the names of the columns in the
	 * target table that contain the auto-generated keys that should be made
	 * available. The driver will ignore the array if the given SQL statement
	 * is not an <code>INSERT</code> statement.
	 * <p>
	 * In some (uncommon) situations, a single SQL statement may return
	 * multiple result sets and/or update counts.  Normally you can ignore
	 * this unless you are (1) executing a stored procedure that you know may
	 * return multiple results or (2) you are dynamically executing an
	 * unknown SQL string.
	 * <p>
	 * The <code>execute</code> method executes an SQL statement and indicates the
	 * form of the first result.  You must then use the methods
	 * <code>getResultSet</code> or <code>getUpdateCount</code>
	 * to retrieve the result, and <code>getMoreResults</code> to
	 * move to any subsequent result(s).
	 *
	 * @param sql		 any SQL statement
	 * @param columnNames an array of the names of the columns in the inserted
	 *                    row that should be made available for retrieval by a call to the
	 *                    method <code>getGeneratedKeys</code>
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there
	 *         are no more results
	 * @throws java.sql.SQLException if a database access error occurs or the
	 *                               elements of the <code>String</code> array passed to this
	 *                               method are not valid column names
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 * @see #getGeneratedKeys
	 * @since 1.4
	 */
	public boolean execute(String sql, String columnNames[]) throws SQLException {
		return wrappedStatement.execute(sql, columnNames);
	}

	// ---------------------------------------------------------------- output

	/**
	 * Returns the sql statement string (question marks replaced with set parameter values)
	 * that will be (or has been) executed by the {@link java.sql.PreparedStatement PreparedStatement} that this
	 * <code>LoggablePreparedStatement</code> is a wrapper for.

	 * @return the statement represented by this <code>LoggablePreparedStatement</code>
	 */
	public String getQueryString() {
		StringBuilder buf = new StringBuilder();
		int qMarkCount = 0;
		StringTokenizer tok = new StringTokenizer(sqlTemplate + ' ', "?");
		while (tok.hasMoreTokens()) {
			String oneChunk = tok.nextToken();
			buf.append(oneChunk);
			try {
				Object value;
				if (parameterValues.size() > 1 + qMarkCount) {
					value = parameterValues.get(1 + qMarkCount);
					qMarkCount++;
				} else {
					if (tok.hasMoreTokens()) {
						value = null;
					} else {
						value = "";
					}
				}
				buf.append(value);
			} catch (Throwable th) {
				buf.append("--- Exception occurs while creating query string for log: ").append(th.toString());
			}
		}
		return buf.toString().trim();
	}

	/**
	 * Saves the parameter value <code>obj</code> for the specified <code>position</code> for use in logging output
	 *
	 * @param position position (starting at 1) of the parameter to save
	 * @param obj java.lang.Object the parameter value to save
	 */
	private void saveQueryParamValue(int position, Object obj) {
		String strValue;
		if (obj instanceof String || obj instanceof Date) {
			strValue = "'" + obj + '\'';        // if we have a String or Date , include '' in the saved value
		} else {

			if (obj == null) {
				strValue = "<null>";            // convert null to the string null
			} else {
				strValue = obj.toString();      // unknown object (includes all Numbers), just call toString
			}
		}

		// if we are setting a position larger than current size of parameterValues, first make it larger
		while (position >= parameterValues.size()) {
			parameterValues.add(null);
		}
		parameterValues.set(position, strValue);
	}
}
