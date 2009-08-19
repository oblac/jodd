// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.type.SqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class BooSqlType extends SqlType<Boo> {

	@Override
	public void set(PreparedStatement st, int index, Boo value) throws SQLException {
		st.setInt(index, value.value);
	}

	@Override
	public Boo get(ResultSet rs, int index) throws SQLException {
		Boo boo = new Boo();
		boo.value = rs.getInt(index);
		return boo;
	}
}
