// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.naming;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Naming strategy for converting entity names
 * (i.e. class names) to database table names.
 */
public class TableNamingStrategy extends BaseNamingStrategy {

	// ---------------------------------------------------------------- properties

	protected String prefix = StringPool.EMPTY;
	protected String suffix = StringPool.EMPTY;
	protected char entityNameTerminator = '$';

	public String getPrefix() {
		return prefix;
	}

	/**
	 * Table prefix, may be <code>null</code>.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	/**
	 * Table suffix, may be <code>null</code>.
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public char getEntityNameTerminator() {
		return entityNameTerminator;
	}

	/**
	 * Specifies the terminator character for entity names.
	 * When some type is proxified, new class name usually contains
	 * some special character, like '$'.
	 */
	public void setEntityNameTerminator(char entityNameTerminator) {
		this.entityNameTerminator = entityNameTerminator;
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Converts entity name to table name.
	 * @see #convertEntityNameToTableName(String)
	 */
	public String convertEntityNameToTableName(Class type) {
		return convertEntityNameToTableName(type.getSimpleName());
	}

	/**
	 * Converts entity (type) name to table name.
	 */
	public String convertEntityNameToTableName(String entityName) {
		int ndx = entityName.indexOf(entityNameTerminator);
		if (ndx != -1) {
			entityName = entityName.substring(0, ndx);
		}

		StringBuilder tableName = new StringBuilder(entityName.length() * 2);

		if (prefix != null) {
			tableName.append(prefix);
		}

		if (splitCamelCase) {
			String convertedTableName = StringUtil.fromCamelCase(entityName, separatorChar);
			tableName.append(convertedTableName);
		} else {
			tableName.append(entityName);
		}

		if (suffix != null) {
			tableName.append(suffix);
		}

		if (changeCase == false) {
			return tableName.toString();
		}
		return uppercase ?
				toUppercase(tableName).toString() :
				toLowercase(tableName).toString();

	}

	/**
	 * Converts table name to entity (type) name.
	 */
	public String convertTableNameToEntityName(String tableName) {
		StringBuilder className = new StringBuilder(tableName.length());
		int len = tableName.length();

		int i = 0;
		if (prefix != null) {
			if (tableName.startsWith(prefix) == true) {
				i = prefix.length();
			}
		}
		if (suffix != null) {
			if (tableName.endsWith(suffix) == true) {
				len -= suffix.length();
			}
		}

		if (splitCamelCase == true) {
			boolean toUpper = true;
			for (; i < len; i++) {
				char c = tableName.charAt(i);
				if (c == separatorChar) {
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

		return tableName.substring(i, len);
	}
}