// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.sqlmap;

/**
 * Storage of SQL queries.
 */
public interface SqlMap {

	/**
	 * (Re)loads the SQL map.
	 */
	public void load();

	/**
	 * Returns SQL query for given key.
	 */
	public String getQuery(String key);

}