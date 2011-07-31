// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.oom.mapper.ResultSetMapper;
import jodd.db.oom.sqlgen.ParameterValue;
import jodd.util.StringUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static jodd.db.oom.DbOomUtil.initialCollectionSize;

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
public class DbOomQuery extends DbQuery {

	// ---------------------------------------------------------------- default ctors

	public DbOomQuery(Connection conn, String sqlString) {
		super(conn, sqlString);
	}
	public static DbOomQuery query(Connection conn, String sqlString) {
		return new DbOomQuery(conn, sqlString);
	}


	public DbOomQuery(DbSession session, String sqlString) {
		super(session, sqlString);
	}
	public static DbOomQuery query(DbSession session, String sqlString) {
		return new DbOomQuery(session, sqlString);
	}


	public DbOomQuery(String sqlString) {
		super(sqlString);
	}
	public static DbOomQuery query(String sqlString) {
		return new DbOomQuery(sqlString);
	}

	// ---------------------------------------------------------------- sqlgen ctors

	protected DbSqlGenerator sqlgen;

	public DbOomQuery(Connection conn, DbSqlGenerator sqlgen) {
		super(conn, sqlgen.generateQuery());
		this.sqlgen = sqlgen;
	}
	public static DbOomQuery query(Connection conn, DbSqlGenerator sqlgen) {
		return new DbOomQuery(conn, sqlgen);
	}

	public DbOomQuery(DbSession session, DbSqlGenerator sqlgen) {
		super(session, sqlgen.generateQuery());
		this.sqlgen = sqlgen;
	}
	public static DbOomQuery query(DbSession session, DbSqlGenerator sqlgen) {
		return new DbOomQuery(session, sqlgen);
	}

	public DbOomQuery(DbSqlGenerator sqlgen) {
		super(sqlgen.generateQuery());
		this.sqlgen = sqlgen;
	}
	public static DbOomQuery query(DbSqlGenerator sqlgen) {
		return new DbOomQuery(sqlgen);
	}

	// ---------------------------------------------------------------- initialization

	protected DbOomManager dbOomManager = DbOomManager.getInstance();

