// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class IntegerSqlType extends SqlType<Integer> {

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
	public void set(PreparedStatement st, int index, Integer value) throws SQLException {
		st.setInt(index, value.intValue());
	}
}
