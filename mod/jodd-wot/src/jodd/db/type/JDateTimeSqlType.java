// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.datetime.JDateTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * JDateTime sql type stores JDateTime data as number of milliseconds passed from 1970.
 */
public class JDateTimeSqlType extends SqlType<JDateTime> {

	@Override
	public void set(PreparedStatement st, int index, JDateTime value, int dbSqlType) throws SQLException {
		if (value == null) {
			st.setNull(index, dbSqlType);
			return;
		}
		if (dbSqlType == Types.TIMESTAMP) {
			st.setTimestamp(index, value.convertToSqlTimestamp());
			return;
		}
		st.setLong(index, value.getTimeInMillis());
	}

	@Override
	public JDateTime get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		if (dbSqlType == Types.TIMESTAMP) {
			Timestamp timestamp = rs.getTimestamp(index);
			if (timestamp == null) {
				return null;
			}
			return new JDateTime(timestamp);
		}
		long time = rs.getLong(index);

		if (time == 0 && rs.wasNull()) {
			return null;
		}
		return new JDateTime(time);
	}
}
