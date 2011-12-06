// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

/**
 * Various db name utilities.
 */
public class DbNameUtil {

	// ---------------------------------------------------------------- tables

	public static String convertClassNameToTableName(Class clazz, boolean toUpperCase) {
		return convertClassNameToTableName(clazz, null, null, toUpperCase);
	}
	public static String convertClassNameToTableName(Class clazz, String tablePrefix, String tableSuffix, boolean toUpperCase) {
		return convertClassNameToTableName(clazz.getSimpleName(), tablePrefix, tableSuffix, toUpperCase);
	}

	public static String convertClassNameToTableName(String className, boolean toUpperCase) {
		return convertClassNameToTableName(className, null, null, toUpperCase);
	}

	/**
	 * Converts class name to table name. All lower characters are converted to
	 * upper ones. All upper characters are prefixed with '_'. Therefore, class name
	 * <code>FooBooZoo</code> will be converted to <code>FOO_BOO_ZOO</code>.
	 * <p>
	 * Important note: if class name contains a '$' character, all letters after it
	 * will be ignored. This is useful when entities are proxified and a '$' sign is
	 * a common marker used in naming such classes.
	 */
	public static String convertClassNameToTableName(String className, String tablePrefix, String tableSuffix, boolean toUpperCase) {
		StringBuilder tableName = new StringBuilder(className.length() + 10);
		if (tablePrefix != null) {
			tableName.append(tablePrefix);
		}
		for (int i = 0; i < className.length(); i++) {
			char c = className.charAt(i);
			if (c == '$') {
				break;
			}
			if ((i != 0) && (Character.isUpperCase(c) == true)) {
				tableName.append('_');
			}
			if (toUpperCase) {
				tableName.append(Character.toUpperCase(c));
			} else {
				tableName.append(Character.toLowerCase(c));
			}
		}
		if (tableSuffix != null) {
			tableName.append(tableSuffix);
		}
		return tableName.toString();
	}

	/**
	 * Converts table name to class name. All characters after '_' are converted to upper ones.
	 * Other characters are converted to lower. Therefore, table name <code>FOO_BOO_ZOO</code>
	 * will be converted to <code>FooBooZoo</code>.
	 */
	public static String convertTableNameToClassName(String tableName, String tablePrefix, String tableSuffix) {
		StringBuilder className = new StringBuilder(tableName.length());
		int i = 0;
		int len = tableName.length();
		if (tablePrefix != null) {
			if (tableName.startsWith(tablePrefix) == true) {
				i = tablePrefix.length();
			}
		}
		if (tableSuffix != null) {
			if (tableName.endsWith(tableSuffix) == true) {
				len -= tableSuffix.length();
			}
		}
		boolean toUpper = true;
		for (; i < len; i++) {
			char c = tableName.charAt(i);
			if (c == '_') {
				toUpper = true;
				continue;
			}
			if (toUpper == true) {
				className.append(Character.toUpperCase(c));
				toUpper = false;
			} else {
				className.append(Character.toLowerCase(c));
			}
		}
		return className.toString();
	}

	// ---------------------------------------------------------------- columns


	/**
	 * Converts property name to column name.
	 */
	public static String convertPropertyNameToColumnName(String propertyName, boolean toUpperCase) {
		StringBuilder tableName = new StringBuilder(propertyName.length() + 10);
		for (int i = 0; i < propertyName.length(); i++) {
			char c = propertyName.charAt(i);
			if ((i != 0) && (Character.isUpperCase(c) == true)) {
				tableName.append('_');
			}
			if (toUpperCase) {
				tableName.append(Character.toUpperCase(c));
			} else {
				tableName.append(Character.toLowerCase(c));
			}
		}
		return tableName.toString();
	}

	/**
	 * Converts column name to property name.
	 */
	public static String convertColumnNameToPropertyName(String columnName) {
		StringBuilder propertyName = new StringBuilder(columnName.length());
		int i = 0;
		boolean toUpper = false;
		for (; i < columnName.length(); i++) {
			char c = columnName.charAt(i);
			if (c == '_') {
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
}
