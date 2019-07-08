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

package jodd.db.oom.sqlgen;

import jodd.db.DbOom;
import jodd.db.oom.ColumnAliasType;
import jodd.db.oom.ColumnData;
import jodd.db.oom.DbEntityColumnDescriptor;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.NamedValuesHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Template common data used during template parsing.
 * Extracted to super class for better visibility.
 */
public abstract class TemplateData {

	private static final String COL_CODE_PREFIX = "col_";
	protected final DbEntityManager entityManager;
	protected final ColumnAliasType defaultColumnAliasType;

	protected TemplateData(final DbOom dbOom) {
		this.entityManager = dbOom.entityManager();
		this.columnAliasType = defaultColumnAliasType = dbOom.config().getDefaultColumnAliasType();
	}

	/**
	 * Resets the builder so it can be used again. Not everything is reset:
	 * object references and column alias type is not.
	 */
	protected void resetSoft() {
		columnCount = 0;
		paramCount = 0;
		hintCount = 0;

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
		//columnAliasType = defaultColumnAliasType;
	}

	protected void resetHard() {
		resetSoft();
		objectRefs = null;
		columnAliasType = defaultColumnAliasType;
	}


	// ---------------------------------------------------------------- object refs

	protected Map<String, Object> objectRefs;             // used object references

	/**
	 * Saves object reference.
	 */
	public void setObjectReference(final String name, final Object object) {
		if (objectRefs == null) {
			objectRefs = new HashMap<>();
		}
		objectRefs.put(name, object);
	}

	/**
	 * Returns object reference.
	 */
	public Object getObjectReference(final String name) {
		if (objectRefs == null) {
			return null;
		}
		return objectRefs.get(name);
	}

	/**
	 * Lookups for object reference and throws an exception if reference doesn't exist.
	 */
	public Object lookupObject(final String ref) {
		Object value = getObjectReference(ref);
		if (value == null) {
			throw new DbSqlBuilderException("Invalid object reference: " + ref);
		}
		return value;
	}

	// ---------------------------------------------------------------- tables

	private static final class TableRefData {
		final String alias;
		final DbEntityDescriptor desc;

		private TableRefData(final DbEntityDescriptor desc, final String alias) {
			this.alias = alias;
			this.desc = desc;
		}
	}

	protected Map<String, TableRefData> tableRefs;

	/**
	 * Returns entity descriptor for provided table reference.
	 */
	public DbEntityDescriptor getTableDescriptor(final String tableRef) {
		if (tableRefs == null) {
			return null;
		}
		TableRefData t = tableRefs.get(tableRef);
		return t == null ? null : t.desc;
	}

	/**
	 * Finds entity descriptor of a table that contains provided column reference.
	 */
	public DbEntityDescriptor findTableDescriptorByColumnRef(final String columnRef) {
		for (Map.Entry<String, TableRefData> entry : tableRefs.entrySet()) {
			DbEntityDescriptor ded = entry.getValue().desc;

			if (ded.findByPropertyName(columnRef) != null) {
				return ded;
			}
		}
		return null;
	}

	/**
	 * Returns table alias for provided table reference.
	 */
	public String getTableAlias(final String tableRef) {
		if (tableRefs == null) {
			return null;
		}
		TableRefData t = tableRefs.get(tableRef);
		return t == null ? null : t.alias;
	}

	/**
	 * Registers table reference for provided entity.
	 */
	public void registerTableReference(final String tableReference, final DbEntityDescriptor ded, final String tableAlias) {
		if (tableRefs == null) {
			tableRefs = new HashMap<>();
		}
		TableRefData t = new TableRefData(ded, tableAlias);
		if (tableRefs.put(tableReference, t) != null) {
			throw new DbSqlBuilderException("Duplicated table reference: " + tableReference);
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


	public void registerColumnDataForTableRef(final String tableRef, final String tableName) {
		if (columnData == null) {
			columnData = new NamedValuesHashMap<>();
		}
		columnData.put(tableRef, new ColumnData(tableName));
	}

	public String registerColumnDataForColumnCode(final String tableName, final String column) {
		if (columnData == null) {
			columnData = new NamedValuesHashMap<>();
		}
		String columnCode = COL_CODE_PREFIX + Integer.toString(columnCount++) + '_';
		columnData.put(columnCode, new ColumnData(tableName, column));
		return columnCode;
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
	public void addParameter(final String name, final Object value, final DbEntityColumnDescriptor dec) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		parameters.put(name, new ParameterValue(value, dec));
	}


	// ---------------------------------------------------------------- lookup


	/**
	 * Lookups for entity name and throws exception if entity name not found.
	 */
	protected DbEntityDescriptor lookupName(final String entityName) {
		DbEntityDescriptor ded = entityManager.lookupName(entityName);
		if (ded == null) {
			throw new DbSqlBuilderException("Entity name not registered: " + entityName);
		}
		return ded;
	}

	/**
	 * Lookups for entity name and throws an exception if entity type is invalid.
	 */
	protected DbEntityDescriptor lookupType(final Class entity) {
		DbEntityDescriptor ded = entityManager.lookupType(entity);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid or not-persistent entity type: " + entity.getName());
		}
		return ded;
	}

	/**
	 * Lookups for table reference and throws an exception if table reference not found.
	 */
	protected DbEntityDescriptor lookupTableRef(final String tableRef) {
		DbEntityDescriptor ded = getTableDescriptor(tableRef);
		if (ded == null) {
			throw new DbSqlBuilderException("Table reference not used in this query: " + tableRef);
		}
		return ded;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Defines parameter with name and its value.
	 */
	protected void defineParameter(final StringBuilder query, String name, final Object value) {
		if (name == null) {
			name = getNextParameterName();
		}
		query.append(':').append(name);
		addParameter(name, value, null);
	}


	// ---------------------------------------------------------------- hints

	protected int hintCount;

	protected List<String> hints;

	/**
	 * Registers a hint.
	 */
	public void registerHint(final String hint) {
		if (hints == null) {
			hints = new ArrayList<>(hintCount);
		}
		hints.add(hint);
	}

	/**
	 * Increments hints count.
	 */
	public void incrementHintsCount() {
		hintCount++;
	}

	/**
	 * Returns <code>true</code> if there are hints.
	 */
	public boolean hasHints() {
		return hintCount > 0;
	}


	// ---------------------------------------------------------------- last column

	public DbEntityColumnDescriptor lastColumnDec;

}