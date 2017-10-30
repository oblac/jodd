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

package jodd.db.oom;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbUtil;
import jodd.db.JoddDb;
import jodd.db.oom.mapper.DefaultResultSetMapper;
import jodd.db.oom.mapper.ResultSetMapper;
import jodd.db.oom.sqlgen.ParameterValue;
import jodd.db.type.SqlType;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static jodd.db.oom.DbOomUtil.initialCollectionSize;

/**
 * A simple ORM extension for {@link DbQuery}.
 * <p>
 * OOM extension may map results to objects in two ways:
 * <ul>
 * <li><i>auto</i> mode - when result set is mapped to provided types, and</li>
 * <li><i>mapped</i> mode - requires explicit mapping definitions.</li>
 * </ul>
 */
public class DbOomQuery extends DbQuery {

	private static final Logger log = LoggerFactory.getLogger(DbOomQuery.class);

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
		if (hints == null) {
			String[] joinHints = sqlgen.getJoinHints();
			if (joinHints != null) {
				withHints(joinHints);
			}
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
				resolveColumnDbSqlType(connection, dec);
				setObject(paramName, param.getValue(), dec.getSqlTypeClass(), dec.getDbSqlType());
			}
		}
	}

	/**
	 * Resolves column db sql type and populates it in column descriptor if missing.
	 */
	protected void resolveColumnDbSqlType(Connection connection, DbEntityColumnDescriptor dec) {
		if (dec.dbSqlType != SqlType.DB_SQLTYPE_UNKNOWN) {
			return;
		}
		ResultSet rs = null;
		DbEntityDescriptor ded = dec.getDbEntityDescriptor();
		try {
			DatabaseMetaData dmd = connection.getMetaData();
			rs = dmd.getColumns(null, ded.getSchemaName(), ded.getTableName(), dec.getColumnName());
			if (rs.next()) {
				dec.dbSqlType = rs.getInt("DATA_TYPE");
			} else {
				dec.dbSqlType = SqlType.DB_SQLTYPE_NOT_AVAILABLE;
				if (log.isWarnEnabled()) {
					log.warn("Column SQL type not available: " + ded.toString() + '.' + dec.getColumnName());
				}
			}
		} catch (SQLException sex) {
			dec.dbSqlType = SqlType.DB_SQLTYPE_NOT_AVAILABLE;
			if (log.isWarnEnabled()) {
				log.warn("Column SQL type not resolved: " + ded.toString() + '.' + dec.getColumnName(), sex);
			}
		} finally {
			DbUtil.close(rs);
		}
	}


	// ---------------------------------------------------------------- join hints

	protected final JoinHintResolver hintResolver = JoddDb.runtime().hintResolver();

	protected String[] hints;

	/**
	 * Specifies hints for the query. Provided string is
	 * split on ',' separator.
	 */
	public DbOomQuery withHints(String hint) {
		this.hints = StringUtil.splitc(hint, ',');
		return this;
	}

	/**
	 * Specifies multiple hints for the query.
	 */
	public DbOomQuery withHints(String... hints) {
		this.hints = hints;
		return this;
	}

	/**
	 * Prepares a row (array of rows mapped object) using hints.
	 * Returns either single object or objects array.
	 */
	protected Object resolveRowResults(Object[] row) {
		row = hintResolver.join(row, hints);
		return row.length == 1 ? row[0] : row;
	}

	// ---------------------------------------------------------------- result set

	protected boolean cacheEntities = JoddDb.defaults().getDbOomConfig().isCacheEntitiesInResultSet();

	/**
	 * Defines if entities should be cached in {@link ResultSetMapper}.
	 * Overrides default value in {@link DbEntityManager}.
	 */
	public DbOomQuery cacheEntities(boolean cacheEntities) {
		this.cacheEntities = cacheEntities;
		return this;
	}

	/**
	 * Executes the query and returns {@link #createResultSetMapper(java.sql.ResultSet) builded ResultSet mapper}.
	 */
	protected ResultSetMapper executeAndBuildResultSetMapper() {
		ResultSet resultSet = execute();

		return createResultSetMapper(resultSet);
	}

	/**
	 * Factory for result sets mapper.
	 */
	protected ResultSetMapper createResultSetMapper(ResultSet resultSet) {
		Map<String, ColumnData> columnAliases = sqlgen != null ? sqlgen.getColumnData() : null;

		return new DefaultResultSetMapper(resultSet, columnAliases, cacheEntities, this);
	}

	// ---------------------------------------------------------------- db list

	protected boolean entityAwareMode = JoddDb.defaults().getDbOomConfig().isEntityAwareMode();

	/**
	 * Defines entity-aware mode for entities tracking in result collection.
	 */
	public DbOomQuery entityAwareMode(boolean entityAware) {
		if (entityAware) {
			this.cacheEntities = true;
		}
		this.entityAwareMode = entityAware;
		return this;
	}

	// ---------------------------------------------------------------- iterator

	public <T> Iterator<T> iterate(Class... types) {
		return iterate(types, autoClose);
	}
	public <T> Iterator<T> iterate() {
		return iterate(null, autoClose);
	}
	protected <T> Iterator<T> iterate(Class[] types, boolean close) {
		return new DbListIterator<>(this, types, close);
	}

	// ---------------------------------------------------------------- list

	public <T> List<T> list(Class... types) {
		return list(types, -1, autoClose);
	}
	public <T> List<T> list() {
		return list(null, -1, autoClose);
	}
	public <T> List<T> list(int max, Class... types) {
		return list(types, max, autoClose);
	}
	public <T> List<T> list(int max) {
		return list(null, max, autoClose);
	}
	/**
	 * Iterates result set, maps rows to classes and populates resulting array list.
	 * @param types mapping types
	 * @param max max number of rows to collect, <code>-1</code> for all
	 * @param close <code>true</code> if query is closed at the end, otherwise <code>false</code>.
	 * @return list of mapped entities or array of entities
	 */
	@SuppressWarnings({"unchecked"})
	protected <T> List<T> list(Class[] types, int max, boolean close) {
		List<T> result = new ArrayList<>(initialCollectionSize(max));

		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		if (types == null) {
			types = rsm.resolveTables();
		}

		Object previousElement = null;

		while (rsm.next()) {
			Object[] objects = rsm.parseObjects(types);
			Object row = resolveRowResults(objects);

			int size = result.size();

			T newElement = (T) row;

			if (entityAwareMode && size > 0) {
				if (previousElement != null && newElement != null) {
					boolean equals;

					if (newElement.getClass().isArray()) {
						equals = Arrays.equals((Object[]) previousElement, (Object[]) newElement);
					} else {
						equals = previousElement.equals(newElement);
					}

					if (equals) {
						continue;
					}
				}
			}

			if (size == max) {
				break;
			}

			result.add(newElement);
			previousElement = newElement;
		}

		close(rsm, close);
		return result;
	}

	// ---------------------------------------------------------------- set

	public <T> Set<T> listSet(Class... types) {
		return listSet(types, -1, autoClose);
	}
	public <T> Set<T> listSet() {
		return listSet(null, -1, autoClose);
	}
	public <T> Set<T> listSet(int max, Class... types) {
		return listSet(types, max, autoClose);
	}
	public <T> Set<T> listSet(int max) {
		return listSet(null, max, autoClose);
	}
	@SuppressWarnings({"unchecked"})
	protected <T> Set<T> listSet(Class[] types, int max, boolean close) {
		Set<T> result = new LinkedHashSet<>(initialCollectionSize(max));

		ResultSetMapper rsm = executeAndBuildResultSetMapper();
		if (types == null) {
			types = rsm.resolveTables();
		}

		Object previousElement = null;

		while (rsm.next()) {
			Object[] objects = rsm.parseObjects(types);
			Object row = resolveRowResults(objects);

			int size = result.size();

			T newElement = (T) row;

			if (entityAwareMode && size > 0) {
				if (previousElement != null && newElement != null) {
					boolean equals;

					if (newElement.getClass().isArray()) {
						equals = Arrays.equals((Object[]) previousElement, (Object[]) newElement);
					} else {
						equals = previousElement.equals(newElement);
					}

					if (equals) {
						continue;
					}
				}
			}

			if (size == max) {
				break;
			}

			result.add(newElement);
			previousElement = newElement;
		}

		close(rsm, close);
		return result;
	}

	// ---------------------------------------------------------------- find

	public <T> T find(Class... types) {
		return find(types, autoClose, null);
	}
	public <T> T find() {
		return find(null, autoClose, null);
	}
	protected <T> T find(Class[] types, boolean close, ResultSet resultSet) {
		if (resultSet == null) {
			resultSet = execute();
		}
		ResultSetMapper rsm = createResultSetMapper(resultSet);

		Iterator<T> iterator = new DbListIterator<>(this, types, rsm, false);

		T result = null;

		if (iterator.hasNext()) {
			result = iterator.next();
		}

		close(rsm, close);
		return result;
	}

	// ---------------------------------------------------------------- generated columns

	/**
	 * Finds generated key column of given type.
	 */
	public <T> T findGeneratedKey(Class<T> type) {
		return find(new Class[] {type}, false, getGeneratedColumns());
	}

	/**
	 * Finds generated columns.
	 */
	public Object findGeneratedColumns(Class... types) {
		return find(types, false, getGeneratedColumns());
	}

	// ---------------------------------------------------------------- util

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DbOomQuery autoClose() {
		super.autoClose();
		return this;
	}

	/**
	 * Closes results set or whole query.
	 */
	protected void close(ResultSetMapper rsm, boolean closeQuery) {
		if (closeQuery) {
			close();
		} else {
			closeResultSet(rsm.getResultSet());
		}
	}

}