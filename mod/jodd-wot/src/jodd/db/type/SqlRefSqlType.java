// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Ref;

public class SqlRefSqlType extends SqlType<Ref> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Ref get(ResultSet rs, int index) throws SQLException {
		return rs.getRef(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Ref value) throws SQLException {
		st.setRef(index, value);
	}
}
