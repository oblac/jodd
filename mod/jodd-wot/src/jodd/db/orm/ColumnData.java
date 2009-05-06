// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

/**
 * Column data.
 */
public class ColumnData {

	private final String tableName;
	private final String columnName;

	public ColumnData(String tableName) {
		this.tableName = tableName;
		this.columnName = null;
	}

	public ColumnData(String tableName, String columnName) {
		this.tableName = tableName;
		this.columnName = columnName;
	}

	/**
	 * Returns table name for this column.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns column name for this column.
	 */
	public String getColumnName() {
		return columnName;
	}

}
