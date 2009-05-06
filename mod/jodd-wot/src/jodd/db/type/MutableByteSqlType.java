// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.mutable.MutableByte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class MutableByteSqlType extends SqlType<MutableByte> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MutableByte get(ResultSet rs, int index) throws SQLException {
		return new MutableByte(rs.getByte(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, MutableByte value) throws SQLException {
		st.setByte(index, value.byteValue());
	}

}
