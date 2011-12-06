// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

import jodd.db.oom.DbOomQuery;
import jodd.db.oom.sqlgen.DbSqlBuilder;

import java.util.List;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;
import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

public class MySqlPager {

	private static final String SELECT = "select";

	/**
	 * Performs the pagination.
	 */
	public static <T> PageData<T> page(PageRequest pageRequest, String sql, Map params, Class... target) {
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
	@SuppressWarnings({"unchecked"})
	public static <T> PageData<T> page(String sql, int page, int pageSize, Map params, Class... target) {
		int ndx = sql.indexOf(SELECT);
		if (ndx != -1) {
			sql = sql.substring(ndx + SELECT.length());
		}

		int from = (page - 1) * pageSize;
		DbSqlBuilder dbsql = sql("select SQL_CALC_FOUND_ROWS " + sql + " limit " + from + ", " + pageSize);
		DbOomQuery query = query(dbsql);
		query.setMaxRows(pageSize);
		query.setFetchSize(pageSize);
		query.setMap(params);
		List list = query.listAndClose(target);
		long count = query("SELECT FOUND_ROWS()").executeCountAndClose();

		return new PageData<T>(page, (int) count, pageSize, list);
	}

}
