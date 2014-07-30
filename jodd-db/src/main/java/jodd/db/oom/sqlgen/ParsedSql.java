// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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