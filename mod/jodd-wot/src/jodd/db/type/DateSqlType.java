// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Date;

public class DateSqlType extends SqlType<Date> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date get(ResultSet rs, int index) throws SQLException {
		return rs.getDate(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Date value) throws SQLException {
		st.setTimestamp(index, new Timestamp(value.getTime()));
	}

}