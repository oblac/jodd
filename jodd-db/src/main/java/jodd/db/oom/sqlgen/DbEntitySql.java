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

import jodd.bean.BeanUtil;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbOomManager;
import jodd.util.StringPool;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static jodd.util.StringPool.EQUALS;
import static jodd.util.StringPool.SPACE;
import static jodd.util.StringUtil.capitalize;
import static jodd.util.StringUtil.uncapitalize;

/**
 * Useful {@link DbSqlBuilder} generators.
 */
public class DbEntitySql {

	private static final String DELETE_FROM = "delete from ";
	private static final String WHERE = " where ";
	private static final String UPDATE = "update ";
	private static final String SELECT = "select ";
	private static final String FROM = " from ";
	private static final String SET = " set ";
	private static final String SELECT_COUNT_1_FROM = "select count(*) from ";

	// ---------------------------------------------------------------- insert

	/**
	 * Creates INSERT query for the entity.
	 */
	public static DbSqlBuilder insert(Object entity) {
		return sql().insert(entity);
	}

	// ---------------------------------------------------------------- truncate

	/**
	 * Creates DELETE query that truncates all table data.
	 */
	public static DbSqlBuilder truncate(Object entity) {
		return sql().$(DELETE_FROM).table(entity, null);
	}

	// ---------------------------------------------------------------- update

	/**
	 * Creates UPDATE query that updates all non-null values of an entity that is matched by id.
	 */
	public static DbSqlBuilder update(Object entity) {
		String tableRef = createTableRefName(entity);

		if (!DbOomManager.getInstance().getSqlGenConfig().isUpdateAcceptsTableAlias()) {
			tableRef = null;
		}

		return sql().$(UPDATE).table(entity, tableRef).set(tableRef, entity).$(WHERE).matchIds(tableRef, entity);
	}

	/**
	 * Creates UPDATE query that updates all values of an entity that is matched by id.
	 */
	public static DbSqlBuilder updateAll(Object entity) {
		String tableRef = createTableRefName(entity);

		if (!DbOomManager.getInstance().getSqlGenConfig().isUpdateAcceptsTableAlias()) {
			tableRef = null;
		}

		return sql().$(UPDATE).table(entity, tableRef).setAll(tableRef, entity).$(WHERE).matchIds(tableRef, entity);
	}

	/**
	 * Creates UPDATE query for single column of an entity that is matched by id.
	 */
	public static DbSqlBuilder updateColumn(Object entity, String columnRef, Object value) {
		String tableRef = createTableRefName(entity);

		if (!DbOomManager.getInstance().getSqlGenConfig().isUpdateAcceptsTableAlias()) {
			tableRef = null;
		}

		return sql().$(UPDATE).table(entity, tableRef).$(SET).ref(null, columnRef).$(EQUALS).columnValue(value).$(WHERE).matchIds(tableRef, entity);
	}

	/**
	 * Reads property value and updates the DB.
	 */
	public static DbSqlBuilder updateColumn(Object entity, String columnRef) {
		Object value = BeanUtil.pojo.getProperty(entity, columnRef);
		return updateColumn(entity, columnRef, value);
	}

	// ---------------------------------------------------------------- delete

	/**
	 * Creates DELETE query that deletes entity matched by non-null values.
	 */
	public static DbSqlBuilder delete(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(DELETE_FROM).table(entity, null, tableRef).$(WHERE).match(tableRef, entity);
	}

	/**
	 * Creates DELETE query that deletes entity matched by all values.
	 */
	public static DbSqlBuilder deleteByAll(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(DELETE_FROM).table(entity, null, tableRef).$(WHERE).matchAll(tableRef, entity);
	}

	// ---------------------------------------------------------------- delete by id

	/**
	 * Creates DELETE query that deletes entity by ID.
	 */
	public static DbSqlBuilder deleteById(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(DELETE_FROM).table(entity, null, tableRef).$(WHERE).matchIds(tableRef, entity);
	}

	/**
	 * Creates DELETE query that deletes entity by ID.
	 */
	public static DbSqlBuilder deleteById(Object entityType, long id) {
		String tableRef = createTableRefName(entityType);
		return sql().
			$(DELETE_FROM).table(entityType, null, tableRef).
			$(WHERE).refId(tableRef).$(EQUALS).columnValue(Long.valueOf(id));
	}

	// ---------------------------------------------------------------- from

	/**
	 * Creates 'SELECT all FROM entity' part of the SQL query that can be easily extended.
	 * Entity is referred with its simple class name.
	 */
	public static DbSqlBuilder from(Object entity) {
		return from(entity, createTableRefName(entity));
	}

	public static DbSqlBuilder from(Object entity, String tableRef) {
		return sql().$(SELECT).column(tableRef).$(FROM).table(entity, tableRef).$(SPACE);
	}

	public static DbSqlBuilder from(Class entityType) {
		return from(entityType, createTableRefName(entityType));
	}

	public static DbSqlBuilder from(Class entityType, String tableRef) {
		return sql().$(SELECT).column(tableRef).$(FROM).table(entityType, tableRef).$(SPACE);
	}

