// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class URLSqlType extends SqlType<URL> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL get(ResultSet rs, int index) throws SQLException {
		return rs.getURL(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, URL value) throws SQLException {
		st.setURL(index, value);
	}

}
