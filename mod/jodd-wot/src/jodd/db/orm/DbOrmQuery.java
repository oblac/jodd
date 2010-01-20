// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.orm.mapper.DefaultResultSetMapper;
import jodd.db.orm.mapper.ResultSetMapper;
import jodd.db.orm.sqlgen.ParameterValue;
import jodd.util.StringUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * A simple ORM extension for {@link DbQuery}.
 * <p>
 * ORM extension may map results to objects in two ways:
 * <ul>
 * <li><i>auto</i> mode - when result set is mapped to provided types, and</li>
 * <li><i>mapped</i> mode - requires explicit mapping definitions.</li>
 * </ul>
 *
 */
public class DbOrmQuery extends DbQuery {

	// ---------------------------------------------------------------- default ctors

	public DbOrmQuery(Connection conn, String sqlString) {
		super(conn, sqlString);
	}
	public static DbOrmQuery query(Connection conn, String sqlString) {
		return new DbOrmQuery(conn, sqlString);
	}


	public DbOrmQuery(DbSession session, String sqlString) {
		super(session, sqlString);
	}
	public static DbOrmQuery query(DbSession session, String sqlString) {
		return new DbOrmQuery(session, sqlString);
	}


	public DbOrmQuery(String sqlString) {
		super(sqlString);
	}
	public static DbOrmQuery query(String sqlString) {
		return new DbOrmQuery(sqlString);
	}

	// ---------------------------------------------------------------- sqlgen ctors

	protected DbSqlGenerator sqlgen;

	public DbOrmQuery(Connection conn, DbSqlGenerator sqlgen) {
		super(conn, sqlgen.generateQuery());
		this.sqlgen = sqlgen;
	}
	public static DbOrmQuery query(Connection conn, DbSqlGenerator sqlgen) {
		return new DbOrmQuery(conn, sqlgen);
	}

	public DbOrmQuery(DbSession session, DbSqlGenerator sqlgen) {
		super(session, sqlgen.generateQuery());
		this.sqlgen = sqlgen;
	}
	public static DbOrmQuery query(DbSession session, DbSqlGenerator sqlgen) {
		return new DbOrmQuery(session, sqlgen);
	}

	public DbOrmQuery(DbSqlGenerator sqlgen) {
		super(sqlgen.generateQuery());
		this.sqlgen = sqlgen;
	}
	public static DbOrmQuery query(DbSqlGenerator sqlgen) {
		return new DbOrmQuery(sqlgen);
	}

	// ---------------------------------------------------------------- initialization

	protected DbOrmManager dbOrmManager = DbOrmManager.getInstance();

	/**
	 * Returns used ORM manager.
	 */
	public DbOrmManager getManager() {
		return dbOrmManager;
	}

	/**
	 * Prepares the query after initialization. Besides default work, it checks if sql generator
	 * is used, and if so, generator hints and query parameters will be used for this query.
	 * Note regarding hints: since hints can be added manually, generators hints will be ignored
	 * if there exists some manually set hints.
	 */
	@Override
	protected void prepareQuery() {
		super.prepareQuery();
		if (sqlgen == null) {
			return;
		}
		String[] joinHints = sqlgen.getJoinHints();
		if (joinHints != null) {
			withHints(joinHints);
		}
		// insert parameters
		Map<String, ParameterValue> parameters = sqlgen.getQueryParameters();
		if (parameters == null) {
			return;
		}
		for (Map.Entry<String, ParameterValue> entry : parameters.entrySet()) {
			String paramName = entry.getKey();
			ParameterValue param = entry.getValue();
			DbEntityColumnDescriptor dec = param.getColumnDescriptor();
			if (dec == null) {
				setObject(paramName, param.getValue());
			} else {
				DbMetaUtil.resolveColumnDbSqlType(connection, dec);
				setObject(paramName, param.getValue(), dec.getSqlTypeClass(), dec.getDbSqlType());
			}
		}
	}

	// ---------------------------------------------------------------- join hints

	protected String[] hints;

	protected JoinHintResolver hintResolver = dbOrmManager.getHintResolver();

	/**
	 * Specifies hints for the query.
	 */
	public DbOrmQuery withHints(String hint) {
		this.hints = StringUtil.splitc(hint, ',');
		return this;
	}

	/**
	 * Specifies multiple hints for the query.
	 */
	public DbOrmQuery withHints(String[] hints) {
		this.hints = hints;
		return this;
	}

	/**
	 * Prepares a row (array of rows mapped object) using hints.
	 * Returns either single object or objects array.
	 */
	protected Object prepareRow(Object[] row) {
		row = hintResolver.join(row, hints);
		return row.length == 1 ? row[0] : row;
	}

	// ---------------------------------------------------------------- result set

	/**
	 * Executes the query and returns {@link #buildResultSetMapper(java.sql.ResultSet) builded ResultSet mapper}.
	 */
	protected ResultSetMapper executeAndBuildResultSetMapper() {
		return buildResultSetMapper(execute());
	}

	/**
	 * Factory for result sets mapper.
	 */
	protected ResultSetMapper buildResultSetMapper(ResultSet resultSet) {
		return new DefaultResultSetMapper(resultSet, sqlgen != null ? sqlgen.getColumnData() : null, dbOrmManager);
	}


	// ---------------------------------------------------------------- iterator

	public <T> Iterator<T> iterateOne(Class<T> type) {
		return iterateOne(type, false);
	}
	public <T> Iterator<T> iterateOneAndClose(Class<T> type) {
		return iterateOne(type, true);
	}
	public <T> Iterator<T> iterateOne() {
		return iterateOne(null, false);
	}
	public <T> Iterator<T> iterateOneAndClose() {
		return iterateOne(null, true);
	}
	protected <T> Iterator<T> iterateOne(Class<T> type, boolean close) {
		return new DbListOneIterator<T>(this, type, close);
	}

