// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.typeconverter.TypeConverterManager;
import jodd.util.ReflectUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * SQL type.
 */
public abstract class SqlType<T> {

	protected Class<T> sqlType;

	@SuppressWarnings({"unchecked"})
	protected SqlType() {
		this.sqlType = ReflectUtil.getGenericSupertype(this.getClass(), 0);
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
	public void storeValue(PreparedStatement st, int index, Object value, int dbSqlType) throws SQLException {
		T t = TypeConverterManager.castType(value, sqlType);
		set(st, index, t, dbSqlType);
	}

	/**
	 * Reads value from database. Value is casted to destination type.
	 * @param rs result set
	 * @param index database column index
	 * @param destinationType property type
	 * @param dbSqlType hint for column sql type value
	 */
	public <E> E readValue(ResultSet rs, int index, Class<E> destinationType, int dbSqlType) throws SQLException {
		T t = get(rs, index, dbSqlType);
		return prepareGetValue(t, destinationType);
	}

	/**
	 * Once when value is read from result set, prepare it to match destination type.
	 * @param t get value
	 * @param destinationType destination type
	 */
	@SuppressWarnings({"unchecked"})
	protected <E> E prepareGetValue(T t, Class<E> destinationType) {
		if (t == null) {
			return null;
		}
		if (destinationType == null) {
			return (E) t;
		}
		return TypeConverterManager.castType(t, destinationType);
	}

}