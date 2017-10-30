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

import jodd.db.oom.naming.ColumnNamingStrategy;
import jodd.db.oom.naming.TableNamingStrategy;

public class DbOomConfig {

	protected String columnAliasSeparator = "$";

	/**
	 * Returns value for separator for column aliases that divides table reference and column name.
	 */
	public String getColumnAliasSeparator() {
		return columnAliasSeparator;
	}

	/**
	 * Specifies separator for column aliases that divides table reference and column name.
	 * Separator should contains of characters that are not used in table names, such as:
	 * '$' or '__'.
	 */
	public void setColumnAliasSeparator(String separator) {
		this.columnAliasSeparator = separator;
	}


	// ---------------------------------------------------------------- result set mapper

	protected boolean cacheEntitiesInResultSet = false;

	public boolean isCacheEntitiesInResultSet() {
		return cacheEntitiesInResultSet;
	}

	/**
	 * Defines if entities have to be cached in result set.
	 * When cached, more memory is consumed during the existence of
	 * {@link jodd.db.oom.mapper.ResultSetMapper}.
	 */
	public void setCacheEntitiesInResultSet(boolean cacheEntitiesInResultSet) {
		this.cacheEntitiesInResultSet = cacheEntitiesInResultSet;
	}

	// ---------------------------------------------------------------- db list

	protected boolean entityAwareMode = false;

	/**
	 * Returns <code>true</code> if entity-aware mode is enabled.
	 */
	public boolean isEntityAwareMode() {
		return entityAwareMode;
	}

	/**
	 * Defines entity-aware mode, when resulting collections does not have duplicates.
	 * It make sense to enable it only if {@link #setCacheEntitiesInResultSet(boolean) cache} is set.
	 * Therefore, enabling smart mode will also enable caching.
	 */
	public void setEntityAwareMode(boolean entityAwareMode) {
		if (entityAwareMode) {
			this.cacheEntitiesInResultSet = true;
		}
		this.entityAwareMode = entityAwareMode;
	}

	// ---------------------------------------------------------------- default column alias type

	protected ColumnAliasType defaultColumnAliasType;

	public ColumnAliasType getDefaultColumnAliasType() {
		return defaultColumnAliasType;
	}

	/**
	 * Specifies default column alias type.
	 */
	public void setDefaultColumnAliasType(ColumnAliasType defaultColumnAliasType) {
		this.defaultColumnAliasType = defaultColumnAliasType;
	}

	// ---------------------------------------------------------------- naming

	protected String schemaName;
	protected TableNamingStrategy tableNames = new TableNamingStrategy();
	protected ColumnNamingStrategy columnNames = new ColumnNamingStrategy();

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * Returns current table name strategy.
	 */
	public TableNamingStrategy getTableNames() {
		return tableNames;
	}

	/**
	 * Sets new table name strategy.
	 */
	public void setTableNames(TableNamingStrategy tableNames) {
		this.tableNames = tableNames;
	}

	/**
	 * Returns current column name strategy.
	 */
	public ColumnNamingStrategy getColumnNames() {
		return columnNames;
	}

	/**
	 * Sets new column name strategy,
	 */
	public void setColumnNames(ColumnNamingStrategy columnNames) {
		this.columnNames = columnNames;
	}

}
