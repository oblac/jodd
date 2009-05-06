// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import java.util.Map;

/**
 * Generates SQL queries.
 */
public interface DbSqlGenerator {

	/**
	 * Generates SQL query.
	 */
	String generateQuery();

	/**
	 * Returns a map of SQL parameters used by generated query.
	 * Must be invoked only <b>after</b> the {@link #generateQuery()}.
	 * May be <code>null</code>
	 */
	Map<String, Object> getQueryParameters();

	/**
	 * Returns an optional map of table and column names, used by {@link jodd.db.orm.mapper.ResultSetMapper}.
	 * May be <code>null</code>.
	 */
	Map<String, ColumnData> getColumnData();

	/**
	 * Returns join hints. May be <code>null</code>. 
	 */
	String[] getJoinHints();
}
