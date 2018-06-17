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
import jodd.db.type.SqlType;
import jodd.db.type.SqlTypeManager;

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
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
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
public class DbQuery<Q extends DbQuery> extends DbQueryBase<Q> {

	/**
	 * Creates new query.
	 */
	public DbQuery(final DbOom dbOom, final Connection conn, final String sqlString) {
		super(dbOom);
		this.connection = conn;
		this.sqlString = sqlString;
	}

	public static DbQuery query(final Connection conn, final String sqlString) {
		return new DbQuery(DbOom.get(), conn, sqlString);
	}

	/**
	 * Creates a new query from {@link DbSession}.
	 */
	public DbQuery(final DbOom dbOom, final DbSession session, final String sqlString) {
		super(dbOom);
		initSession(session);
		this.session.attachQuery(this);
		this.sqlString = sqlString;
	}

	public static DbQuery query(final DbSession session, final String sqlString) {
		return new DbQuery(DbOom.get(), session, sqlString);
	}

	/**
	 * Creates a new query using default session provider.
	 */
	public DbQuery(final DbOom dbOom, final String sqlString) {
		this(dbOom, (DbSession)null, sqlString);
	}

	public static DbQuery query(final String sqlString) {
		return new DbQuery(DbOom.get(), sqlString);
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
	public Q clearParameters() {
		init();
		if (preparedStatement == null) {
			return _this();
		}
		try {
			preparedStatement.clearParameters();
		} catch (SQLException sex) {
			throw new DbSqlException(sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- methods for setting statement parameters

	private void throwSetParamError(final int index, final Exception ex) {
		throw new DbSqlException(this, "Invalid SQL parameter with index: " + index, ex);
	}

	private void throwSetParamError(final String param, final Exception ex) {
		throw new DbSqlException(this, "Invalid SQL parameter with name: " + param, ex);
	}

	// ---------------------------------------------------------------- null

	public Q setNull(final int index, final int type) {
		initPrepared();
		try {
			preparedStatement.setNull(index, type);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + index, sex);
		}
		return _this();
	}

	public Q setNull(final String param, final int type) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setNull(position, type);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + param, sex);
		}
		return _this();
	}

