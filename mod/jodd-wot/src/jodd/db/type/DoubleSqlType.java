// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DoubleSqlType extends SqlType<Double> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return Double.valueOf(rs.getDouble(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Double value) throws SQLException {
		st.setDouble(index, value.doubleValue());
	}
}
