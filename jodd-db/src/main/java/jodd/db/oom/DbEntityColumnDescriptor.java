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

import jodd.db.type.SqlType;
import jodd.util.StringUtil;

/**
 * Column descriptors.
 */
public class DbEntityColumnDescriptor implements Comparable {

	protected final DbEntityDescriptor dbEntityDescriptor;
	protected final String columnName;
	protected final String columnNameForQuery;
	protected final String propertyName;
	protected final Class propertyType;
	protected final boolean isId;
	protected final Class<? extends SqlType> sqlTypeClass;
	protected int dbSqlType = SqlType.DB_SQLTYPE_UNKNOWN;

	public DbEntityColumnDescriptor(
			final DbEntityDescriptor ded,
			final String columnName,
			final String fieldName,
			final Class fieldType,
			final boolean isId,
			final Class<? extends SqlType> sqlTypeClass) {

		this.dbEntityDescriptor = ded;
		this.propertyName = fieldName;
		this.propertyType = fieldType;
		this.isId = isId;
		this.sqlTypeClass = sqlTypeClass;

		this.columnNameForQuery = columnName;

		if (StringUtil.detectQuoteChar(columnNameForQuery) != 0) {
			this.columnName = StringUtil.substring(columnNameForQuery, 1, -1);
		}
		else {
			this.columnName = columnNameForQuery;
		}
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
	 * Returns column name to be used in the generated sql.
	 */
	public String getColumnNameForQuery() {
		return columnNameForQuery;
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
	public void updateDbSqlType(final int dbSqlType) {
		if (this.dbSqlType == SqlType.DB_SQLTYPE_UNKNOWN) {
			this.dbSqlType = dbSqlType;
		}
	}

	// ---------------------------------------------------------------- comparable

	/**
	 * Compares two column descriptors. Identity columns should be the first on the list.
	 * Each group then will be sorted by column name.
	 */
	@Override
	public int compareTo(final Object o) {
		DbEntityColumnDescriptor that = (DbEntityColumnDescriptor) o;
		if (this.isId != that.isId) {
			return this.isId ? -1 : 1;      // IDs should be the first in the array
		}
		return this.columnName.compareTo(that.columnName);
	}

	// ---------------------------------------------------------------- equals and hash

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (this.getClass() != o.getClass()) {
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
