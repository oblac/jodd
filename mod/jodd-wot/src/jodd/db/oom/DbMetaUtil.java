// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.meta.DbTable;

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

}
