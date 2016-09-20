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

import jodd.bean.BeanUtil;
import jodd.util.CharUtil;
import jodd.util.collection.IntArrayList;
import jodd.db.querymap.QueryMap;
import jodd.db.type.SqlTypeManager;
import jodd.db.type.SqlType;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Encapsulates {@link Statement} and all its operations.
 * <p>
 * It may be:
 * <ul>
 * <li>used in an un-managed way, created directly from connection;</li>
 * <li>managed by {@link DbSession};</li>
 * <li>managed by {@link jodd.db.DbThreadSession} i.e. {@link ThreadDbSessionHolder}.</li>
 * </ul>
 * <p>
 * Query life-cycle states:
 * <ul>
 * <li>1) created:     statement == null, initialized == false</li>
 * <li>2) initialized: statement != null, initialized == true</li>
 * <li>3) closed:      statement == null, initialized == true</li>
 * </ul>
 */
public class DbQuery extends DbQueryBase {

	/**
	 * Creates new query.
	 */
	public DbQuery(Connection conn, String sqlString) {
		this.connection = conn;
		this.sqlString = preprocessSql(sqlString);
	}

	/**
	 * Creates a new query from {@link DbSession}.
	 */
	public DbQuery(DbSession session, String sqlString) {
		initSession(session);

		this.session.attachQuery(this);
		this.sqlString = preprocessSql(sqlString);
	}

	/**
	 * Creates a new query using default session provider.
	 */
	public DbQuery(String sqlString) {
		this((DbSession)null, sqlString);
	}

	// ---------------------------------------------------------------- sql map

	/**
	 * Pre-process SQL before using it. If string starts with a non-ascii char
	 * or it has no spaces, it will be loaded from the query map.
	 */
	protected String preprocessSql(String sqlString) {
		// quickly detect if sql string is a key
		if (!CharUtil.isAlpha(sqlString.charAt(0))) {
			sqlString = sqlString.substring(1);
		}
		else if (sqlString.indexOf(' ') != -1) {
			return sqlString;
		}

		QueryMap queryMap = DbManager.getInstance().getQueryMap();

		if (queryMap != null) {
			String sqlFromMap = queryMap.getQuery(sqlString);

			if (sqlFromMap != null) {
				sqlString = sqlFromMap.trim();
			}
		}

		return sqlString;
	}

	// ---------------------------------------------------------------- additional statement parameters

	/**
	 * Clears the current parameter values immediately.
	 * <p>
	 * In general, parameter values remain in force for repeated use of a
	 * statement. Setting a parameter value automatically clears its
	 * previous value. However, in some cases it is useful to immediately
	 * release the resources used by the current parameter values; this can
	 * be done by calling the method <code>clearParameters</code>.
	 */
	public void clearParameters() {
		init();
		if (preparedStatement == null) {
			return;		// ignore
		}
		try {
			preparedStatement.clearParameters();
		} catch (SQLException sex) {
			throw new DbSqlException(sex);
		}
	}


	// ---------------------------------------------------------------- methods for setting statement parameters

	private void throwSetParamError(int index, Exception ex) {
		throw new DbSqlException(this, "Invalid SQL parameter with index: " + index, ex);
	}

	private void throwSetParamError(String param, Exception ex) {
		throw new DbSqlException(this, "Invalid SQL parameter with name: " + param, ex);
	}

	// ---------------------------------------------------------------- null