	/**
	 * Returns used ORM manager.
	 */
	public DbOomManager getManager() {
		return dbOomManager;
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

	protected JoinHintResolver hintResolver = dbOomManager.getHintResolver();

	/**
	 * Specifies hints for the query.
	 */
	public DbOomQuery withHints(String hint) {
		this.hints = StringUtil.splitc(hint, ',');
		return this;
	}

	/**
	 * Specifies multiple hints for the query.
	 */
	public DbOomQuery withHints(String[] hints) {
		this.hints = hints;
		return this;
	}

	/**
	 * Prepares a row (array of rows mapped object) using hints.
	 * Returns either single object or objects array.
	 */
	protected Object resolveRowHints(Object[] row) {
		row = hintResolver.join(row, hints);
		return row.length == 1 ? row[0] : row;
	}

	// ---------------------------------------------------------------- result set

	/**
	 * Executes the query and returns {@link #createResultSetMapper(java.sql.ResultSet) builded ResultSet mapper}.
	 */
	protected ResultSetMapper executeAndBuildResultSetMapper() {
		return createResultSetMapper(execute());
	}

	/**
	 * Factory for result sets mapper.
	 */
	protected ResultSetMapper createResultSetMapper(ResultSet resultSet) {
		return dbOomManager.createResultSetMapper(resultSet, sqlgen != null ? sqlgen.getColumnData() : null);
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
		return listOne(type, 0, false);
	}
	public <T> List<T> listOneAndClose(Class<T> type) {
		return listOne(type, 0, true);
	}
	public <T> List<T> listOne() {
		return listOne(null, 0, false);
	}
	public <T> List<T> listOneAndClose() {
		return listOne(null, 0, true);
	}
	public <T> List<T> listOne(int max, Class<T> type) {
		return listOne(type, max, false);
	}
	public <T> List<T> listOneAndClose(int max, Class<T> type) {
		return listOne(type, max, true);
	}
	public <T> List<T> listOne(int max) {
		return listOne(null, max, false);
	}
	public <T> List<T> listOneAndClose(int max) {
		return listOne(null, max, true);
	}

	/**
	 * Iterates results set, maps rows to just one class and populates the array list.
	 * @param type target type
	 * @param max max number of rows to collect, <code>0</code> for all
	 * @param close <code>true</code> if query is closed at the end, otherwise <code> false
	 * @return list of mapped entities
	 */
	@SuppressWarnings({"unchecked"})
	protected <T> List<T> listOne(Class<T> type, int max, boolean close) {
		List<T> result = new ArrayList<T>(initialCollectionSize(max));
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		Class[] types = (type == null ? rsm.resolveTables() : new Class[]{type});
		while (rsm.next()) {
			result.add((T) rsm.parseOneObject(types));
			max--;
			if (max == 0) {
				break;
			}
		}
		close(rsm, close);
		return result;
	}


	public <T> List<T> list(Class... types) {
		return list(types, 0, false);
	}
	public <T> List<T> listAndClose(Class... types) {
		return list(types, 0, true);
	}
	public <T> List<T> list() {
		return list(null, 0, false);
	}
	public <T> List<T> listAndClose() {
		return list(null, 0, true);
	}
	public <T> List<T> list(int max, Class... types) {
		return list(types, max, false);
	}
	public <T> List<T> listAndClose(int max, Class... types) {
		return list(types, max, true);
	}
	public <T> List<T> list(int max) {
		return list(null, max, false);
	}
	public <T> List<T> listAndClose(int max) {
		return list(null, max, true);
	}
	/**
	 * Iterates result set, maps rows to classes and populates the array list.
	 * @param types mapping types
	 * @param max max number of rows to collect, <code>0</code> for all
	 * @param close <code>true</code> if query is closed at the end, otherwise <code> false
	 * @return list of mapped entities
	 */
	@SuppressWarnings({"unchecked"})
	protected <T> List<T> list(Class[] types, int max, boolean close) {
		List<T> result = new ArrayList<T>(initialCollectionSize(max));
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		if (types == null) {
			types = rsm.resolveTables();
		}
		while (rsm.next()) {
			Object row = resolveRowHints(rsm.parseObjects(types));
			result.add((T) row);

			max--;
			if (max == 0) {
				break;
			}
		}
		close(rsm, close);
		return result;
	}

	// ---------------------------------------------------------------- set

	public <T> Set<T> listSetOne(Class<T> type) {
		return listSetOne(type, 0, false);
	}
	public <T> Set<T> listSetOneAndClose(Class<T> type) {
		return listSetOne(type, 0, true);
	}
	public <T> Set<T> listSetOne() {
		return listSetOne(null, 0, false);
	}
	public <T> Set<T> listSetOneAndClose() {
		return listSetOne(null, 0, true);
	}
	public <T> Set<T> listSetOne(int max, Class<T> type) {
		return listSetOne(type, max, false);
	}
	public <T> Set<T> listSetOneAndClose(int max, Class<T> type) {
		return listSetOne(type, max, true);
	}
	public <T> Set<T> listSetOne(int max) {
		return listSetOne(null, max, false);
	}
	public <T> Set<T> listSetOneAndClose(int max) {
		return listSetOne(null, max, true);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> Set<T> listSetOne(Class<T> type, int max, boolean close) {
		Set<T> result = new LinkedHashSet<T>(initialCollectionSize(max));
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		Class[] types = (type == null ? rsm.resolveTables() : new Class[]{type});
		while (rsm.next()) {
			result.add((T) rsm.parseOneObject(types));
			max--;
			if (max == 0) {
				break;
			}
		}
		close(rsm, close);
		return result;
	}

	public <T> Set<T> listSet(Class... types) {
		return listSet(types, 0, false);
	}
	public <T> Set<T> listSetAndClose(Class... types) {
		return listSet(types, 0, true);
	}
	public <T> Set<T> listSet() {
		return listSet(null, 0, false);
	}
	public <T> Set<T> listSetAndClose() {
		return listSet(null, 0, true);
	}
	public <T> Set<T> listSet(int max, Class... types) {
		return listSet(types, max, false);
	}
	public <T> Set<T> listSetAndClose(int max, Class... types) {
		return listSet(types, max, true);
	}
	public <T> Set<T> listSet(int max) {
		return listSet(null, max, false);
	}
	public <T> Set<T> listSetAndClose(int max) {
		return listSet(null, max, true);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> Set<T> listSet(Class[] types, int max, boolean close) {
		Set<T> result = new LinkedHashSet<T>(initialCollectionSize(max));
		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		if (types == null) {
			types = rsm.resolveTables();
		}
		while (rsm.next()) {
			Object row = resolveRowHints(rsm.parseObjects(types));
			result.add((T) row);
			max--;
			if (max == 0) {
				break;
			}
		}
		close(rsm, close);
		return result;
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
		ResultSetMapper rsm = createResultSetMapper(resultSet);
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
		ResultSetMapper rsm = createResultSetMapper(resultSet);
		if (rsm.next() == false) {
			return null;
		}
		if (types == null) {
			types = rsm.resolveTables();
		}
		Object result = resolveRowHints(rsm.parseObjects(types));
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