// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.type.SqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class FooWeigthSqlType extends SqlType<FooWeight> {

	@Override
	public void set(PreparedStatement st, int index, FooWeight value, int dbSqlType) throws SQLException {
		st.setInt(index, value.getValue());
	}

	@Override
	public FooWeight get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return FooWeight.valueOf(rs.getInt(index));
	}
}
