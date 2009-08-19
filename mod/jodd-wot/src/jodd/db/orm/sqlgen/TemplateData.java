// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import jodd.db.orm.DbOrmManager;
import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.ColumnData;
import jodd.db.orm.ColumnAliasType;
import jodd.db.orm.DbEntityColumnDescriptor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Template common data used during template parsing.
 * Extracted to super class for better visibility.
 */
public abstract class TemplateData {

	private static final String COL_CODE_PREFIX = "col_";
	
	public final DbOrmManager dbOrmManager;

	protected TemplateData() {
		dbOrmManager = DbOrmManager.getInstance();
		columnAliasType = dbOrmManager.getDefaultColumnAliasType();
	}

	/**
	 * Returns associated {@link jodd.db.orm.DbOrmManager}.
	 */
	public DbOrmManager getDbOrmManager() {
		return dbOrmManager;
	}

	/**
	 * Resets the builde before initializing and processing.
	 */
	protected void resetOnPreInit() {
		columnCount = 0;
		paramCount = 0;
		hintCount = 0;
	}

	/**
	 * Resets the builder so it can be used again. Note that
	 * object references are not cleared.
	 */
	protected void resetAll() {
		resetOnPreInit();
		if (tableRefs != null) {
			tableRefs.clear();
		}
//		objectRefs = null;
		if (columnData != null) {
			columnData.clear();
		}
		if (parameters != null) {
			parameters.clear();
		}
		if (hints != null) {
			hints.clear();
		}
		columnAliasType = dbOrmManager.getDefaultColumnAliasType();
	}


	// ---------------------------------------------------------------- object refs

	protected Map<String, Object> objectRefs;             // used object references

	/**
	 * Saves object reference.
	 */
	public void setObjectReference(String name, Object object) {
		if (objectRefs == null) {
			objectRefs = new HashMap<String, Object>();
		}
		objectRefs.put(name, object);
	}

	/**
	 * Returns object reference.
	 */
	public Object getObjectReference(String name) {
		if (objectRefs == null) {
			return null;
		}
		return objectRefs.get(name);
	}

	/**
	 * Lookups for object reference and throws an exception if reference doesn't exist.
	 */
	public Object lookupObject(String ref) {
		Object value = getObjectReference(ref);
		if (value == null) {
			throw new DbSqlBuilderException("Invalid object reference: '" + ref + "'.");
		}
		return value;
	}

	// ---------------------------------------------------------------- tables

	private static final class TableRefData {
		final String alias;
		final DbEntityDescriptor desc;

		private TableRefData(DbEntityDescriptor desc, String alias) {
			this.alias = alias;
			this.desc = desc;
		}
	}

	protected Map<String, TableRefData> tableRefs;

	/**
	 * Returns entity descriptor for provided table reference.
	 */
	public DbEntityDescriptor getTableDescriptor(String tableRef) {
		if (tableRefs == null) {
			return null;
		}
		TableRefData t = tableRefs.get(tableRef);
		return t == null ? null : t.desc;
	}

	/**
	 * Returns table alias for provided table reference.
	 */
	public String getTableAlias(String tableRef) {
		if (tableRefs == null) {
			return null;
		}
		TableRefData t = tableRefs.get(tableRef);
		return t == null ? null : t.alias;
	}

	/**
	 * Registers table reference for provided entity.
	 */
	public void registerTableReference(String tableReference, DbEntityDescriptor ded, String tableAlias) {
		if (tableRefs == null) {
			tableRefs = new HashMap<String, TableRefData>();
		}
		TableRefData t = new TableRefData(ded, tableAlias);
		if (tableRefs.put(tableReference, t) != null) {
			throw new DbSqlBuilderException("Duplicated table reference detected: '" + tableReference + "'.");
		}
	}


	// ---------------------------------------------------------------- column data

	/**
	 * Column or table aliases.
	 */
	protected Map<String, ColumnData> columnData;

	/**
	 * Column counter for COLUMN_CODE column alias type.
	 */
	protected int columnCount;


	/**
	 * Specifies column alias type. May be <code>null</code> when column aliases are not used.
	 */
	protected ColumnAliasType columnAliasType;

	/**
	 * Returns column alias type.
	 */
	public ColumnAliasType getColumnAliasType() {
		return columnAliasType;
	}


	public void registerColumnDataForTableRef(String tableRef, String tableName) {
		if (columnData == null) {
			columnData = new HashMap<String, ColumnData>();
		}
		columnData.put(tableRef, new ColumnData(tableName));
	}

	public String registerColumnDataForColumnCode(String tableName, String column) {
		if (columnData == null) {
			columnData = new HashMap<String, ColumnData>();
		}
		String code = COL_CODE_PREFIX + Integer.toString(columnCount++) + '_';
		columnData.put(code, new ColumnData(tableName, column));
		return code;
	}


	// ---------------------------------------------------------------- parameters

	protected Map<String, ParameterValue> parameters;

	protected int paramCount;

	/**
	 * Returns the next auto-generated parameter name.
	 */
	public String getNextParameterName() {
		return "p" + (paramCount++);
	}

	/**
	 * Adds query parameter.
	 */
	public void addParameter(String name, Object value, DbEntityColumnDescriptor dec) {
		if (parameters == null) {
			parameters = new HashMap<String, ParameterValue>();
		}
		parameters.put(name, new ParameterValue(value, dec));
	}


	// ---------------------------------------------------------------- lookup


	/**
	 * Lookups for entity name and throws exception if entity name not found.
	 */
	protected DbEntityDescriptor lookupName(String entityName) {
		DbEntityDescriptor ded = dbOrmManager.lookupName(entityName);
		if (ded == null) {
			throw new DbSqlBuilderException("Entity name '" + entityName + "' is not registered with DbOrmManager.");
		}
		return ded;
	}

	/**
	 * Lookups for entity name and throws an exception if entity type is invalid.
	 */
	protected DbEntityDescriptor lookupType(Class entity) {
		DbEntityDescriptor ded = dbOrmManager.lookupType(entity);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid or not-persistent entity type: '" + entity.getName() + "'.");
		}
		return ded;
	}

	/**
	 * Lookups for table reference and throws an exception if table reference not found.
	 */
	protected DbEntityDescriptor lookupTableRef(String tableRef) {
		DbEntityDescriptor ded = getTableDescriptor(tableRef);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid table reference: '" + tableRef + "', not used in this query.");
		}
		return ded;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Defines parameter with name and its value.
	 */
	protected void defineParameter(StringBuilder query, String name, Object value) {
		if (name == null) {
			name = getNextParameterName();
		}
		query.append(':').append(name);
		addParameter(name, value, null);
	}


	// ---------------------------------------------------------------- hints

	public int hintCount;

	protected List<String> hints;

	/**
	 * Registers a hint.
	 */
	public void registerHint(String hint) {
		if (hints == null) {
			hints = new ArrayList<String>(hintCount);
		}
		hints.add(hint);
	}


}