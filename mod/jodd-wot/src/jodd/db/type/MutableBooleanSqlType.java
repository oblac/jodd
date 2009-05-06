// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableBoolean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class MutableBooleanSqlType extends SqlType<MutableBoolean> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableBoolean get(ResultSet rs, int index) throws SQLException {
		return new MutableBoolean(rs.getBoolean(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableBoolean value) throws SQLException {
		st.setBoolean(index, value.getValue());
	}

}
