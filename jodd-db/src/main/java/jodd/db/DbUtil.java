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

import jodd.db.type.SqlType;
import jodd.typeconverter.Converter;
import jodd.typeconverter.TypeConverterManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Various DB utilities.
 */
public class DbUtil {

	/**
	 * Closes statement safely without throwing an exception.
	 */
	public static void close(final Statement statement) {
		if (statement == null) {
			return;
		}

		try {
			statement.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	/**
	 * Closes result set safely without throwing an exception.
	 */
	public static void close(final ResultSet resultSet) {
		if (resultSet == null) {
			return;
		}
		try {
			resultSet.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	/**
	 * Returns long value of very first column in result set.
	 */
	public static long getFirstLong(final ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			return resultSet.getLong(1);
		}
		return -1;
	}

	/**
	 * Returns int value of very first column in result set.
	 */
	public static int getFirstInt(final ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			return resultSet.getInt(1);
		}
		return -1;
	}

	public static Object getFirstObject(final ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			return resultSet.getObject(1);
		}
		return null;
	}

	/**
	 * Sets prepared statement object using target SQL type.
	 * Here Jodd makes conversion and not JDBC driver.
	 * See: http://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
	 */
	public static void setPreparedStatementObject(final PreparedStatement preparedStatement, final int index, final Object value, final int targetSqlType) throws SQLException {
		if (value == null) {
			preparedStatement.setNull(index, Types.NULL);
			return;
		}

		switch (targetSqlType) {
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CHAR:
				preparedStatement.setString(index, Converter.get().toString(value));
				break;

			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				preparedStatement.setInt(index, Converter.get().toIntValue(value));
				break;

			case Types.BIGINT:
				preparedStatement.setLong(index, Converter.get().toLongValue(value));
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				preparedStatement.setBoolean(index, Converter.get().toBooleanValue(value));
				break;

			case Types.DATE:
				preparedStatement.setDate(index, TypeConverterManager.get().convertType(value, java.sql.Date.class));
				break;

			case Types.NUMERIC:
			case Types.DECIMAL:
				preparedStatement.setBigDecimal(index, Converter.get().toBigDecimal(value));
				break;

			case Types.DOUBLE:
				preparedStatement.setDouble(index, Converter.get().toDoubleValue(value));
				break;

			case Types.REAL:
			case Types.FLOAT:
				preparedStatement.setFloat(index, Converter.get().toFloatValue(value));
			    break;

			case Types.TIME:
				preparedStatement.setTime(index, TypeConverterManager.get().convertType(value, java.sql.Time.class));
				break;

			case Types.TIMESTAMP:
				preparedStatement.setTimestamp(index, TypeConverterManager.get().convertType(value, Timestamp.class));
				break;

			case Types.BINARY:
			case Types.VARBINARY:
				preparedStatement.setBytes(index, TypeConverterManager.get().convertType(value, byte[].class));
				break;

			default:
				if (targetSqlType != SqlType.DB_SQLTYPE_NOT_AVAILABLE) {
					preparedStatement.setObject(index, value, targetSqlType);
				} else {
					preparedStatement.setObject(index, value);
				}
		}

	}

}