// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page.db;

import jodd.joy.page.DbPager;

/**
 * MySql database pager.
 */
public class MySqlPager extends DbPager {

	/**
	 * Appends ORDER BY keyword.
	 */
	@Override
	protected String buildOrderSql(String sql, String column, boolean ascending) {
		sql += " order by " + column;
		if (!ascending) {
			sql += " desc";
		}
		return sql;
	}

	/**
	 * Builds page SQL using <code>limit</code> keyword.
	 * Uses SQL_CALC_FOUND_ROWS for {@link #buildCountSql(String)}.
	 */
	@Override
	protected String buildPageSql(String sql, int from, int pageSize) {
		sql = removeSelect(sql);
		return "select SQL_CALC_FOUND_ROWS " + sql + " limit " + from + ", " + pageSize;
	}

	/**
	 * Returns FOUND_ROWS() sql to determine total count of founded rows.
	 */
	@Override
	protected String buildCountSql(String sql) {
		return "SELECT FOUND_ROWS()";
	}

}