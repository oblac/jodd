// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.type.SqlType;
import jodd.typeconverter.Convert;
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
	public static void close(Statement statement) {
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
	public static void close(ResultSet resultSet) {
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
	public static long getFirstLong(ResultSet resultSet) throws SQLException {
		if (resultSet.next() == true) {
			return resultSet.getLong(1);
		}
		return -1;
	}

	/**
	 * Returns int value of very first column in result set.
	 */
	public static int getFirstInt(ResultSet resultSet) throws SQLException {
		if (resultSet.next() == true) {
			return resultSet.getInt(1);
		}
		return -1;
	}

	/**
	 * Sets prepared statement object using target SQL type.
	 * Here Jodd makes conversion and not JDBC driver.
	 * See: http://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
	 */
	public static void setPreparedStatementObject(PreparedStatement preparedStatement, int index, Object value, int targetSqlType) throws SQLException {
		if (value == null) {
			preparedStatement.setNull(index, Types.NULL);
		}

		switch (targetSqlType) {
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CHAR:
				preparedStatement.setString(index, Convert.toString(value));
				break;

			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				preparedStatement.setInt(index, Convert.toIntValue(value));
				break;

			case Types.BIGINT:
				preparedStatement.setLong(index, Convert.toLongValue(value));
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				preparedStatement.setBoolean(index, Convert.toBooleanValue(value));
				break;

			case Types.DATE:
				preparedStatement.setDate(index, TypeConverterManager.convertType(value, java.sql.Date.class));
				break;

			case Types.NUMERIC:
			case Types.DECIMAL:
				preparedStatement.setBigDecimal(index, Convert.toBigDecimal(value));
				break;

			case Types.DOUBLE:
				preparedStatement.setDouble(index, Convert.toDoubleValue(value));
				break;

			case Types.REAL:
			case Types.FLOAT:
				preparedStatement.setFloat(index, Convert.toFloatValue(value));
			    break;

			case Types.TIME:
				preparedStatement.setTime(index, TypeConverterManager.convertType(value, java.sql.Time.class));
				break;

			case Types.TIMESTAMP:
				preparedStatement.setTimestamp(index, TypeConverterManager.convertType(value, Timestamp.class));
				break;

			case Types.BINARY:
			case Types.VARBINARY:
				preparedStatement.setBytes(index, TypeConverterManager.convertType(value, byte[].class));
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