// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Date;

public class SqlDateSqlType extends SqlType<Date>{

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
		st.setDate(index, value);
	}

}
