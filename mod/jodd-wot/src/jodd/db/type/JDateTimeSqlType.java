// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.datetime.JDateTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * JDateTime sql type stores JDateTime data as number of milliseconds passed from 1970.
 */
public class JDateTimeSqlType extends SqlType<JDateTime> {

	@Override
	public void set(PreparedStatement st, int index, JDateTime value) throws SQLException {
		st.setLong(index, value.getTimeInMillis());
	}

	@Override
	public JDateTime get(ResultSet rs, int index) throws SQLException {
		return new JDateTime(rs.getLong(index));
	}
}
