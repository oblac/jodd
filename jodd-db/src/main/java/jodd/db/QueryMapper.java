// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Process {@link jodd.db.DbQuery query} and map it execution result to a type.
 */
public interface QueryMapper<T> {

	/**
	 * Processes <b>single</b> result sets row.
	 * Returns <code>null</code> to stop iterations.
	 */
	T process(ResultSet resultSet) throws SQLException;

}