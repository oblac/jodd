// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class BigDecimalSqlType extends SqlType<BigDecimal> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal get(ResultSet rs, int index) throws SQLException {
		return rs.getBigDecimal(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, BigDecimal value) throws SQLException {
		st.setBigDecimal(index, value);
	}

}
