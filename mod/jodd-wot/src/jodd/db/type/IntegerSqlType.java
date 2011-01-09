// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class IntegerSqlType extends NullAwareSqlType<Integer> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return Integer.valueOf(rs.getInt(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Integer value, int dbSqlType) throws SQLException {
		st.setInt(index, value.intValue());
	}
}
