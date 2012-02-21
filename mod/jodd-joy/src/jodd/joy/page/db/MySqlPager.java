// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page.db;

import jodd.joy.page.DbPager;

/**
 * MySql database pager.
 */
public class MySqlPager extends DbPager {

	/**
	 * Builds page SQL using <code>limit</code> keyword.
	 * Uses SQL_CALC_FOUND_ROWS for {@link #buildCountSql(String)}.
	 */
	@Override
	protected String buildPageSql(String sqlNoSelect, int from, int pageSize) {
		return "select SQL_CALC_FOUND_ROWS " + sqlNoSelect + " limit " + from + ", " + pageSize;
	}

	/**
	 * Returns FOUND_ROWS() sql to determine total count of founded rows.
	 */
	@Override
	protected String buildCountSql(String sqlNoSelect) {
		return "SELECT FOUND_ROWS()";
	}

}