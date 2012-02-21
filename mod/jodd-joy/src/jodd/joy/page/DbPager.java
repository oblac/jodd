// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

import jodd.db.oom.DbOomQuery;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.util.StringUtil;

import java.util.List;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;
import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

/**
 * Database pager.
 */
public abstract class DbPager {

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
		int from = (page - 1) * pageSize;

		DbSqlBuilder dbsql = sql(buildPageSql(sql, from, pageSize));

		DbOomQuery query = query(dbsql);
		query.setMaxRows(pageSize);
		query.setFetchSize(pageSize);
		query.setMap(params);

		List<T> list = query.listAndClose(pageSize, target);

		dbsql = sql(buildCountSql(sql));
		long count = query(dbsql).executeCountAndClose();

		return new PageData<T>(page, (int) count, pageSize, list);
	}

	/**
	 * Builds page SQL string.
	 * Returned SQL string may return more than <code>pageSize</code> elements,
	 * but only <code>pageSize</code> will be parsed.
	 */
	protected abstract String buildPageSql(String sql, int from, int pageSize);

	/**
	 * Builds SQL for retrieving total number of results.
	 */
	protected abstract String buildCountSql(String sql);

	// ---------------------------------------------------------------- sql manipulation

	/**
	 * Removes the first 'select' from the sql query.
	 */
	protected String removeSelect(String sql) {
		int ndx = StringUtil.indexOfIgnoreCase(sql, "select");
		if (ndx != -1) {
			sql = sql.substring(ndx + 6);	// select.length()
		}
		return sql;
	}

	/**
	 * Removes the first part of the sql up to the 'from'.
	 */
	protected String removeToFrom(String sql) {
		int ndx = StringUtil.indexOfIgnoreCase(sql, "from");
		if (ndx != -1) {
			sql = sql.substring(ndx);
		}
		return sql;
	}

	/**
	 * Removes everything from last last order by.
	 */
	protected String removeLastOrderBy(String sql) {
		int ndx = StringUtil.lastIndexOfIgnoreCase(sql, "order by");
		if (ndx != -1) {
			int ndx2 = sql.lastIndexOf(sql, ')');
			if (ndx > ndx2) {
				sql = sql.substring(0, ndx);
			}
		}
		return sql;
	}

}