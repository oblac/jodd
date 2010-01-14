// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.type.SqlType;

/**
 * Column descriptors.
 */
public class DbEntityColumnDescriptor implements Comparable {

	protected final String columnName;
	protected final String propertyName;
	protected final Class propertyType;
	protected final boolean isId;
	protected final Class<? extends SqlType> sqlTypeClass;

	public DbEntityColumnDescriptor(String columnName, String fieldName, Class fieldType, boolean isId, Class<? extends SqlType> sqlTypeClass) {
		this.columnName = columnName;
		this.propertyName = fieldName;
		this.propertyType = fieldType;
		this.isId = isId;
		this.sqlTypeClass = sqlTypeClass;
	}

	/**
	 * Returns column name.
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Returns property name.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Returns property type.
	 */
	public Class getPropertyType() {
		return propertyType;
	}

	/**
	 * Returns <code>true</code> if column is ID column.
	 */
	public boolean isId() {
		return isId;
	}

	/**
	 * Returns SqlType or <code>null</code> for default type.
	 */
	public Class<? extends SqlType> getSqlTypeClass() {
		return sqlTypeClass;
	}

	// ---------------------------------------------------------------- comparable

	/**
	 * Compares two column descriptors. Identity columns should be the first on the list.
	 * Each group then will be sorted by column name.
	 */
	public int compareTo(Object o) {
		DbEntityColumnDescriptor that = (DbEntityColumnDescriptor) o;
		if (this.isId != that.isId) {
			return this.isId == true ? -1 : 1;      // IDs should be the first in the array
		}
		return this.columnName.compareTo(that.columnName);
	}

	// ---------------------------------------------------------------- equals and hash

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DbEntityColumnDescriptor)) {
			return false;
		}
		DbEntityColumnDescriptor that = (DbEntityColumnDescriptor) o;
		return columnName.equals(that.columnName);
	}

	@Override
	public int hashCode() {
		return columnName.hashCode();
	}

	// ---------------------------------------------------------------- to string


	@Override
	public String toString() {
		return "DbEntityColumnDescriptor{" +
				"columnName='" + columnName + '\'' +
				", propertyName='" + propertyName + '\'' +
				", isId=" + isId +
				'}';
	}
}
