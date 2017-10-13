// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
	 * Returns default page request when passed one is <code>null</code>.
	 * This usually happens on initial page view, when no page request is created.
	 * Returned <code>PageRequest</code> defines <i>global</i> defaults.
	 */
	protected PageRequest getDefaultPageRequest() {
		return new PageRequest();
	}

	/**
	 * Performs the pagination with given {@link jodd.joy.page.PageRequest}.
	 *
	 * @param pageRequest page request, may be <code>null</code>, then the {@link #getDefaultPageRequest() default page request} will be used
	 * @param sql SQL query that lists <b>all</b> items
	 * @param params SQL query parameters or <code>null</code>
	 * @param sortColumns array of all column names
	 * @param target db entities for mapping (as usual in DbOom)
	 *
	 * @see #page(String, java.util.Map, int, int, String, boolean, Class[])
	 */
	public <T> PageData<T> page(PageRequest pageRequest, String sql, Map params, String[] sortColumns, Class[] target) {
		if (pageRequest == null) {
			pageRequest = getDefaultPageRequest();
		}

		// check sort

		String sortColumName = null;
		boolean ascending = true;

		int sort = pageRequest.getSort();
		if (sort != 0) {
			ascending = sort > 0;
			if (!ascending) {
				sort = -sort;
			}
			int index = sort - 1;

			if (index >= sortColumns.length) {
				index = 1;
			}
			sortColumName = sortColumns[index];
		}

		// page

		int page = pageRequest.getPage();
		int pageSize = pageRequest.getSize();

		PageData<T> pageData = page(sql, params, page, pageSize, sortColumName, ascending, target);

		// fix the out-of-bounds

		if (pageData.getItems().isEmpty() && pageData.currentPage != 0) {
			if (pageData.currentPage != page) {
				// out of bounds
				int newPage = pageData.getCurrentPage();
				pageData = page(sql, params, newPage, pageSize, sortColumName, ascending, target);
			}
		}

		return pageData;
	}

	/**
	 * Pages given page.
	 *
	 * @param sql sql query that lists <b>all</b> items
	 * @param params map of SQL parameters
	 * @param page current page to show
	 * @param pageSize number of items to show
	 * @param sortColumnName name of sorting column, <code>null</code> for no sorting
	 * @param ascending <code>true</code> for ascending order
	 * @param target db entities for mapping (sa usual in DbOom)
	 */
	protected <T> PageData<T> page(String sql, Map params, int page, int pageSize, String sortColumnName, boolean ascending, Class[] target) {
		if (sortColumnName != null) {
			sql = buildOrderSql(sql, sortColumnName, ascending);
		}

		int from = (page - 1) * pageSize;

		String pageSql = buildPageSql(sql, from, pageSize);
		DbSqlBuilder dbsql = sql(pageSql);

		DbOomQuery query = query(dbsql);
		query.setMaxRows(pageSize);
		query.setFetchSize(pageSize);
		query.setMap(params);

		List<T> list = query.list(pageSize, target);
		query.close();

		String countSql = buildCountSql(sql);
		dbsql = sql(countSql);
		query = query(dbsql);
		query.setMap(params);
		long count = query.executeCount();
		query.close();

		return new PageData<>(page, (int) count, pageSize, list);
	}

	// ---------------------------------------------------------------- abstract

	/**
	 * Builds order SQL string.
	 * Invoked before all other SQL modification.
	 */
	protected abstract String buildOrderSql(String sql, String column, boolean ascending);

	/**
	 * Builds page SQL string.
	 * Returned SQL string may return more than <code>pageSize</code> elements,
	 * but only <code>pageSize</code> will be consumed.
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
	 * Removes the first part of the sql up to the relevant 'from'.
	 * Tries to detect sub-queries in the 'select' part.
	 */
	protected String removeToFrom(String sql) {
		int from = 0;
		int fromCount = 1;
		int selectCount = 0;
		int lastNdx = 0;
		while (true) {
			int ndx = StringUtil.indexOfIgnoreCase(sql, "from", from);
			if (ndx == -1) {
				break;
			}

			// count selects in left part
			String left = sql.substring(lastNdx, ndx);
			selectCount += StringUtil.countIgnoreCase(left, "select");

			if (fromCount >= selectCount) {
				sql = sql.substring(ndx);
				break;
			}

			// find next 'from'
			lastNdx = ndx;
			from = ndx + 4;
			fromCount++;
		}
		return sql;
	}

	/**
	 * Removes everything from last "order by".
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