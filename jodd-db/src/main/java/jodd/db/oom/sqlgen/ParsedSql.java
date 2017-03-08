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

package jodd.db.oom.sqlgen;

import jodd.db.oom.ColumnData;
import jodd.db.oom.DbSqlGenerator;

import java.util.Map;

/**
 * Simple holder of parsed SQL data. It occupies less memory and may be cached.
 * @see DbSqlBuilder#parse()
 */
public class ParsedSql implements DbSqlGenerator {

	protected final String generatedQuery;
	protected final Map<String, ParameterValue> queryParameters;
	protected final Map<String, ColumnData> columnData;
	protected final String[] joinHints;

	public ParsedSql(DbSqlGenerator dbSqlGenerator) {
		generatedQuery = dbSqlGenerator.generateQuery();
		queryParameters = dbSqlGenerator.getQueryParameters();
		columnData = dbSqlGenerator.getColumnData();
		joinHints = dbSqlGenerator.getJoinHints();
	}

	public String generateQuery() {
		return generatedQuery;
	}

	public Map<String, ParameterValue> getQueryParameters() {
		return queryParameters;
	}

	public Map<String, ColumnData> getColumnData() {
		return columnData;
	}

	public String[] getJoinHints() {
		return joinHints;
	}

}