// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableInteger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class MutableIntegerSqlType extends SqlType<MutableInteger> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableInteger get(ResultSet rs, int index) throws SQLException {
		return new MutableInteger(rs.getInt(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableInteger value) throws SQLException {
		st.setInt(index, value.intValue());
	}
}
