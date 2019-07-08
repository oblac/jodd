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

package jodd.db.debug;

import jodd.typeconverter.Converter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public abstract class BaseLoggableStatement<S extends Statement> implements Statement {

	protected final S wrappedStatement;
	protected final String sqlTemplate;

	public BaseLoggableStatement(final S wrappedStatement, final String sqlTemplate) {
		this.wrappedStatement = wrappedStatement;
		this.sqlTemplate = sqlTemplate;
	}

	// ---------------------------------------------------------------- query

	protected ArrayList<String> parameterValues;

	/**
	 * Returns the query string.
	 */
	public String getQueryString() {
		if (sqlTemplate == null) {
			return toString();
		}
		if (parameterValues == null) {
			return sqlTemplate;
		}

		final StringBuilder sb = new StringBuilder();

		int qMarkCount = 0;
		final StringTokenizer tok = new StringTokenizer(sqlTemplate + ' ', "?");

		while (tok.hasMoreTokens()) {
			final String oneChunk = tok.nextToken();
			sb.append(oneChunk);
			try {
				Object value = null;
				if (parameterValues.size() > 1 + qMarkCount) {
					value = parameterValues.get(1 + qMarkCount);
					qMarkCount++;
				} else {
					if (!tok.hasMoreTokens()) {
						value = "";
					}
				}
				if (value == null) {
					value = "?";
				}
				sb.append(value);
			} catch (Throwable th) {
				sb.append("--- Building query failed: ").append(th.toString());
			}
		}
		return sb.toString().trim();
	}

	/**
	 * Saves the parameter value <code>obj</code> for the specified
	 * <code>position</code> for use in logging output.
	 *
	 * @param position position (starting at 1) of the parameter to save
	 * @param obj      java.lang.Object the parameter value to save
	 */
	protected void saveQueryParamValue(final int position, final Object obj) {
		final String strValue;
		if (obj instanceof String || obj instanceof Date) {
			strValue = "'" + obj + '\''; // if we have a String or Date, include '' in the saved value
		} else if (obj instanceof LocalDateTime || obj instanceof LocalDate || obj instanceof LocalTime) {
			strValue = "'" + Converter.get().toString(obj) + '\''; // time as string with '
		} else if (obj == null) {
			strValue = "<null>"; // convert null to the string null
		} else {
			strValue = Converter.get().toString(obj); // all other objects (includes all Numbers, arrays, etc)
		}

		// if we are setting a position larger than current size of parameterValues,
		// first make it larger
		if (parameterValues == null) {
			parameterValues = new ArrayList<>();
		}

		while (position >= parameterValues.size()) {
			parameterValues.add(null);
		}
		parameterValues.set(position, strValue);
	}

	// ---------------------------------------------------------------- statement

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		return wrappedStatement.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return wrappedStatement.executeUpdate(sql);
	}

	@Override
	public void close() throws SQLException {
		wrappedStatement.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return wrappedStatement.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		wrappedStatement.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {
		return wrappedStatement.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		wrappedStatement.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		wrappedStatement.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return wrappedStatement.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		wrappedStatement.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {
		wrappedStatement.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return wrappedStatement.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		wrappedStatement.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		wrappedStatement.setCursorName(name);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return wrappedStatement.execute(sql);
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return wrappedStatement.getResultSet();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return wrappedStatement.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return wrappedStatement.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		wrappedStatement.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return wrappedStatement.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		wrappedStatement.setFetchDirection(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return wrappedStatement.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return wrappedStatement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return wrappedStatement.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		wrappedStatement.addBatch(sql);
	}

	@Override
	public void clearBatch() throws SQLException {
		wrappedStatement.clearBatch();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return wrappedStatement.executeBatch();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return wrappedStatement.getConnection();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return wrappedStatement.getMoreResults(current);
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return wrappedStatement.getGeneratedKeys();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return wrappedStatement.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return wrappedStatement.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return wrappedStatement.executeUpdate(sql, columnNames);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return wrappedStatement.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return wrappedStatement.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return wrappedStatement.execute(sql, columnNames);
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return wrappedStatement.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return wrappedStatement.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		wrappedStatement.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return wrappedStatement.isPoolable();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		wrappedStatement.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return wrappedStatement.isCloseOnCompletion();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return wrappedStatement.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return wrappedStatement.isWrapperFor(iface);
	}
}
