// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableFloat;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class MutableFloatSqlType extends SqlType<MutableFloat> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableFloat get(ResultSet rs, int index) throws SQLException {
		return new MutableFloat(rs.getFloat(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableFloat value) throws SQLException {
		st.setFloat(index, value.floatValue());
	}

}