// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableDouble;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class MutableDoubleSqlType extends SqlType<MutableDouble> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableDouble get(ResultSet rs, int index) throws SQLException {
		return new MutableDouble(rs.getDouble(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableDouble value) throws SQLException {
		st.setDouble(index, value.doubleValue());
	}
	
}
