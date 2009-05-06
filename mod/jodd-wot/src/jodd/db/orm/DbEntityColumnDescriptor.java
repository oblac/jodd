// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

/**
 * Column descriptors.
 */
public class DbEntityColumnDescriptor implements Comparable {

	protected final String columnName;
	protected final String propertyName;
	protected final Class propertyType;
	protected final boolean isId;

	public DbEntityColumnDescriptor(String columnName, String fieldName, Class fieldType, boolean isId) {
		this.columnName = columnName;
		this.propertyName = fieldName;
		this.propertyType = fieldType;
		this.isId = isId;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Class getPropertyType() {
		return propertyType;
	}

	public boolean isId() {
		return isId;
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
