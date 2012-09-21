// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

/**
 * Various column alias types. Sometimes, when database JDBC driver does not
 * support table names in ResultSet metadata, it might be useful to include
 * table name or a reference in the column name, separated by some separator.
 * <p>
 * Using TABLE_NAME type might be dangerous, since most databases (including Oracle)
 * has some maximum length for column names, so adding table name prefix might
 * be too much;)
 * <p>
 * Column types are usually used internally by SQL generators (see: {@link DbSqlGenerator}).
 */
public enum ColumnAliasType {

	TABLE_NAME(1),          // table_name<separator>column_name
	TABLE_REFERENCE(2),     // table_reference<separator>column_name
	COLUMN_CODE(3);         // col_<number>_

	int value;

	ColumnAliasType(int aliasType) {
		this.value = aliasType;
	}
}
