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
import jodd.util.CharUtil;
import jodd.util.collection.IntArrayList;

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
public class DbQuery<Q extends DbQuery> extends DbQueryBase<Q> {

	/**
	 * Creates new query.
	 */
	public DbQuery(final Connection conn, final String sqlString) {
		super(JoddDb.defaults().getQueryConfig(), JoddDb.defaults().isDebug());
		this.connection = conn;
		this.sqlString = preprocessSql(sqlString);
	}

	/**
	 * Creates a new query from {@link DbSession}.
	 */
	public DbQuery(final DbSession session, final String sqlString) {
		super(JoddDb.defaults().getQueryConfig(), JoddDb.defaults().isDebug());

		initSession(session);

		this.session.attachQuery(this);
		this.sqlString = preprocessSql(sqlString);
	}

	/**
	 * Creates a new query using default session provider.
	 */
	public DbQuery(final String sqlString) {
		this((DbSession)null, sqlString);
	}

	// ---------------------------------------------------------------- sql map

	/**
	 * Pre-process SQL before using it. If string starts with a non-ascii char
	 * or it has no spaces, it will be loaded from the query map.
	 */
	protected String preprocessSql(String sqlString) {

		// detects callable
		if (sqlString.charAt(0) == '{') {
			return sqlString;
		}

		// quickly detect if sql string is a key
		if (!CharUtil.isAlpha(sqlString.charAt(0))) {
			sqlString = sqlString.substring(1);
		}
		else if (sqlString.indexOf(' ') != -1) {
			return sqlString;
		}

		String sqlFromMap = JoddDb.defaults().getQueryMap().getQuery(sqlString);

		if (sqlFromMap != null) {
			sqlString = sqlFromMap.trim();
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
	public Q clearParameters() {
		init();
		if (preparedStatement == null) {
			return (Q) this;
		}
		try {
			preparedStatement.clearParameters();
		} catch (SQLException sex) {
			throw new DbSqlException(sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setNull(final String param, final int type) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setNull(positions.get(i), type);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + param, sex);
		}
		return (Q) this;
	}

	public Q setNull(final int index, final int type, final String typeName) {
		initPrepared();
		try {
			preparedStatement.setNull(index, type, typeName);
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + index, sex);
		}
		return (Q) this;
	}

	public Q setNull(final String param, final int value, final String typeName) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setNull(positions.get(i), value, typeName);
			}
		} catch (SQLException sex) {
			throw new DbSqlException(this, "Failed to set null to parameter: " + param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- int

	public Q setInteger(final int index, final int value) {
		initPrepared();
		try {
			preparedStatement.setInt(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setInteger(final String param, final int value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setInt(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setInteger(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.INTEGER);
		}
		else {
			setInteger(param, value.intValue());
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- boolean

	public Q setBoolean(final int index, final boolean value) {
		initPrepared();
		try {
			preparedStatement.setBoolean(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setBoolean(final String param, final boolean value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBoolean(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setBoolean(final String param, final Boolean value) {
		if (value == null) {
			setNull(param, Types.BOOLEAN);
		}
		else {
			setBoolean(param, value.booleanValue());
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- long

	public Q setLong(final int index, final long value) {
		initPrepared();
		try {
			preparedStatement.setLong(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setLong(final String param, final long value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setLong(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- Long

	public Q setLong(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.BIGINT);
		}
		else {
			setLong(index, value.longValue());
		}
		return (Q) this;
	}

	public Q setLong(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.BIGINT);
		}
		else {
			setLong(param, value.longValue());
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setByte(final String param, final byte value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setByte(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setByte(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.SMALLINT);
		}
		else {
			setByte(param, value.byteValue());
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- bytes[]

	public Q setBytes(final int index, final byte[] value) {
		initPrepared();
		try {
			preparedStatement.setBytes(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setBytes(final String param, final byte[] value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBytes(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- double

	public Q setDouble(final int index, final double value) {
		initPrepared();
		try {
			preparedStatement.setDouble(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setDouble(final String param, final double value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setDouble(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setDouble(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.DOUBLE);
		}
		else {
			setDouble(param, value.doubleValue());
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- float

	public Q setFloat(final int index, final float value) {
		initPrepared();
		try {
			preparedStatement.setFloat(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setFloat(final String param, final float value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setFloat(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setFloat(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.FLOAT);
		}
		else {
			setFloat(param, value.floatValue());
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- short

	public Q setShort(final int index, final short value) {
		initPrepared();
		try {
			preparedStatement.setShort(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setShort(final String param, final short value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				int index = positions.get(i);
				preparedStatement.setShort(index, value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- Short

	public Q setShort(final int index, final Number value) {
		if (value == null) {
			setNull(index, Types.SMALLINT);
		}
		else {
			setShort(index, value.shortValue());
		}
		return (Q) this;
	}

	public Q setShort(final String param, final Number value) {
		if (value == null) {
			setNull(param, Types.SMALLINT);
		}
		else {
			setShort(param, value.shortValue());
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- string

	public Q setString(final int index, final String value) {
		initPrepared();
		try {
			preparedStatement.setString(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setString(final String param, final String value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setString(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
		return (Q) this;
	}

	public Q setDate(final String param, final Date value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setDate(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- time

	public Q setTime(final int index, final Time value) {
		initPrepared();
		try {
			preparedStatement.setTime(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setTime(final String param, final Time value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setTime(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- timestamp

	public Q setTimestamp(final int index, final Timestamp value) {
		initPrepared();
		try {
			preparedStatement.setTimestamp(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setTimestamp(final String param, final Timestamp value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setTimestamp(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- big decimal

	public Q setBigDecimal(final int index, final BigDecimal value) {
		initPrepared();
		try {
			preparedStatement.setBigDecimal(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setBigDecimal(final String param, final BigDecimal value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBigDecimal(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- big integer

	public Q setBigInteger(final int index, final BigInteger value) {
		if (value == null) {
			setNull(index, Types.NUMERIC);
		}
		else {
			setLong(index, value.longValue());
		}
		return (Q) this;
	}

	public Q setBigInteger(final String param, final BigInteger value) {
		if (value == null) {
			setNull(param, Types.NUMERIC);
		}
		else {
			setLong(param, value.longValue());
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- URL


	public Q setURL(final int index, final URL value) {
		initPrepared();
		try {
			preparedStatement.setURL(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setURL(final String param, final URL value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setURL(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- BLOB

	public Q setBlob(final int index, final Blob value) {
		initPrepared();
		try {
			preparedStatement.setBlob(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setBlob(final String param, final Blob value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setBlob(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- CLOB

	public Q setClob(final int index, final Clob value) {
		initPrepared();
		try {
			preparedStatement.setClob(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setClob(final String param, final Clob value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setClob(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- Array

	public Q setArray(final int index, final Array value) {
		initPrepared();
		try {
			preparedStatement.setArray(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setArray(final String param, final Array value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setArray(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- Ref

	public Q setRef(final int index, final Ref value) {
		initPrepared();
		try {
			preparedStatement.setRef(index, value);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setRef(final String param, final Ref value) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setRef(positions.get(i), value);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- ascii streams

	public Q setAsciiStream(final int index, final InputStream stream) {
		initPrepared();
		try {
			preparedStatement.setAsciiStream(index, stream, stream.available());
		} catch (IOException | SQLException ioex) {
			throwSetParamError(index, ioex);
		}
		return (Q) this;
	}

	public Q setAsciiStream(final String param, final InputStream stream) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setAsciiStream(positions.get(i), stream, stream.available());
			}
		} catch (IOException | SQLException ioex) {
			throwSetParamError(param, ioex);
		}
		return (Q) this;
	}

	public Q setAsciiStream(final int index, final InputStream stream, final int length) {
		initPrepared();
		try {
			preparedStatement.setAsciiStream(index, stream, length);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setAsciiStream(final String param, final InputStream stream, final int length) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setAsciiStream(positions.get(i), stream, length);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	public Q setCharacterStream(final int index, final Reader reader, final int length) {
		initPrepared();
		try {
			preparedStatement.setCharacterStream(index, reader, length);
		} catch (SQLException sex) {
			throwSetParamError(index, sex);
		}
		return (Q) this;
	}

	public Q setCharacterStream(final String param, final Reader reader, final int length) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setCharacterStream(positions.get(i), reader, length);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- bean

	/**
	 * Sets bean parameters from bean. Non-existing bean properties are ignored.
	 */
	public Q setBean(String beanName, final Object bean) {
		if (bean == null) {
			return (Q) this;
		}
		init();
		beanName += '.';
		Iterator it = query.iterateNamedParameters();

		while (it.hasNext()) {
			String paramName = (String) it.next();

			if (paramName.startsWith(beanName)) {
				String propertyName = paramName.substring(beanName.length());

				if (BeanUtil.declared.hasRootProperty(bean, propertyName)) {
					Object value = BeanUtil.declared.getProperty(bean, propertyName);
					setObject(paramName, value);
				}
			}
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- map

	/**
	 * Sets properties from the map.
	 */
	public Q setMap(final Map parameters) {
		if (parameters == null) {
			return (Q) this;
		}
		init();
		Iterator it = query.iterateNamedParameters();
		while (it.hasNext()) {
			String paramName = (String) it.next();
			setObject(paramName, parameters.get(paramName));
		}
		return (Q) this;
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
		return (Q) this;
	}

	/**
	 * Sets the value of the designated parameter with the given object.
	 * This method is like the method <code>setObject</code>
	 * above, except that it assumes a scale of zero.
	 */
	public Q setObject(final String param, final Object object, final int targetSqlType) {
		initPrepared();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				preparedStatement.setObject(positions.get(i), object, targetSqlType);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
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
	public Q setObject(final int index, final Object value) {
		setObject(index, value, null, Types.OTHER);
		return (Q) this;
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
			return (Q) this;
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
		return (Q) this;
	}

	/**
	 * @see #setObject(String, Object, Class, int)
	 */
	public Q setObject(final String param, final Object value) {
		setObject(param, value, null, Types.OTHER);
		return (Q) this;
	}

	/**
	 * @see #setObject(String, Object, Class, int) 
	 */
	public Q setObject(final String param, final Object value, final Class<? extends SqlType> sqlTypeClass, final int dbSqlType) {
		init();
		IntArrayList positions = query.getNamedParameterIndices(param);
		for (int i = 0; i < positions.size(); i++) {
			setObject(positions.get(i), value, sqlTypeClass, dbSqlType);
		}
		return (Q) this;
	}


	// ---------------------------------------------------------------- set object shortcuts

	/**
	 * Sets an array of objects parameters in given order.
	 */
	public Q setObjects(final Object... objects) {
		int index = 1;
		for (Object object : objects) {
			setObject(index++, object);
		}
		return (Q) this;
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
		return (Q) this;
	}

	// ---------------------------------------------------------------- batch

	/**
	 * Sets batch parameters with given array of values.
	 */
	public Q setBatch(final String name, final int[] array, int startingIndex) {
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
		return (Q) this;
	}
	/**
	 * Sets batch parameters with given array of values.
	 */
	public Q setBatch(final String name, final long[] array, int startingIndex) {
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
		return (Q) this;
	}

	/**
	 * Sets batch parameters with given array of values.
	 */
	public Q setBatch(final String name, final Object[] array, int startingIndex) {
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
		return (Q) this;
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
		return (Q) this;
	}
	private Q registerOutParameter(final String param, final int type) {
		initCallable();
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			for (int i = 0; i < positions.size(); i++) {
				callableStatement.registerOutParameter(positions.get(i), type);
			}
		} catch (SQLException sex) {
			throwSetParamError(param, sex);
		}
		return (Q) this;
	}

	// ---------------------------------------------------------------- close

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Q autoClose() {
		super.autoClose();
		return (Q) this;
	}

}
