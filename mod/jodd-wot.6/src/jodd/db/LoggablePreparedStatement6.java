// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

/**
 * A {@link LoggablePreparedStatement} for JDK6.
 */
@SuppressWarnings("RedundantInterfaceDeclaration")
public class LoggablePreparedStatement6 extends LoggablePreparedStatement implements PreparedStatement {

	public LoggablePreparedStatement6(Connection connection, String sql) throws SQLException {
		super(connection, sql);
	}

	public LoggablePreparedStatement6(Connection connection, String sql, int resultType, int resultSetConcurrency) throws SQLException {
		super(connection, sql, resultType, resultSetConcurrency);
	}

	public LoggablePreparedStatement6(Connection connection, String sql, int resultType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		super(connection, sql, resultType, resultSetConcurrency, resultSetHoldability);
	}

	public LoggablePreparedStatement6(Connection connection, String sql, int generateKeys) throws SQLException {
		super(connection, sql, generateKeys);
	}

	public LoggablePreparedStatement6(Connection connection, String sql, String[] columnNames) throws SQLException {
		super(connection, sql, columnNames);
	}

	// ---------------------------------------------------------------- prepare statement jdk6 methods

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		((PreparedStatement)wrappedStatement).setRowId(parameterIndex, x);
		saveQueryParamValue(parameterIndex, x);
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		((PreparedStatement)wrappedStatement).setNString(parameterIndex, value);
		saveQueryParamValue(parameterIndex, value);
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setNCharacterStream(parameterIndex, value, length);
		saveQueryParamValueName(parameterIndex, "NCharacterStream", length);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		((PreparedStatement)wrappedStatement).setNClob(parameterIndex, value);
		saveQueryParamValueName(parameterIndex, "NClob", -1);
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setClob(parameterIndex, reader, length);
		saveQueryParamValueName(parameterIndex, "Clob", length);
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setBlob(parameterIndex, inputStream, length);
		saveQueryParamValueName(parameterIndex, "Blob", length);
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setNClob(parameterIndex, reader, length);
		saveQueryParamValueName(parameterIndex, "NClob", length);
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		((PreparedStatement)wrappedStatement).setSQLXML(parameterIndex, xmlObject);
		saveQueryParamValue(parameterIndex, xmlObject);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setAsciiStream(parameterIndex, x, length);
		saveQueryParamValueName(parameterIndex, "AsciiStream", length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setBinaryStream(parameterIndex, x, length);
		saveQueryParamValueName(parameterIndex, "BinaryStream", length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		((PreparedStatement)wrappedStatement).setCharacterStream(parameterIndex, reader, length);
		saveQueryParamValueName(parameterIndex, "CharacterStream", length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		((PreparedStatement)wrappedStatement).setAsciiStream(parameterIndex, x);
		saveQueryParamValueName(parameterIndex, "AsciiStream", -1);
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		((PreparedStatement)wrappedStatement).setBinaryStream(parameterIndex, x);
		saveQueryParamValueName(parameterIndex, "BinaryStream", -1);
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement)wrappedStatement).setCharacterStream(parameterIndex, reader);
		saveQueryParamValueName(parameterIndex, "CharacterStream", -1);
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		((PreparedStatement)wrappedStatement).setNCharacterStream(parameterIndex, value);
		saveQueryParamValueName(parameterIndex, "NCharacterStream", -1);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement)wrappedStatement).setClob(parameterIndex, reader);
		saveQueryParamValueName(parameterIndex, "Clob", -1);
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		((PreparedStatement)wrappedStatement).setBlob(parameterIndex, inputStream);
		saveQueryParamValueName(parameterIndex, "Blob", -1);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement)wrappedStatement).setNClob(parameterIndex, reader);
		saveQueryParamValueName(parameterIndex, "NClob", -1);
	}

	// ---------------------------------------------------------------- statement jdk6 methods

	public boolean isClosed() throws SQLException {
		return ((PreparedStatement)wrappedStatement).isClosed();
	}

	public void setPoolable(boolean poolable) throws SQLException {
		((PreparedStatement)wrappedStatement).setPoolable(poolable);
	}

	public boolean isPoolable() throws SQLException {
		return ((PreparedStatement)wrappedStatement).isPoolable();
	}

	// ---------------------------------------------------------------- java.sql.Wrapper jdk6 methods

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return ((PreparedStatement)wrappedStatement).unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return ((PreparedStatement)wrappedStatement).isWrapperFor(iface);
	}

}