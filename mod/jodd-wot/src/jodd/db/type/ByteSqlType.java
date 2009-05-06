// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ByteSqlType extends SqlType<Byte> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte get(ResultSet rs, int index) throws SQLException {
		return Byte.valueOf(rs.getByte(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Byte value) throws SQLException {
		st.setByte(index, value.byteValue());
	}
}
