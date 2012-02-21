// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;
import jodd.db.type.SqlType;

import java.lang.reflect.Field;

/**
 * Few meta resolving utils.
 */
public class DbMetaUtil {

	/**
	 * Resolves table name from a type. If type is annotated, table name
	 * will be read from annotation value. If this value is empty or if
	 * type is not annotated, table name will be set to wildcard pattern '*'
	 * (to match all tables).
	 */
	public static String resolveTableName(Class<?> type, String tableNamePrefix, String tableNameSuffix, boolean toUpperCase) {
		String tableName = null;
		DbTable dbTable = type.getAnnotation(DbTable.class);
		if (dbTable != null) {
			tableName = dbTable.value().trim();
		}
		if ((tableName == null) || (tableName.length() == 0)) {
			tableName = DbNameUtil.convertClassNameToTableName(type, tableNamePrefix, tableNameSuffix, toUpperCase);
		}
		return tableName;
	}

	/**
	 * Resolves schema name. Uses default schema name if not specified.
	 */
	public static String resolveSchemaName(Class<?> type, String defaultSchemaName) {
		String schemaName = null;
		DbTable dbTable = type.getAnnotation(DbTable.class);
		if (dbTable != null) {
			schemaName = dbTable.schema().trim();
		}
		if ((schemaName == null) || (schemaName.length() == 0)) {
			schemaName = defaultSchemaName;
		}
		return schemaName;
	}

	/**
	 * Returns <code>true</code> if class is annotated with <code>DbTable</code> annotation.
	 */
	public static boolean resolveIsAnnotated(Class<?> type) {
		DbTable dbTable = type.getAnnotation(DbTable.class);
		return dbTable != null;
	}

	/**
	 * Resolves column descriptor from field. If field is annotated value will be read
	 * from annotation. If field is not annotated, then field will be ignored
	 * if entity is annotated. Otherwise, column name is generated from the field name.
	 */
	public static DbEntityColumnDescriptor resolveColumnDescriptors(DbEntityDescriptor ded, Field field, boolean isAnnotated, boolean toUpperCase) {
		String columnName = null;
		boolean isId = false;
		Class<? extends SqlType> sqlTypeClass = null;
		DbId dbId = field.getAnnotation(DbId.class);
		if (dbId != null) {
			columnName = dbId.value().trim();
			sqlTypeClass = dbId.sqlType();
			isId = true;
		} else {
			DbColumn dbColumn = field.getAnnotation(DbColumn.class);
			if (dbColumn != null) {
				columnName = dbColumn.value().trim();
				sqlTypeClass = dbColumn.sqlType();
			} else {
				if (isAnnotated == true) {
					return null;
				}
			}
		}

		if ((columnName == null) || (columnName.length() == 0)) {
			columnName = DbNameUtil.convertPropertyNameToColumnName(field.getName(), toUpperCase);
		}
		if (sqlTypeClass == SqlType.class) {
			sqlTypeClass = null;
		}
	    return new DbEntityColumnDescriptor(ded, columnName, field.getName(), field.getType(), isId, sqlTypeClass);
	}

}
