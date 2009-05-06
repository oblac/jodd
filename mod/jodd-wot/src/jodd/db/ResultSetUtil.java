// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Various result set utilities.
 */
public class ResultSetUtil {

	/**
	 * Closes provided result set without throwing an exception.
	 */
	public static void close(ResultSet resultSet) {
		if (resultSet == null) {
			return;
		}
		try {
			resultSet.close();
		} catch (SQLException sex) {
			// ignore
		}
	}



	/**
	 * Returns long value of very first column in result set.
	 */
	public static long getFirstLong(ResultSet resultSet) throws SQLException {
		if (resultSet.next() == true) {
			return resultSet.getLong(1);
		}
		return -1;
	}

	/**
	 * Returns int value of very first column in result set.
	 */
	public static int getFirstInt(ResultSet resultSet) throws SQLException {
		if (resultSet.next() == true) {
			return resultSet.getInt(1);
		}
		return -1;
	}

	// ---------------------------------------------------------------- mapper

	/**
	 * Invokes {@link jodd.db.RowMapper} for each row of the result set.
	 */
	public void iterate(ResultSet rs, RowMapper mapper) throws SQLException {
		int count = 0;
		while (rs.next()) {
			mapper.mapRow(rs, ++count);
		}
	}
}
