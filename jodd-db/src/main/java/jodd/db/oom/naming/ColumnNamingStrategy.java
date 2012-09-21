// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.naming;

import jodd.util.StringUtil;

/**
 * Naming strategy for converting property names
 * to database column names.
 */
public class ColumnNamingStrategy extends BaseNamingStrategy {

	// ---------------------------------------------------------------- methods

	/**
	 * Converts property name to column name.
	 */
	public String convertPropertyNameToColumnName(String propertyName) {
		StringBuilder tableName = new StringBuilder(propertyName.length() * 2);

		if (splitCamelCase) {
			String convertedTableName = StringUtil.fromCamelCase(propertyName, separatorChar);
			tableName.append(convertedTableName);
		} else {
			tableName.append(propertyName);
		}

		if (changeCase == false) {
			return tableName.toString();
		}
		return uppercase ?
				toUppercase(tableName).toString() :
				toLowercase(tableName).toString();

	}

	/**
	 * Converts column name to property name.
	 */
	public String convertColumnNameToPropertyName(String columnName) {
		StringBuilder propertyName = new StringBuilder(columnName.length());
		int len = columnName.length();

		if (splitCamelCase == true) {
			boolean toUpper = false;
			for (int i = 0; i < len; i++) {
				char c = columnName.charAt(i);
				if (c == separatorChar) {
					toUpper = true;
					continue;
				}
				if (toUpper == true) {
					propertyName.append(Character.toUpperCase(c));
					toUpper = false;
				} else {
					propertyName.append(Character.toLowerCase(c));
				}
			}
			return propertyName.toString();
		}
		return columnName;
	}
}
