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

	private Class sqlType;

	protected SqlType() {
		this.sqlType = ReflectUtil.getGenericSupertype(this.getClass(), 0);
	}

	/**
	 * Returns sql type for current implementation.
	 */
	public final Class getSqlType() {
		return sqlType;
	}

	/**
	 * Sets prepared statement value.
	 */
	public abstract void set(PreparedStatement st, int index, T value) throws SQLException;

	/**
	 * Returns value from result set.
	 */
	public abstract T get(ResultSet rs, int index) throws SQLException;
}