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

package jodd.joy.db;

import jodd.db.JoddDb;
import jodd.db.oom.DbOomException;
import jodd.db.oom.DbOomQuery;
import jodd.db.oom.DbSqlGenerator;
import jodd.db.oom.sqlgen.ParsedSql;
import jodd.db.querymap.QueryMap;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.proxetta.ProxyTargetInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;
import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

/**
 * Builds and populates queries automatically for target method.
 * Optionally override this class and provide static helper methods
 * for convenient usage.
 */
public class DbQueryBuilder {

	protected final QueryMap queryMap;
	protected final Map<String, ParsedSql> parsedSqlMap;
	protected final Map<String, String[]> methodParamNames;

	public DbQueryBuilder() {
		queryMap = JoddDb.runtime().queryMap();
		parsedSqlMap = new HashMap<>();
		methodParamNames = new HashMap<>();
	}

	/**
	 * Prepares <code>DbQuery</code>.
	 */
	public DbOomQuery createAndPopulateDbQuery(ProxyTargetInfo proxyTargetInfo, String query) {

		if (query == null) {
			query = resolveQuery(proxyTargetInfo);

			if (query == null) {
				throw new DbOomException("Query not resolved.");
			}
		}

		// sql generator

		DbSqlGenerator dbSqlGenerator = parsedSqlMap.get(query);

		if (dbSqlGenerator == null) {
			ParsedSql parsedSql = sql(query).parse();

			parsedSqlMap.put(query, parsedSql);

			dbSqlGenerator = parsedSql;
		}

		// db oom query

		DbOomQuery dbOomQuery = query(dbSqlGenerator);

		// parameter names

		String keyName = proxyTargetInfo.targetClass.getName() + "." + proxyTargetInfo.targetMethodName;

		String[] paramNames = methodParamNames.get(keyName);

		if (paramNames == null) {
			paramNames = resolveMethodParameterNames(proxyTargetInfo);
			methodParamNames.put(keyName, paramNames);
		}

		// set SQL parameters

		for (int i = 0; i < paramNames.length; i++) {
			String paramName = paramNames[i];

			dbOomQuery.setObject(paramName, proxyTargetInfo.arguments[i]);
		}

		return dbOomQuery;
	}

	/**
	 * Resolves method parameter names.
	 */
	protected String[] resolveMethodParameterNames(ProxyTargetInfo proxyTargetInfo) {
		String[] paramNames;Method method;

		try {
			method = proxyTargetInfo.targetClass.getDeclaredMethod(
				proxyTargetInfo.targetMethodName,
				proxyTargetInfo.argumentsClasses
			);
		} catch (NoSuchMethodException ex) {
			throw new DbOomException(ex);
		}

		MethodParameter[] methodParameters = Paramo.resolveParameters(method);

		paramNames = new String[methodParameters.length];

		for (int i = 0; i < methodParameters.length; i++) {
			MethodParameter methodParameter = methodParameters[i];

			paramNames[i] = methodParameter.getName();
		}
		return paramNames;
	}

	/**
	 * Returns query from target info.
	 * Returns <code>null</code> if query is not found.
	 */
	protected String resolveQuery(ProxyTargetInfo proxyTargetInfo) {
		String keyName = proxyTargetInfo.targetClass.getName() + "." + proxyTargetInfo.targetMethodName;

		String query = queryMap.getQuery(keyName);

		if (query != null) {
			return query;
		}

		keyName = proxyTargetInfo.targetClass.getSimpleName() + "." + proxyTargetInfo.targetMethodName;

		query = queryMap.getQuery(keyName);

		if (query != null) {
			return query;
		}

		keyName = proxyTargetInfo.targetMethodName;

		return queryMap.getQuery(keyName);
	}

}