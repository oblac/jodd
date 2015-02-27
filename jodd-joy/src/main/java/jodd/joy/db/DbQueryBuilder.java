// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.DbManager;
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
		queryMap = DbManager.getInstance().getQueryMap();
		parsedSqlMap = new HashMap<String, ParsedSql>();
		methodParamNames = new HashMap<String, String[]>();
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