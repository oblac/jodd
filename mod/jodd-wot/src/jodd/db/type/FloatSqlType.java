// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class FloatSqlType extends NullAwareSqlType<Float> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return Float.valueOf(rs.getFloat(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Float value, int dbSqlType) throws SQLException {
		st.setFloat(index, value.floatValue());
	}

}