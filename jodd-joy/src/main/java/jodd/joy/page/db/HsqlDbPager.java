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

package jodd.joy.page.db;

import jodd.joy.page.DbPager;

/**
 * HSQLDB database pager.
 */
public class HsqlDbPager extends DbPager {

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
	 * Builds page sql using LIMIT keyword after the SELECT.
	 */
	@Override
	protected String buildPageSql(String sql, int from, int pageSize) {
		sql = removeSelect(sql);
		return "select LIMIT " + from + ' ' + pageSize + sql;
	}

	/**
	 * Builds count sql using COUNT(*).
	 */
	@Override
	protected String buildCountSql(String sql) {
		sql = removeToFrom(sql);
		sql = removeLastOrderBy(sql);
		return "select count(*) " + sql;
	}
}