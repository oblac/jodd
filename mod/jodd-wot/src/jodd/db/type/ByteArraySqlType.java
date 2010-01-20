// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ByteArraySqlType extends SqlType<byte[]> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return rs.getBytes(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, byte[] value) throws SQLException {
		st.setBytes(index, value);
	}

}