	public <T> Iterator<T> iterate(Class... types) {
		return iterate(types, false);
	}
	public <T> Iterator<T> iterateAndClose(Class... types) {
		return iterate(types, true);
	}
	public <T> Iterator<T> iterate() {
		return iterate(null, false);
	}
	public <T> Iterator<T> iterateAndClose() {
		return iterate(null, true);
	}
	protected <T> Iterator<T> iterate(Class[] types, boolean close) {
		return new DbListIterator<T>(this, types, close);
	}

	// ---------------------------------------------------------------- list

	public <T> List<T> listOne(Class<T> type) {
		return listOne(type, false);
	}
	public <T> List<T> listOneAndClose(Class<T> type) {
		return listOne(type, true);
	}
	public <T> List<T> listOne() {
		return listOne(null, false);
	}
	public <T> List<T> listOneAndClose() {
		return listOne(null, true);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> List<T> listOne(Class<T> type, boolean close) {
		List<T> result = new ArrayList<T>();
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		Class[] types = (type == null ? rsm.resolveTables() : new Class[]{type});
		while (rsm.next()) {
			result.add((T) rsm.parseOneObject(types));
		}
		close(rsm, close);
		return result;
	}


	public <T> List<T> list(Class... types) {
		return list(types, false);
	}
	public <T> List<T> listAndClose(Class... types) {
		return list(types, true);
	}
	public <T> List<T> list() {
		return list(null, false);
	}
	public <T> List<T> listAndClose() {
		return list(null, true);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> List<T> list(Class[] types, boolean close) {
		List<Object> result = new ArrayList<Object>();
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		if (types == null) {
			types = rsm.resolveTables();
		}
		while (rsm.next()) {
			result.add(prepareRow(rsm.parseObjects(types)));
		}
		close(rsm, close);
		return (List<T>) result;
	}

	// ---------------------------------------------------------------- set

	public <T> Set<T> listSetOne(Class<T> type) {
		return listSetOne(type, false);
	}
	public <T> Set<T> listSetOneAndClose(Class<T> type) {
		return listSetOne(type, true);
	}
	public <T> Set<T> listSetOne() {
		return listSetOne(null, false);
	}
	public <T> Set<T> listSetOneAndClose() {
		return listSetOne(null, true);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> Set<T> listSetOne(Class<T> type, boolean close) {
		Set<T> result = new LinkedHashSet<T>();
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		Class[] types = (type == null ? rsm.resolveTables() : new Class[]{type});
		while (rsm.next()) {
			result.add((T) rsm.parseOneObject(types));
		}
		close(rsm, close);
		return result;
	}

	public <T> Set<T> listSet(Class... types) {
		return listSet(types, false);
	}
	public <T> Set<T> listSetAndClose(Class... types) {
		return listSet(types, true);
	}
	public <T> Set<T> listSet() {
		return listSet(null, false);
	}
	public <T> Set<T> listSetAndClose() {
		return listSet(null, true);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> Set<T> listSet(Class[] types, boolean close) {
		Set<Object> result = new LinkedHashSet<Object>();
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		if (types == null) {
			types = rsm.resolveTables();
		}
		while (rsm.next()) {
			result.add(prepareRow(rsm.parseObjects(types)));
		}
		close(rsm, close);
		return (Set<T>) result;
	}

	// ---------------------------------------------------------------- find

	@SuppressWarnings({"unchecked"})
	public <T> T findOne(Class<T> type) {
		return findOne(type, false, null);
	}
	public <T> T findOneAndClose(Class<T> type) {
		return findOne(type, true, null);
	}
	public Object findOne() {
		return findOne(null, false, null);
	}
	public Object findOneAndClose() {
		return findOne(null, true, null);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> T findOne(Class<T> type, boolean close, ResultSet resultSet) {
		if (resultSet == null) {
			resultSet = execute();
		}
		ResultSetMapper rsm = buildResultSetMapper(resultSet);
		if (rsm.next() == false) {
			return null;
		}
		Class[] types = (type == null ? rsm.resolveTables() : new Class[]{type});
		Object result = rsm.parseOneObject(types);
		close(rsm, close);
		return (T) result;
	}


	public Object find(Class... types) {
		return find(types, false, null);
	}
	public Object findAndClose(Class... types) {
		return find(types, true, null);
	}
	public Object find() {
		return find(null, false, null);
	}
	public Object findAndClose() {
		return find(null, true, null);
	}
	protected Object find(Class[] types, boolean close, ResultSet resultSet) {
		if (resultSet == null) {
			resultSet = execute();
		}
		ResultSetMapper rsm = buildResultSetMapper(resultSet);
		if (rsm.next() == false) {
			return null;
		}
		if (types == null) {
			types = rsm.resolveTables();
		}
		Object result = prepareRow(rsm.parseObjects(types));
		close(rsm, close);
		return result;
	}

	// ---------------------------------------------------------------- generated columns

	public <T> T findGeneratedKey(Class<T> type) {
		return findOne(type, false, getGeneratedColumns());
	}

	public Object findGeneratedColumns(Class... types) {
		return find(types, false, getGeneratedColumns());
	}

	// ---------------------------------------------------------------- util

	/**
	 * Closes results set or whole query.
	 */
	protected void close(ResultSetMapper rsm, boolean closeQuery) {
		if (closeQuery == true) {
			close();
		} else {
			closeResultSet(rsm.getResultSet());
		}
	}

}