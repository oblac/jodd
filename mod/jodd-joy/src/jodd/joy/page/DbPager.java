// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

import jodd.db.oom.DbOomQuery;
import jodd.db.oom.sqlgen.DbSqlBuilder;

import java.util.List;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;
import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

/**
 * Database pager. Provides
 */
public class DbPager {

	private static final String SELECT = "select";

	/**
	 * Performs the pagination.
	 */
	public <T> PageData<T> page(PageRequest pageRequest, String sql, Map params, Class... target) {
		PageData<T> pageData = page(sql, pageRequest.getPage(), pageRequest.getSize(), params, target);

		if (pageData.getItems().isEmpty() && pageData.currentPage != 0) {
			if (pageData.currentPage != pageRequest.getPage()) {
				// out of bounds
				int newPage = pageData.getCurrentPage();
				pageData = page(sql, newPage, pageRequest.getSize(), params, target);
			}
		}
		return pageData;
	}

	/**
	 * Pages given page. No fix in case of out-of-bounds.
	 */
	public <T> PageData<T> page(String sql, int page, int pageSize, Map params, Class... target) {
		int ndx = sql.indexOf(SELECT);
		if (ndx != -1) {
			sql = sql.substring(ndx + SELECT.length());
		}

		int from = (page - 1) * pageSize;

		DbSqlBuilder dbsql = sql(buildPageSql(sql, from, pageSize));

		DbOomQuery query = query(dbsql);
		query.setMaxRows(pageSize);
		query.setFetchSize(pageSize);
		query.setMap(params);

		List<T> list = query.listAndClose(pageSize, target);

		long count = list.size();

		return new PageData<T>(page, (int) count, pageSize, list);
	}

	/**
	 * Builds page SQL string. Given sql string has removed the 'select' keyword.
	 * Returned SQL string may return more than <code>pageSize</code> elements,
	 * but only <code>pageSize</code> will be parsed.
	 */
	protected String buildPageSql(String sqlNoSelect, int from, int pageSize) {
		return "select " + sqlNoSelect + " limit " + from + ", " + pageSize;
	}

}