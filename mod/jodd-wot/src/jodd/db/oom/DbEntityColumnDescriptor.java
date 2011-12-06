// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.type.SqlType;

/**
 * Column descriptors.
 */
public class DbEntityColumnDescriptor implements Comparable {

	protected final DbEntityDescriptor dbEntityDescriptor;
	protected final String columnName;
	protected final String propertyName;
	protected final Class propertyType;
	protected final boolean isId;
	protected final Class<? extends SqlType> sqlTypeClass;
	protected int dbSqlType = Integer.MAX_VALUE;

	public DbEntityColumnDescriptor(DbEntityDescriptor ded, String columnName, String fieldName, Class fieldType, boolean isId, Class<? extends SqlType> sqlTypeClass) {
		this.dbEntityDescriptor = ded;
		this.columnName = columnName;
		this.propertyName = fieldName;
		this.propertyType = fieldType;
		this.isId = isId;
		this.sqlTypeClass = sqlTypeClass;
	}

	/**
	 * Returns {@link jodd.db.oom.DbEntityDescriptor} that this column description belongs to.
	 */
	public DbEntityDescriptor getDbEntityDescriptor() {
		return dbEntityDescriptor;
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

	/**
	 * Returns db sql type. Important: available only after first saving of column value
	 * to database! 
	 */
	public int getDbSqlType() {
		return dbSqlType;
	}

	/**
	 * Updates db sql type if not already set.
	 */
	public void updateDbSqlType(int dbSqlType) {
		if (dbSqlType == Integer.MAX_VALUE) {
			this.dbSqlType = dbSqlType;
		}
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
