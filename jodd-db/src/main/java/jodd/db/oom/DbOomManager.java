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
import jodd.db.oom.sqlgen.SqlGenConfig;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * DbOom manager. Contains various global settings, DB-OOM (database - object-oriented)
 * mapping definitions, naming conventions etc.
 * <p>
 * Important: configure settings before entity registration!
 * <p>
 * Mapping definitions are used <b>only</b> by a result set mapper (such as {@link jodd.db.oom.mapper.ResultSetMapper}
 * to lookup for an entity from table name. Table names are read from result-set meta data, for example.
 * Moreover, it is not needed to use mappings at all: in that case just provide entity types during result set to
 * objects conversion.
 *
 * @see jodd.db.DbManager
 */
public class DbOomManager {

	private static final Logger log = LoggerFactory.getLogger(DbOomManager.class);

	// ---------------------------------------------------------------- singleton

	private static DbOomManager dbOomManager = new DbOomManager();

	/**
	 * Returns DbOom manager instance.
	 */
	public static DbOomManager getInstance() {
		return dbOomManager;
	}

	/**
	 * Sets new DbOom manager.
	 */
	public static void setInstance(DbOomManager oomManager) {
		dbOomManager = oomManager;
	}

	/**
	 * Resets DbOom Manager to defaults.
	 * It's done by simply creating a new instance
	 * of DbOom manager.
	 */
	public static void resetAll() {
		dbOomManager = new DbOomManager();
	}

	// ---------------------------------------------------------------- naming

	protected String schemaName;
	protected TableNamingStrategy tableNames = new TableNamingStrategy();
	protected ColumnNamingStrategy columnNames = new ColumnNamingStrategy();

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

	// ---------------------------------------------------------------- registration

	protected String[] primitiveEntitiesPrefixes = new String[] {
			"java.lang.",
			"jodd.mutable.",
			int.class.getName(),
			long.class.getName(),
			float.class.getName(),
			double.class.getName(),
			short.class.getName(),
			boolean.class.getName(),
			byte.class.getName()};

	public String[] getPrimitiveEntitiesPrefixes() {
		return primitiveEntitiesPrefixes;
	}

	/**
	 * Specifies array of class name prefixes that will be mapped directly to column.
	 */
	public void setPrimitiveEntitiesPrefixes(String... primitiveEntitiesPrefixes) {
		this.primitiveEntitiesPrefixes = primitiveEntitiesPrefixes;
	}

	protected Map<Class, DbEntityDescriptor> descriptorsMap = new HashMap<>();
	protected Map<String, DbEntityDescriptor> entityNamesMap = new HashMap<>();
	protected Map<String, DbEntityDescriptor> tableNamesMap = new NamedValuesHashMap<>();

	/**
	 * Lookups {@link DbEntityDescriptor} for some type and registers the type if is new.
	 * <p>
	 * Returns <code>null</code> for core classes from <code>java</code> run-time packages!
	 * Some types are <b>not</b> entities, i.e. domain objects. Instead, primitive entities
	 * are simply mapped to one column. 
	 */
	public <E> DbEntityDescriptor<E> lookupType(Class<E> type) {
		String typeName = type.getName();
		if (StringUtil.startsWithOne(typeName, primitiveEntitiesPrefixes) != -1) {
			return null;
		}
		DbEntityDescriptor<E> ded = descriptorsMap.get(type);
		if (ded == null) {
			ded = registerType(type);
		}
		return ded;
	}

	/**
	 * Returns <code>true</code> if type is registered withing manager.
	 */
	public boolean isRegistered(Class type) {
		return descriptorsMap.containsKey(type);
	}


	/**
	 * Lookups for {@link jodd.db.oom.DbEntityDescriptor} that was registered with this manager.
	 * Returns <code>null</code> if name not found.
	 */
	public DbEntityDescriptor lookupName(String typeName) {
		return entityNamesMap.get(typeName);
	}

	/**
	 * Lookups for {@link jodd.db.oom.DbEntityDescriptor} that was registered with this manager.
	 * Returns <code>null</code> if table name not found. Lookup is case-insensitive.
	 */
	public DbEntityDescriptor lookupTableName(String tableName) {
		return tableNamesMap.get(tableName);
	}

	/**
	 * Registers just type and entity names. Enough for most usages.
	 */
	public <E> DbEntityDescriptor<E> registerType(Class<E> type) {
		DbEntityDescriptor<E> ded = createDbEntityDescriptor(type);
		DbEntityDescriptor<E> existing = descriptorsMap.put(type, ded);

		if (log.isDebugEnabled()) {
			log.debug("Register " + type.getName() + " as " + ded.getTableName());
		}

		if (existing != null) {
			if (ded.getType() == type) {
				return ded;
			}
			throw new DbOomException("Type already registered: " + existing.getType());
		}

		existing = entityNamesMap.put(ded.getEntityName(), ded);

		if (existing != null) {
			throw new DbOomException("Name '" + ded.getEntityName() + "' already mapped to an entity: " + existing.getType());
		}
		return ded;
	}

	/**
	 * Registers entity. {@link #registerType(Class) Registers types} and table names.
	 */
	public <E> DbEntityDescriptor<E> registerEntity(Class<E> type) {
		DbEntityDescriptor<E> ded = registerType(type);
		DbEntityDescriptor existing = tableNamesMap.put(ded.getTableName(), ded);

		if (existing != null) {
			if (ded.getType() == type) {
				return ded;
			}
			throw new DbOomException("Entity registration failed! Table '" + ded.getTableName() + "' already mapped to an entity: " + existing.getType());
		}
		return ded;
	}

	/**
	 * Registers entity. Existing entity will be removed if exist, so no exception will be thrown. 
	 */
	public <E> DbEntityDescriptor<E> registerEntity(Class<E> type, boolean force) {
		if (force) {
			removeEntity(type);
		}
		return registerEntity(type);
	}

	/**
	 * Removes entity and returns removed descriptor.
	 */
	public <E> DbEntityDescriptor<E> removeEntity(Class<E> type) {
		DbEntityDescriptor<E> ded = descriptorsMap.remove(type);
		if (ded == null) {
			ded = createDbEntityDescriptor(type);
		}
		entityNamesMap.remove(ded.getEntityName());
		tableNamesMap.remove(ded.getTableName());
		return ded;
	}


	/**
	 * Creates {@link DbEntityDescriptor}.
	 */
	protected <E> DbEntityDescriptor<E> createDbEntityDescriptor(Class<E> type) {
		return new DbEntityDescriptor<>(type, schemaName, tableNames, columnNames);
	}


	// ---------------------------------------------------------------- stats

	/**
	 * Returns total number of registered entity names.
	 */
	public int getTotalNames() {
		return entityNamesMap.size();
	}

	/**
	 * Returns total number of registered table names.
	 */
	public int getTotalTableNames() {
		return tableNamesMap.size();
	}

	/**
	 * Returns total number of registered types.
	 */
	public int getTotalTypes() {
		return descriptorsMap.size();
	}

	/**
	 * Resets the manager and clears descriptors maps.
	 * The configuration is not changed, just table-related
	 * data is cleared. To reset all, call {@link #resetAll()}.
	 */
	public void reset() {
		descriptorsMap.clear();
		entityNamesMap.clear();
		tableNamesMap.clear();
	}


	// ---------------------------------------------------------------- table separators

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


	// ---------------------------------------------------------------- hint resolver

	protected JoinHintResolver hintResolver = new JoinHintResolver();

	public JoinHintResolver getHintResolver() {
		return hintResolver;
	}

	/**
	 * Specifies hint resolver.
	 */
	public void setHintResolver(JoinHintResolver hintResolver) {
		this.hintResolver = hintResolver;
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


	// ---------------------------------------------------------------- result set mapper

	protected boolean cacheEntitiesInResultSet;

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

	protected boolean entityAwareMode;

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

	// ---------------------------------------------------------------- create entity

	/**
	 * Creates new entity instances.
	 */
	public <E> E createEntityInstance(Class<E> type) {
		try {
			return type.newInstance();
		} catch (Exception ex) {
			throw new DbOomException(ex);
		}
	}


	// ---------------------------------------------------------------- sqlgenconfig

	private final SqlGenConfig sqlGenConfig = new SqlGenConfig();

	/**
	 * Returns {@link SqlGenConfig}.
	 */
	public SqlGenConfig getSqlGenConfig() {
		return sqlGenConfig;
	}

}