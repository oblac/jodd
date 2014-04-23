// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.querymap;

/**
 * Storage of SQL queries.
 */
public interface QueryMap {

	/**
	 * (Re)loads the query map.
	 */
	public void load();

	/**
	 * Returns query for given key.
	 */
	public String getQuery(String key);

}