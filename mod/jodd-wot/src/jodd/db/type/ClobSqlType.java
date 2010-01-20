// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Clob;

public class ClobSqlType extends SqlType<Clob> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Clob get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return rs.getClob(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Clob value, int dbSqlType) throws SQLException {
		st.setClob(index, value);
	}

}