// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.orm.meta.DbTable;
import jodd.db.orm.meta.DbId;
import jodd.db.orm.meta.DbColumn;
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
	public static String resolveTableName(Class<?> type, String tableNamePrefix, String tableNameSuffix) {
		String tableName = null;
		DbTable dbTable = type.getAnnotation(DbTable.class);
		if (dbTable != null) {
			tableName = dbTable.value().trim();
		}
		if ((tableName == null) || (tableName.length() == 0)) {
			tableName = DbNameUtil.convertClassNameToTableName(type, tableNamePrefix, tableNameSuffix);
		}
		return tableName;
	}

	public static boolean resolveIsAnnotated(Class<?> type) {
		DbTable dbTable = type.getAnnotation(DbTable.class);
		return dbTable != null;
	}

	/**
	 * Resolves column descriptor from field. If field is annotated value will be read
	 * from annotation. If field is not annotated, then field will be ignored
	 * if entity is annotated. Otherwise, column name is generated from the field name.
	 */
	public static DbEntityColumnDescriptor resolveColumnDescriptors(Field field, boolean isAnnotated) {
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
			columnName = DbNameUtil.convertPropertyNameToColumnName(field.getName());
		}
		if (sqlTypeClass == SqlType.class) {
			sqlTypeClass = null;
		}
	    return new DbEntityColumnDescriptor(columnName, field.getName(), field.getType(), isId, sqlTypeClass);
	}

}
