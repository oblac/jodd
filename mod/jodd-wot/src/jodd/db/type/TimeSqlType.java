// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Time;

public class TimeSqlType extends SqlType<Time> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Time get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return rs.getTime(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Time value) throws SQLException {
		st.setTime(index, value);
	}

}