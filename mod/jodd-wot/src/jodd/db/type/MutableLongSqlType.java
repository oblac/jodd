// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableLong;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class MutableLongSqlType extends SqlType<MutableLong> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableLong get(ResultSet rs, int index) throws SQLException {
		return new MutableLong(rs.getLong(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableLong value) throws SQLException {
		st.setLong(index, value.longValue());
	}

}