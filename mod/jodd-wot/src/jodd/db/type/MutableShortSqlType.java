// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableShort;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class MutableShortSqlType extends SqlType<MutableShort> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableShort get(ResultSet rs, int index) throws SQLException {
		return new MutableShort(rs.getShort(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableShort value) throws SQLException {
		st.setShort(index, value.shortValue());
	}

}