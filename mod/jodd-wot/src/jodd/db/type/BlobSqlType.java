// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Blob;

public class BlobSqlType extends SqlType<Blob> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Blob get(ResultSet rs, int index) throws SQLException {
		return rs.getBlob(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Blob value) throws SQLException {
		st.setBlob(index, value);
	}

}