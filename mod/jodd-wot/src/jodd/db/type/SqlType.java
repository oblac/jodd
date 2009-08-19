// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.util.ReflectUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * SQL type.
 */
public abstract class SqlType<T> {

	private Class<T> sqlType;

	@SuppressWarnings({"unchecked"})
	protected SqlType() {
		this.sqlType = ReflectUtil.getGenericSupertype(this.getClass(), 0);
	}

	/**
	 * Sets prepared statement value.
	 */
	public abstract void set(PreparedStatement st, int index, T value) throws SQLException;

	/**
	 * Returns value from result set.
	 */
	public abstract T get(ResultSet rs, int index) throws SQLException;

	/**
	 * Stores value in database. Value is casted to sql type.
	 */
	public void storeValue(PreparedStatement st, int index, Object value) throws SQLException {
		T t = ReflectUtil.castType(value, sqlType);
		set(st, index, t);
	}

	/**
	 * Reads value from database. Value is casted to destination type.
	 */
	@SuppressWarnings({"unchecked"})
	public <E> E readValue(ResultSet rs, int index, Class<E> destinationType) throws SQLException {
		T t = get(rs, index);
		if (destinationType == null) {
			return (E) t;
		}
		return ReflectUtil.castType(t, destinationType);
	}
}