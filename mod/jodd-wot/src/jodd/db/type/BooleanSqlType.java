// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class BooleanSqlType extends NullAwareSqlType<Boolean> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return Boolean.valueOf(rs.getBoolean(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Boolean value, int dbSqlType) throws SQLException {
		st.setBoolean(index, value.booleanValue());
	}

}
