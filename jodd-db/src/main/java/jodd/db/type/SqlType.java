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

package jodd.db.type;

import jodd.typeconverter.TypeConverterManager;
import jodd.util.ClassUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL type.
 */
public abstract class SqlType<T> {

	/**
	 * Indicator for not yet resolved DB SQL type.
	 */
	public static final int DB_SQLTYPE_UNKNOWN = Integer.MAX_VALUE;

	/**
	 * Indicator for unavailable DB SQL type. Used usually when sql type is not
	 * available by JDBC meta data or when column can not be matched
	 * (due to case-mismatching).
	 */
	public static final int DB_SQLTYPE_NOT_AVAILABLE = Integer.MIN_VALUE;


	protected Class<T> sqlType;

	@SuppressWarnings({"unchecked"})
	protected SqlType() {
		this.sqlType = ClassUtil.getGenericSupertype(this.getClass(), 0);
	}

	/**
	 * Sets prepared statement value.
	 */
	public abstract void set(PreparedStatement st, int index, T value, int dbSqlType) throws SQLException;

	/**
	 * Returns value from result set.
	 * @param rs result set
	 * @param index column index
	 * @param dbSqlType java.sql.Types hint
	 */
	public abstract T get(ResultSet rs, int index, int dbSqlType) throws SQLException;


	/**
	 * Stores value in database. Value is casted to sql type.
	 */
	public void storeValue(final PreparedStatement st, final int index, final Object value, final int dbSqlType) throws SQLException {
		T t = TypeConverterManager.get().convertType(value, sqlType);
		set(st, index, t, dbSqlType);
	}

	/**
	 * Reads value from database. Value is casted to destination type.
	 * @param rs result set
	 * @param index database column index
	 * @param destinationType property type
	 * @param dbSqlType hint for column sql type value
	 */
	public <E> E readValue(final ResultSet rs, final int index, final Class<E> destinationType, final int dbSqlType) throws SQLException {
		T t = get(rs, index, dbSqlType);
		return prepareGetValue(t, destinationType);
	}

	/**
	 * Once when value is read from result set, prepare it to match destination type.
	 * @param t get value
	 * @param destinationType destination type
	 */
	@SuppressWarnings({"unchecked"})
	protected <E> E prepareGetValue(final T t, final Class<E> destinationType) {
		if (t == null) {
			return null;
		}
		if (destinationType == null) {
			return (E) t;
		}
		return TypeConverterManager.get().convertType(t, destinationType);
	}

}