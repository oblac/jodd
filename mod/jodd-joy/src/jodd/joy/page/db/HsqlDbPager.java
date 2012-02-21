// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page.db;

import jodd.joy.page.DbPager;

/**
 * HSQLDB database pager.
 */
public class HsqlDbPager extends DbPager {

	/**
	 * Builds page sql using LIMIT keyword after the SELECT.
	 */
	@Override
	protected String buildPageSql(String sql, int from, int pageSize) {
		sql = removeSelect(sql);
		return "select LIMIT " + from + ' ' + pageSize + sql;
	}

	/**
	 * Builds count sql using COUNT(1).
	 */
	@Override
	protected String buildCountSql(String sql) {
		sql = removeToFrom(sql);
		sql = removeLastOrderBy(sql);
		return "select count(1) " + sql;
	}
}