// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Null-aware {@link SqlType sql types}.
 */
public abstract class NullAwareSqlType<T> extends SqlType<T> {

	/**
	 * Detects if there was a <code>null</code> reading, never less the real value.
	 */
	@Override
	public <E> E readValue(ResultSet rs, int index, Class<E> destinationType, int dbSqlType) throws SQLException {
		T t = get(rs, index, dbSqlType);
		if ((t == null) || (rs.wasNull())) {
			return null;
		}
		return prepareGetValue(t, destinationType);
	}

	/**
	 * Detects <code>null</code> while storing value into the database.
	 */
	@Override
	public void storeValue(PreparedStatement st, int index, Object value, int dbSqlType) throws SQLException {
		if (value == null) {
			st.setNull(index, dbSqlType);
			return;
		}
		super.storeValue(st, index, value, dbSqlType);
	}

}
