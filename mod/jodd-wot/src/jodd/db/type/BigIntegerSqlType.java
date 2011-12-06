// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class BigIntegerSqlType extends SqlType<BigInteger> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigInteger get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		return BigInteger.valueOf(rs.getLong(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, BigInteger value, int dbSqlType) throws SQLException {
		st.setLong(index, value.longValue());
	}

}