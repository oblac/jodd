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

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbMapTo;
import jodd.db.oom.meta.DbTable;
import jodd.db.oom.naming.ColumnNamingStrategy;
import jodd.db.oom.naming.TableNamingStrategy;
import jodd.db.type.SqlType;
import jodd.introspector.PropertyDescriptor;
import jodd.util.StringUtil;

/**
 * Meta-data resolving utils.
 */
public class DbMetaUtil {

	/**
	 * Resolves table name from a type. If type is annotated, table name
	 * will be read from annotation value. If this value is empty or if
	 * type is not annotated, table name will be set to wildcard pattern '*'
	 * (to match all tables).
	 */
	public static String resolveTableName(final Class<?> type, final TableNamingStrategy tableNamingStrategy) {
		String tableName = null;
		final DbTable dbTable = type.getAnnotation(DbTable.class);
		if (dbTable != null) {
			tableName = dbTable.value().trim();
		}
		if ((tableName == null) || (tableName.length() == 0)) {
			tableName = tableNamingStrategy.convertEntityNameToTableName(type);
		} else {
			if (!tableNamingStrategy.isStrictAnnotationNames()) {
				tableName = tableNamingStrategy.applyToTableName(tableName);
			}
		}

		return quoteIfRequired(tableName, tableNamingStrategy.isAlwaysQuoteNames(), tableNamingStrategy.getQuoteChar());
	}

	/**
	 * Resolves schema name from a type. Uses default schema name if not specified.
	 */
	public static String resolveSchemaName(final Class<?> type, final String defaultSchemaName) {
		String schemaName = null;
		final DbTable dbTable = type.getAnnotation(DbTable.class);
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
	public static boolean resolveIsAnnotated(final Class<?> type) {
		DbTable dbTable = type.getAnnotation(DbTable.class);
		return dbTable != null;
	}

	/**
	 * Resolves column descriptor from property. If property is annotated value will be read
	 * from annotation. If property is not annotated, then property will be ignored
	 * if entity is annotated. Otherwise, column name is generated from the property name.
	 */
	public static DbEntityColumnDescriptor resolveColumnDescriptors(
		final DbEntityDescriptor dbEntityDescriptor,
		final PropertyDescriptor property,
		final boolean isAnnotated,
		final ColumnNamingStrategy columnNamingStrategy) {

		String columnName = null;
		boolean isId = false;
		Class<? extends SqlType> sqlTypeClass = null;

		// read ID annotation

		DbId dbId = null;

		if (property.getFieldDescriptor() != null) {
			dbId = property.getFieldDescriptor().getField().getAnnotation(DbId.class);
		}
		if (dbId == null && property.getReadMethodDescriptor() != null) {
			dbId = property.getReadMethodDescriptor().getMethod().getAnnotation(DbId.class);
		}
		if (dbId == null && property.getWriteMethodDescriptor() != null) {
			dbId = property.getWriteMethodDescriptor().getMethod().getAnnotation(DbId.class);
		}

		if (dbId != null) {
			columnName = dbId.value().trim();
			sqlTypeClass = dbId.sqlType();
			isId = true;
		} else {
			DbColumn dbColumn = null;

			if (property.getFieldDescriptor() != null) {
				dbColumn = property.getFieldDescriptor().getField().getAnnotation(DbColumn.class);
			}
			if (dbColumn == null && property.getReadMethodDescriptor() != null) {
				dbColumn = property.getReadMethodDescriptor().getMethod().getAnnotation(DbColumn.class);
			}
			if (dbColumn == null && property.getWriteMethodDescriptor() != null) {
				dbColumn = property.getWriteMethodDescriptor().getMethod().getAnnotation(DbColumn.class);
			}

			if (dbColumn != null) {
				columnName = dbColumn.value().trim();
				sqlTypeClass = dbColumn.sqlType();
			} else {
				if (isAnnotated) {
					return null;
				}
			}
		}

		if (StringUtil.isEmpty(columnName)) {
			// default annotation value
			columnName = columnNamingStrategy.convertPropertyNameToColumnName(property.getName());
		} else {
			if (!columnNamingStrategy.isStrictAnnotationNames()) {
				columnName = columnNamingStrategy.applyToColumnName(columnName);
			}
		}
		if (sqlTypeClass == SqlType.class) {
			sqlTypeClass = null;
		}

		return new DbEntityColumnDescriptor(
			dbEntityDescriptor,
			quoteIfRequired(columnName, columnNamingStrategy.isAlwaysQuoteNames(), columnNamingStrategy.getQuoteChar()),
			property.getName(),
			property.getType(),
			isId,
			sqlTypeClass);
	}

	/**
	 * Resolves mapped types from {@link jodd.db.oom.meta.DbMapTo} annotation.
	 */
	public static Class[] resolveMappedTypes(final Class type) {
		DbMapTo dbMapTo = (DbMapTo) type.getAnnotation(DbMapTo.class);
		if (dbMapTo == null) {
			return null;
		}
		return dbMapTo.value();
	}

	// ---------------------------------------------------------------- privates

	private static String quoteIfRequired(final String name, final boolean alwaysQuoteNames, final char quoteChar) {
		if (StringUtil.detectQuoteChar(name) != 0) {
			return name;   // already quoted
		}
		if (alwaysQuoteNames && quoteChar != 0) {
			return quoteChar + name + quoteChar;
		}
		return name;
	}

}