	public Q setNull(final int index, final int type, final String typeName) {
		initPrepared();
		try {
			preparedStatement.setNull(index, type, typeName);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + index, sex);
		}
		return _this();
	}

	public Q setNull(final String param, final int value, final String typeName) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setNull(position, value, typeName);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- int

	public Q setInteger(final int index, final int value) {
		initPrepared();
		try {
			preparedStatement.setInt(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setInteger(final String param, final int value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setInt(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q outInteger(final int index) {
		return registerOutParameter(index, Types.INTEGER);
	}
	public Q outInteger(final String param) {
		return registerOutParameter(param, Types.INTEGER);
	}

	// ---------------------------------------------------------------- Integer

	public Q setInteger(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.INTEGER);
		}
		else {
			setInteger(index, value.intValue());
		}
		return _this();
	}

	public Q setInteger(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.INTEGER);
		}
		else {
			setInteger(param, value.intValue());
		}
		return _this();
	}

	// ---------------------------------------------------------------- boolean

	public Q setBoolean(final int index, final boolean value) {
		initPrepared();
		try {
			preparedStatement.setBoolean(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setBoolean(final String param, final boolean value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setBoolean(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q outBoolean(final int index) {
		return registerOutParameter(index, Types.BOOLEAN);
	}
	public Q outBoolean(final String param) {
		return registerOutParameter(param, Types.BOOLEAN);
	}

	// ---------------------------------------------------------------- Boolean

	public Q setBoolean(final int index, final Boolean value) {
		if (value == null) {
			setNull(index, Types.BOOLEAN);
		}
		else {
			setBoolean(index, value.booleanValue());
		}
		return _this();
	}

	public Q setBoolean(final String param, final Boolean value) {
		if (value == null) {
			setNull(param, Types.BOOLEAN);
		}
		else {
			setBoolean(param, value.booleanValue());
		}
		return _this();
	}

	// ---------------------------------------------------------------- long

	public Q setLong(final int index, final long value) {
		initPrepared();
		try {
			preparedStatement.setLong(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setLong(final String param, final long value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setLong(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- Long

	public Q setLong(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.BIGINT);
		}
		else {
			setLong(index, value.longValue());
		}
		return _this();
	}

	public Q setLong(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.BIGINT);
		}
		else {
			setLong(param, value.longValue());
		}
		return _this();
	}

	public Q outLong(final int index) {
		return registerOutParameter(index, Types.BIGINT);
	}
	public Q outLong(final String param) {
		return registerOutParameter(param, Types.BIGINT);
	}

	// ---------------------------------------------------------------- byte

	public Q setByte(final int index, final byte value) {
		initPrepared();
		try {
			preparedStatement.setByte(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setByte(final String param, final byte value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setByte(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q outByte(final int index) {
		return registerOutParameter(index, Types.SMALLINT);
	}
	public Q outByte(final String param) {
		return registerOutParameter(param, Types.SMALLINT);
	}

	// ---------------------------------------------------------------- Byte

	public Q setByte(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.SMALLINT);
		}
		else {
			setByte(index, value.byteValue());
		}
		return _this();
	}

	public Q setByte(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.SMALLINT);
		}
		else {
			setByte(param, value.byteValue());
		}
		return _this();
	}

	// ---------------------------------------------------------------- bytes[]

	public Q setBytes(final int index, final byte[] value) {
		initPrepared();
		try {
			preparedStatement.setBytes(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setBytes(final String param, final byte[] value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setBytes(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- double

	public Q setDouble(final int index, final double value) {
		initPrepared();
		try {
			preparedStatement.setDouble(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setDouble(final String param, final double value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setDouble(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q outDouble(final int index) {
		return registerOutParameter(index, Types.DOUBLE);
	}
	public Q outDouble(final String param) {
		return registerOutParameter(param, Types.DOUBLE);
	}

	// ---------------------------------------------------------------- Double

	public Q setDouble(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.DOUBLE);
		}
		else {
			setDouble(index, value.doubleValue());
		}
		return _this();
	}

	public Q setDouble(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.DOUBLE);
		}
		else {
			setDouble(param, value.doubleValue());
		}
		return _this();
	}


	// ---------------------------------------------------------------- float

	public Q setFloat(final int index, final float value) {
		initPrepared();
		try {
			preparedStatement.setFloat(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setFloat(final String param, final float value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setFloat(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q outFloat(final int index) {
		return registerOutParameter(index, Types.FLOAT);
	}
	public Q outFloat(final String param) {
		return registerOutParameter(param, Types.FLOAT);
	}

	// ---------------------------------------------------------------- Float

	public Q setFloat(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.FLOAT);
		}
		else {
			setFloat(index, value.floatValue());
		}
		return _this();
	}

	public Q setFloat(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.FLOAT);
		}
		else {
			setFloat(param, value.floatValue());
		}
		return _this();
	}


	// ---------------------------------------------------------------- short

	public Q setShort(final int index, final short value) {
		initPrepared();
		try {
			preparedStatement.setShort(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setShort(final String param, final short value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				final int index = position;
				preparedStatement.setShort(index, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- Short

	public Q setShort(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.SMALLINT);
		}
		else {
			setShort(index, value.shortValue());
		}
		return _this();
	}

	public Q setShort(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.SMALLINT);
		}
		else {
			setShort(param, value.shortValue());
		}
		return _this();
	}

	// ---------------------------------------------------------------- string

	public Q setString(final int index, final String value) {
		initPrepared();
		try {
			preparedStatement.setString(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setString(final String param, final String value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setString(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q outString(final int index) {
		return registerOutParameter(index, Types.VARCHAR);
	}
	public Q outString(final String param) {
		return registerOutParameter(param, Types.VARCHAR);
	}


	// ---------------------------------------------------------------- date

	public Q setDate(final int index, final Date value) {
		initPrepared();
		try {
			preparedStatement.setDate(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setDate(final String param, final Date value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setDate(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- time

	public Q setTime(final int index, final Time value) {
		initPrepared();
		try {
			preparedStatement.setTime(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setTime(final String param, final Time value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setTime(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- timestamp

	public Q setTimestamp(final int index, final Timestamp value) {
		initPrepared();
		try {
			preparedStatement.setTimestamp(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setTimestamp(final String param, final Timestamp value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setTimestamp(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- big decimal

	public Q setBigDecimal(final int index, final BigDecimal value) {
		initPrepared();
		try {
			preparedStatement.setBigDecimal(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setBigDecimal(final String param, final BigDecimal value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setBigDecimal(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- big integer

	public Q setBigInteger(final int index, final BigInteger value) {
		if (value == null) {
			setNull(index, Types.NUMERIC);
		}
		else {
			setLong(index, value.longValue());
		}
		return _this();
	}

	public Q setBigInteger(final String param, final BigInteger value) {
		if (value == null) {
			setNull(param, Types.NUMERIC);
		}
		else {
			setLong(param, value.longValue());
		}
		return _this();
	}


	// ---------------------------------------------------------------- URL


	public Q setURL(final int index, final URL value) {
		initPrepared();
		try {
			preparedStatement.setURL(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setURL(final String param, final URL value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setURL(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- BLOB

	public Q setBlob(final int index, final Blob value) {
		initPrepared();
		try {
			preparedStatement.setBlob(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setBlob(final String param, final Blob value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setBlob(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- CLOB

	public Q setClob(final int index, final Clob value) {
		initPrepared();
		try {
			preparedStatement.setClob(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setClob(final String param, final Clob value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setClob(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- Array

	public Q setArray(final int index, final Array value) {
		initPrepared();
		try {
			preparedStatement.setArray(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setArray(final String param, final Array value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setArray(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- Ref

	public Q setRef(final int index, final Ref value) {
		initPrepared();
		try {
			preparedStatement.setRef(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setRef(final String param, final Ref value) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setRef(position, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- ascii streams

	public Q setAsciiStream(final int index, final InputStream stream) {
		initPrepared();
		try {
			preparedStatement.setAsciiStream(index, stream, stream.available());
		} catch (IOException | SQLException ioex) {
			throwSetParamError(index, ioex);
		}
		return _this();
	}

	public Q setAsciiStream(final String param, final InputStream stream) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setAsciiStream(position, stream, stream.available());
			}
		} catch (IOException | SQLException ioex) {
			throwSetParamError(param, ioex);
		}
		return _this();
	}

	public Q setAsciiStream(final int index, final InputStream stream, final int length) {
		initPrepared();
		try {
			preparedStatement.setAsciiStream(index, stream, length);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setAsciiStream(final String param, final InputStream stream, final int length) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setAsciiStream(position, stream, length);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	public Q setCharacterStream(final int index, final Reader reader, final int length) {
		initPrepared();
		try {
			preparedStatement.setCharacterStream(index, reader, length);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	public Q setCharacterStream(final String param, final Reader reader, final int length) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setCharacterStream(position, reader, length);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}


	// ---------------------------------------------------------------- bean

	/**
	 * Sets bean parameters from bean. Non-existing bean properties are ignored.
	 */
	public Q setBean(final String beanName, final Object bean) {
		if (bean == null) {
			return _this();
		}
		init();
		final String beanNamePrefix = beanName + '.';

		query.forEachNamedParameter(p -> {
			final String paramName = p.name;

			if (paramName.startsWith(beanNamePrefix)) {
				final String propertyName = paramName.substring(beanNamePrefix.length());

				if (BeanUtil.declared.hasRootProperty(bean, propertyName)) {
					final Object value = BeanUtil.declared.getProperty(bean, propertyName);
					setObject(paramName, value);
				}
			}

		});

		return _this();
	}

	// ---------------------------------------------------------------- map

	/**
	 * Sets properties from the map.
	 */
	public Q setMap(final Map parameters) {
		if (parameters == null) {
			return _this();
		}
		init();
		query.forEachNamedParameter(p -> {
			final String paramName = p.name;
			setObject(paramName, parameters.get(paramName));
		});
		return _this();
	}


	// ---------------------------------------------------------------- objects

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
	public Q setObject(final int index, final Object object, final int targetSqlType) {
		initPrepared();
		try {
			preparedStatement.setObject(index, object, targetSqlType);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
	public Q setObject(final String param, final Object object, final int targetSqlType) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setObject(position, object, targetSqlType);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
    void setObject(final int index, final Object object, final int targetSqlType, final int scale) {
		initPrepared();
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
    void setObject(final String param, final Object object, final int targetSqlType, final int scale) {
		initPrepared();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				preparedStatement.setObject(position, object, targetSqlType, scale);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
	}



	// ---------------------------------------------------------------- object ex

	/**
	 * @see #setObject(String, Object, Class, int)
	 */
	public Q setObject(final int index, final Object value) {
		setObject(index, value, null, Types.OTHER);
		return _this();
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
	public Q setObject(final int index, final Object value, final Class<? extends SqlType> sqlTypeClass, final int dbSqlType) {
		init();
		if (value == null) {
			setNull(index, Types.NULL);
			return _this();
		}
		final SqlType sqlType;
		if (sqlTypeClass != null) {
			sqlType = SqlTypeManager.get().lookupSqlType(sqlTypeClass);
		} else {
			sqlType = SqlTypeManager.get().lookup(value.getClass());
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
		return _this();
	}

	/**
	 * @see #setObject(String, Object, Class, int)
	 */
	public Q setObject(final String param, final Object value) {
		setObject(param, value, null, Types.OTHER);
		return _this();
	}

	/**
	 * @see #setObject(String, Object, Class, int) 
	 */
	public Q setObject(final String param, final Object value, final Class<? extends SqlType> sqlTypeClass, final int dbSqlType) {
		init();
		final int[] positions = query.getNamedParameterIndices(param);
		for (final int position : positions) {
			setObject(position, value, sqlTypeClass, dbSqlType);
		}
		return _this();
	}


	// ---------------------------------------------------------------- set object shortcuts

	/**
	 * Sets an array of objects parameters in given order.
	 */
	public Q setObjects(final Object... objects) {
		int index = 1;
		for (final Object object : objects) {
			setObject(index++, object);
		}
		return _this();
	}

	/**
	 * Sets sql parameters from two arrays: names and values.
	 */
	public Q setObjects(final String[] names, final Object[] values) {
		init();
		if (names.length != values.length) {
			throw new DbSqlException(this, "Different number of parameter names and values");
		}
		for (int i = 0; i < names.length; i++) {
			setObject(names[i], values[i]);
		}
		return _this();
	}

	// ---------------------------------------------------------------- batch

	/**
	 * Sets batch parameters with given array of values.
	 */
	public Q setBatch(final String name, final int[] array, int startingIndex) {
		init();
		final int batchSize = query.getBatchParameterSize(name);

		for (int i = 1; i <= batchSize; i++) {
			final String paramName = name + '.' + i;

			if (startingIndex < array.length) {
				setInteger(paramName, array[startingIndex]);
			} else {
				setNull(paramName, Types.INTEGER);
			}
			startingIndex++;
		}
		return _this();
	}
	/**
	 * Sets batch parameters with given array of values.
	 */
	public Q setBatch(final String name, final long[] array, int startingIndex) {
		init();
		final int batchSize = query.getBatchParameterSize(name);

		for (int i = 1; i <= batchSize; i++) {
			final String paramName = name + '.' + i;

			if (startingIndex < array.length) {
				setLong(paramName, array[startingIndex]);
			} else {
				setNull(paramName, Types.INTEGER);
			}
			startingIndex++;
		}
		return _this();
	}

	/**
	 * Sets batch parameters with given array of values.
	 */
	public Q setBatch(final String name, final Object[] array, int startingIndex) {
		init();
		final int batchSize = query.getBatchParameterSize(name);

		for (int i = 1; i <= batchSize; i++) {
			final String paramName = name + '.' + i;

			if (startingIndex < array.length) {
				setObject(paramName, array[startingIndex]);
			} else {
				setObject(paramName, null);
			}
			startingIndex++;
		}
		return _this();
	}

	// ---------------------------------------------------------------- utils

	private void initPrepared() {
		init();
		if (preparedStatement == null) {
			throw new DbSqlException("Prepared statement not initialized.");
		}
	}
	private void initCallable() {
		init();
		if (callableStatement == null) {
			throw new DbSqlException("Callable statement not initialized.");
		}
	}

	private Q registerOutParameter(final int index, final int type) {
		initCallable();
		try {
			callableStatement.registerOutParameter(index,type);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return _this();
	}
	private Q registerOutParameter(final String param, final int type) {
		initCallable();
		final int[] positions = query.getNamedParameterIndices(param);
		try {
			for (final int position : positions) {
				callableStatement.registerOutParameter(position, type);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return _this();
	}

	// ---------------------------------------------------------------- close

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Q autoClose() {
		super.autoClose();
		return _this();
	}

}
