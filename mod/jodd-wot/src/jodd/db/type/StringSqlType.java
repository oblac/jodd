// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class StringSqlType extends SqlType<String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String get(ResultSet rs, int index) throws SQLException {
		return rs.getString(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, String value) throws SQLException {
		st.setString(index, value);
	}

}
