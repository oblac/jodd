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
	protected String buildPageSql(String sqlNoSelect, int from, int pageSize) {
		return "select LIMIT " + from + ' ' + pageSize + sqlNoSelect;
	}

	/**
	 * Builds count sql using COUNT(1).
	 */
	@Override
	protected String buildCountSql(String sqlNoSelect) {
		return "select count(1) " + sqlNoSelect;
	}
}