	// ---------------------------------------------------------------- find

	/**
	 * Creates SELECT criteria for the entity matched by non-null values.
	 */
	public static DbSqlBuilder find(Class target, Object matchEntity) {
		String tableRef = createTableRefName(target);
		return sql().$(SELECT).column(tableRef).$(FROM).table(target, tableRef).$(WHERE).match(tableRef, matchEntity);
	}

	/**
	 * Creates SELECT criteria for the entity matched by non-null values.
	 */
	public static DbSqlBuilder find(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(SELECT).column(tableRef).$(FROM).table(entity, tableRef).$(WHERE).match(tableRef, entity);
	}

	/**
	 * Creates SELECT criteria for the entity matched by all values.
	 */
	public static DbSqlBuilder findByAll(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(SELECT).column(tableRef).$(FROM).table(entity, tableRef).$(WHERE).matchAll(tableRef, entity);
	}

	/**
	 * Creates SELECT criteria for the entity matched by column name
	 */
	public static DbSqlBuilder findByColumn(Class entity, String column, Object value) {
		String tableRef = createTableRefName(entity);
		return sql().$(SELECT).column(tableRef).$(FROM).table(entity, tableRef).$(WHERE).ref(tableRef, column).$(EQUALS).columnValue(value);
	}

	/**
	 * Creates SELECT criteria for the entity matched by foreign key.
	 * Foreign key is created by concatenating foreign table name and column name.
	 */
	public static DbSqlBuilder findForeign(Class entity, Object value) {
		String tableRef = createTableRefName(entity);
		DbOomManager dbOomManager = DbOomManager.getInstance();
		DbEntityDescriptor dedFk = dbOomManager.lookupType(value.getClass());

		String tableName = dbOomManager.getTableNames().convertTableNameToEntityName(dedFk.getTableName());
		String columnName = dbOomManager.getColumnNames().convertColumnNameToPropertyName(dedFk.getIdColumnName());

		String fkColumn = uncapitalize(tableName) + capitalize(columnName);
		Object idValue = BeanUtil.pojo.getProperty(value, dedFk.getIdPropertyName());
		return sql().$(SELECT).column(tableRef).$(FROM).table(entity, tableRef).$(WHERE).ref(tableRef, fkColumn).$(EQUALS).columnValue(idValue);
	}

	// ---------------------------------------------------------------- find by Id

	/**
	 * Creates SELECT criteria for the entity matched by id.
	 */
	public static DbSqlBuilder findById(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(SELECT).column(tableRef).$(FROM).table(entity, tableRef).$(WHERE).matchIds(tableRef, entity);
	}

	/**
	 * Creates SELECT criteria for the entity matched by id.
	 */
	public static DbSqlBuilder findById(Object entityType, long id) {
		String tableRef = createTableRefName(entityType);
		return sql().$(SELECT).column(tableRef).$(FROM).table(entityType, tableRef)
				.$(WHERE).refId(tableRef).$(EQUALS).columnValue(Long.valueOf(id));
	}

	// ---------------------------------------------------------------- count

	/**
	 * Creates SELECT COUNT criteria for the entity matched by non-null values.
	 */
	public static DbSqlBuilder count(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(SELECT_COUNT_1_FROM).table(entity, tableRef).$(WHERE).match(tableRef, entity);
	}

	/**
	 * Creates SELECT COUNT all query.
	 */
	public static DbSqlBuilder count(Class entityType) {
		String tableRef = createTableRefName(entityType);
		return sql().$(SELECT_COUNT_1_FROM).table(entityType, tableRef);
	}


	/**
	 * Creates SELECT COUNT criteria for the entity matched by all values.
	 */
	public static DbSqlBuilder countAll(Object entity) {
		String tableRef = createTableRefName(entity);
		return sql().$(SELECT_COUNT_1_FROM).table(entity, tableRef).$(WHERE).matchAll(tableRef, entity);
	}

	// ---------------------------------------------------------------- increase

	/**
	 * Creates UPDATE that increases/decreases column by some delta value.
	 */
	public static DbSqlBuilder increaseColumn(Class entity, long id, String columnRef, Number delta, boolean increase) {
		String tableRef = createTableRefName(entity);

		return sql().$(UPDATE).table(entity, null, tableRef).$(SET)
				.ref(null, columnRef).$(EQUALS).ref(null, columnRef)
				.$(increase ? StringPool.PLUS : StringPool.DASH)
				.columnValue(delta).$(WHERE).refId(tableRef).$(EQUALS).columnValue(Long.valueOf(id));
	}


	// ---------------------------------------------------------------- resolve tableRef

	/**
	 * Creates table reference name from entity type.
	 * Always appends an underscore to reference name in order
	 * to circumvent SQL compatibility issues when entity class name
	 * equals to a reserved word.
	 */
	protected static String createTableRefName(Object entity) {
		Class type = entity.getClass();
		type = (type == Class.class ? (Class) entity : type);
		return (type.getSimpleName() + '_');
	}

}
