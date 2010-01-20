// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ShortSqlType extends SqlType<Short> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Short get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return Short.valueOf(rs.getShort(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Short value) throws SQLException {
		st.setShort(index, value.shortValue());
	}

}