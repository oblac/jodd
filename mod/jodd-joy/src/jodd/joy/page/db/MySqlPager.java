// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page.db;

import jodd.joy.page.DbPager;

/**
 * MySql database pager.
 */
public class MySqlPager extends DbPager {

	/**
	 * Builds MySql page SQL using <code>limit</code> keyword.
	 * Note that SQL_CALC_FOUND_ROWS is not used as we do parse
	 * returned rows (up to <code>pageSize</code>) so the FOUND_ROWS()
	 * is known.
	 */
	@Override
	protected String buildPageSql(String sqlNoSelect, int from, int pageSize) {
		return "select " + sqlNoSelect + " limit " + from + ", " + pageSize;
	}

}