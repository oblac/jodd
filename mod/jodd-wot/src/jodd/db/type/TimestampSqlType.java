// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class TimestampSqlType extends SqlType<Timestamp> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return rs.getTimestamp(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Timestamp value) throws SQLException {
		st.setTimestamp(index, value);
	}

}
