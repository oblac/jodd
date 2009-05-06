// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class LongSqlType extends SqlType<Long> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long get(ResultSet rs, int index) throws SQLException {
		return Long.valueOf(rs.getLong(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Long value) throws SQLException {
		st.setLong(index, value.longValue());
	}

}