	public void setNull(int index, int type) {
		init();
		try {
			preparedStatement.setNull(index, type);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + index, sex);
		}
	}

	public void setNull(String param, int type) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setNull(positions.get(i), type);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + param, sex);
		}

	}

	public void setNull(int index, int type, String typeName) {
		init();
		try {
			preparedStatement.setNull(index, type, typeName);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + index, sex);
		}
	}

	public void setNull(String param, int value, String typeName) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setNull(positions.get(i), value, typeName);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + param, sex);
		}
	}

	// ---------------------------------------------------------------- int

	public void setInteger(int index, int value) {
		init();
		try {
			preparedStatement.setInt(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setInteger(String param, int value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setInt(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- Integer

	public void setInteger(int index, Number value) {
		if (value == null) {
			setNull(index, Types.INTEGER);
			return;
		}
		setInteger(index, value.intValue());
	}

	public void setInteger(String param, Number value) {
		if (value == null) {
			setNull(param, Types.INTEGER);
			return;
		}
		setInteger(param, value.intValue());
	}

	// ---------------------------------------------------------------- boolean

	public void setBoolean(int index, boolean value) {
		init();
		try {
			preparedStatement.setBoolean(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setBoolean(String param, boolean value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBoolean(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Boolean

	public void setBoolean(int index, Boolean value) {
		if (value == null) {
			setNull(index, Types.BOOLEAN);
			return;
		}
		setBoolean(index, value.booleanValue());
	}

	public void setBoolean(String param, Boolean value) {
		if (value == null) {
			setNull(param, Types.BOOLEAN);
			return;
		}
		setBoolean(param, value.booleanValue());
	}


	// ---------------------------------------------------------------- long

	public void setLong(int index, long value) {
		init();
		try {
			preparedStatement.setLong(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setLong(String param, long value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setLong(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Long

	public void setLong(int index, Number value) {
		if (value == null) {
			setNull(index, Types.INTEGER);
			return;
		}
		setLong(index, value.longValue());
	}

	public void setLong(String param, Number value) {
		if (value == null) {
			setNull(param, Types.INTEGER);
			return;
		}
		setLong(param, value.longValue());
	}


	// ---------------------------------------------------------------- byte

	public void setByte(int index, byte value) {
		init();
		try {
			preparedStatement.setByte(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setByte(String param, byte value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setByte(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Byte

	public void setByte(int index, Number value) {
		if (value == null) {
			setNull(index, Types.SMALLINT);
			return;
		}
		setByte(index, value.byteValue());
	}

	public void setByte(String param, Number value) {
		if (value == null) {
			setNull(param, Types.SMALLINT);
			return;
		}
		setByte(param, value.byteValue());
	}

	// ---------------------------------------------------------------- bytes[]

	public void setBytes(int index, byte[] value) {
		init();
		try {
			preparedStatement.setBytes(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setBytes(String param, byte[] value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBytes(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- double

	public void setDouble(int index, double value) {
		init();
		try {
			preparedStatement.setDouble(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setDouble(String param, double value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setDouble(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Double

	public void setDouble(int index, Number value) {
		if (value == null) {
			setNull(index, Types.DOUBLE);
			return;
		}
		setDouble(index, value.doubleValue());
	}

	public void setDouble(String param, Number value) {
		if (value == null) {
			setNull(param, Types.DOUBLE);
			return;
		}
		setDouble(param, value.doubleValue());
	}


	// ---------------------------------------------------------------- float

	public void setFloat(int index, float value) {
		init();
		try {
			preparedStatement.setFloat(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setFloat(String param, float value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setFloat(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Float

	public void setFloat(int index, Number value) {
		if (value == null) {
			setNull(index, Types.FLOAT);
			return;
		}
		setFloat(index, value.floatValue());
	}

	public void setFloat(String param, Number value) {
		if (value == null) {
			setNull(param, Types.FLOAT);
			return;
		}
		setFloat(param, value.floatValue());
	}


	// ---------------------------------------------------------------- short

	public void setShort(int index, short value) {
		init();
		try {
			preparedStatement.setShort(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setShort(String param, short value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				int index = positions.get(i);
				preparedStatement.setShort(index, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Short

	public void setShort(int index, Number value) {
		if (value == null) {
			setNull(index, Types.SMALLINT);
			return;
		}
		setShort(index, value.shortValue());
	}

	public void setShort(String param, Number value) {
		if (value == null) {
			setNull(param, Types.SMALLINT);
			return;
		}
		setShort(param, value.shortValue());
	}

	// ---------------------------------------------------------------- string

	public void setString(int index, String value) {
		init();
		try {
			preparedStatement.setString(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setString(String param, String value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setString(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- date

	public void setDate(int index, Date value) {
		init();
		try {
			preparedStatement.setDate(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setDate(String param, Date value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setDate(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- time

	public void setTime(int index, Time value) {
		init();
		try {
			preparedStatement.setTime(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setTime(String param, Time value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setTime(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- timestamp

	public void setTimestamp(int index, Timestamp value) {
		init();
		try {
			preparedStatement.setTimestamp(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setTimestamp(String param, Timestamp value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setTimestamp(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- big decimal

	public void setBigDecimal(int index, BigDecimal value) {
		init();
		try {
			preparedStatement.setBigDecimal(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setBigDecimal(String param, BigDecimal value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBigDecimal(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- big integer

	public void setBigInteger(int index, BigInteger value) {
		if (value == null) {
			setNull(index, Types.NUMERIC);
			return;
		}
		setLong(index, value.longValue());
	}

	public void setBigInteger(String param, BigInteger value) {
		if (value == null) {
			setNull(param, Types.NUMERIC);
			return;
		}
		setLong(param, value.longValue());
	}


	// ---------------------------------------------------------------- URL


	public void setURL(int index, URL value) {
		init();
		try {
			preparedStatement.setURL(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setURL(String param, URL value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setURL(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- BLOB

	public void setBlob(int index, Blob value) {
		init();
		try {
			preparedStatement.setBlob(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setBlob(String param, Blob value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBlob(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- CLOB

	public void setClob(int index, Clob value) {
		init();
		try {
			preparedStatement.setClob(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setClob(String param, Clob value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setClob(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- Array

	public void setArray(int index, Array value) {
		init();
		try {
			preparedStatement.setArray(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setArray(String param, Array value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setArray(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- Ref

	public void setRef(int index, Ref value) {
		init();
		try {
			preparedStatement.setRef(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setRef(String param, Ref value) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setRef(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- ascii streams

	public void setAsciiStream(int index, InputStream stream) {
		init();
		try {
			preparedStatement.setAsciiStream(index, stream, stream.available());
		} catch (IOException | SQLException ioex) {
			throwSetParamError(index, ioex);
		}
	}

	public void setAsciiStream(String param, InputStream stream) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setAsciiStream(positions.get(i), stream, stream.available());
			}
		} catch (IOException | SQLException ioex) {
			throwSetParamError(param, ioex);
		}
	}

	public void setAsciiStream(int index, InputStream stream, int length) {
		init();
		try {
			preparedStatement.setAsciiStream(index, stream, length);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setAsciiStream(String param, InputStream stream, int length) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setAsciiStream(positions.get(i), stream, length);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	public void setCharacterStream(int index, Reader reader, int length) {
		init();
		try {
			preparedStatement.setCharacterStream(index, reader, length);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	public void setCharacterStream(String param, Reader reader, int length) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setCharacterStream(positions.get(i), reader, length);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- bean

	/**
	 * Sets bean parameters from bean. Non-existing bean properties are ignored.
	 */
	public void setBean(String beanName, Object bean) {
		if (bean == null) {
			return;
		}
		init();
		beanName += '.';
		Iterator it = query.iterateNamedParameters();
		while (it.hasNext()) {
			String paramName = (String) it.next();
			if (paramName.startsWith(beanName) == true) {
				String propertyName = paramName.substring(beanName.length());
				if (BeanUtil.declared.hasRootProperty(bean, propertyName) == true) {
					Object value = BeanUtil.declared.getProperty(bean, propertyName);
					setObject(paramName, value);
				}
			}
		}
	}

	// ---------------------------------------------------------------- map

	/**
	 * Sets properties from the map.
	 */
	public void setMap(Map parameters) {
		if (parameters == null) {
			return;
		}
		init();
		Iterator it = query.iterateNamedParameters();
		while (it.hasNext()) {
			String paramName = (String) it.next();
			setObject(paramName, parameters.get(paramName));
		}
	}


	// ---------------------------------------------------------------- objects

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
	public void setObject(int index, Object object, int targetSqlType) {
		init();
		try {
			preparedStatement.setObject(index, object, targetSqlType);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
	public void setObject(String param, Object object, int targetSqlType) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setObject(positions.get(i), object, targetSqlType);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
    void setObject(int index, Object object, int targetSqlType, int scale) {
		init();
	    try {
		    preparedStatement.setObject(index, object, targetSqlType, scale);
	    } catch (SQLException sex) {
			throwSetParamError(index, sex);
	    }
    }

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
    void setObject(String param, Object object, int targetSqlType, int scale) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setObject(positions.get(i), object, targetSqlType, scale);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}



	// ---------------------------------------------------------------- object ex

	/**
	 * @see #setObject(String, Object, Class, int)
	 */
	public void setObject(int index, Object value) {
		setObject(index, value, null, Types.OTHER);
	}

	/**
	 * Sets object parameter in an advanced way.
	 * <p>
	 * First, it checks if object is <code>null</code> and invokes <code>setNull</code> if so.
	 * If object is not <code>null</code>, it tries to resolve {@link SqlType sql type} (by looking up
	 * or using provided class) and use it for setting data.
	 * If sql type is not found, default <code>setObject</code> is invoked.
	 */
	@SuppressWarnings({"unchecked"})
	public void setObject(int index, Object value, Class<? extends SqlType> sqlTypeClass, int dbSqlType) {
		init();
		if (value == null) {
			setNull(index, Types.NULL);
			return;
		}
		SqlType sqlType;
		if (sqlTypeClass != null) {
			sqlType = SqlTypeManager.lookupSqlType(sqlTypeClass);
		} else {
			sqlType = SqlTypeManager.lookup(value.getClass());
		}
		try {
			if ((sqlType != null) && (dbSqlType != SqlType.DB_SQLTYPE_NOT_AVAILABLE)) {
				sqlType.storeValue(preparedStatement, index, value, dbSqlType);
			} else {
				DbUtil.setPreparedStatementObject(preparedStatement, index, value, dbSqlType);
			}
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
	}

	/**
	 * @see #setObject(String, Object, Class, int)
	 */
	public void setObject(String param, Object value) {
		setObject(param, value, null, Types.OTHER);
	}

	/**
	 * @see #setObject(String, Object, Class, int) 
	 */
	public void setObject(String param, Object value, Class<? extends SqlType> sqlTypeClass, int dbSqlType) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		for (int i = 0; i < positions.size(); i++) {
			setObject(positions.get(i), value, sqlTypeClass, dbSqlType);
		}
	}


	// ---------------------------------------------------------------- set object shortcuts

	/**
	 * Sets an array of objects parameters in given order.
	 */
	public void setObjects(Object... objects) {
		int index = 1;
		for (Object object : objects) {
			setObject(index++, object);
		}
	}

	/**
	 * Sets sql parameters from two arrays: names and values.
	 */
	public void setObjects(String[] names, Object[] values) {
		init();
		if (names.length != values.length) {
			throw new DbSqlException(this, "Different number of parameter names and values");
		}
		for (int i = 0; i < names.length; i++) {
			setObject(names[i], values[i]);
		}
	}

	// ---------------------------------------------------------------- batch

	/**
	 * Sets batch parameters with given array of values.
	 */
	public void setBatch(String name, int[] array, int startingIndex) {
		init();
		int batchSize = query.getBatchParameterSize(name);

		for (int i = 1; i <= batchSize; i++) {
			String paramName = name + '.' + i;

			if (startingIndex < array.length) {
				setInteger(paramName, array[startingIndex]);
			} else {
				setNull(paramName, Types.INTEGER);
			}
			startingIndex++;
		}
	}
	/**
	 * Sets batch parameters with given array of values.
	 */
	public void setBatch(String name, long[] array, int startingIndex) {
		init();
		int batchSize = query.getBatchParameterSize(name);

		for (int i = 1; i <= batchSize; i++) {
			String paramName = name + '.' + i;

			if (startingIndex < array.length) {
				setLong(paramName, array[startingIndex]);
			} else {
				setNull(paramName, Types.INTEGER);
			}
			startingIndex++;
		}
	}

	/**
	 * Sets batch parameters with given array of values.
	 */
	public void setBatch(String name, Object[] array, int startingIndex) {
		init();
		int batchSize = query.getBatchParameterSize(name);

		for (int i = 1; i <= batchSize; i++) {
			String paramName = name + '.' + i;

			if (startingIndex < array.length) {
				setObject(paramName, array[startingIndex]);
			} else {
				setObject(paramName, null);
			}
			startingIndex++;
		}
	}

	// ---------------------------------------------------------------- close

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DbQuery autoClose() {
		super.autoClose();
		return this;
	}

}
