// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Array;

public class SqlArraySqlType extends SqlType<Array> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Array get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return rs.getArray(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Array value, int dbSqlType) throws SQLException {
		st.setArray(index, value);
	}

}