// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.mapper.DefaultResultSetMapper;
import jodd.db.oom.mapper.ResultSetMapper;
import jodd.db.oom.naming.ColumnNamingStrategy;
import jodd.db.oom.naming.TableNamingStrategy;
import jodd.util.StringUtil;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * DbOom manager. Contains various global settings, DB-OOM (database - object-oriented)
 * mapping definitions, naming conventions etc.
 * <p>
 * Mapping definitions are used <b>only</b> by a result set mapper (such as {@link jodd.db.oom.mapper.ResultSetMapper}
 * to lookup for an entity from table name. Table names are read from result-set meta data, for example.
 * Moreover, it is not needed to use mappings at all: in that case just provide entity types during result set to
 * objects conversion.
 */
public class DbOomManager {

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

	// ---------------------------------------------------------------- prefix & suffix

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

	protected Map<Class, DbEntityDescriptor> descriptorsMap = new HashMap<Class, DbEntityDescriptor>();
	protected Map<String, DbEntityDescriptor> entityNamesMap = new HashMap<String, DbEntityDescriptor>();
	protected Map<String, DbEntityDescriptor> tableNamesMap = new HashMap<String, DbEntityDescriptor>();

	/**
	 * Lookups {@link DbEntityDescriptor} for some type and registers the type if is new.
	 * <p>
	 * Returns <code>null</code> for core classes from <code>java</code> run-time packages!
	 * Some types are <b>not</b> entities, i.e. domain objects. Instead, primitive entities
	 * are simply mapped to one column. 
	 */
	public DbEntityDescriptor lookupType(Class type) {
		String typeName = type.getName();
		if (StringUtil.startsWithOne(typeName, primitiveEntitiesPrefixes) != -1) {
			return null;
		}
		DbEntityDescriptor ded = descriptorsMap.get(type);
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
	 * Returns <code>null</code> if table name not found.
	 */
	public DbEntityDescriptor lookupTableName(String typeName) {
		return tableNamesMap.get(typeName);
	}

	/**
	 * Registers just type and entity names. Enough for most usages.
	 */
	public DbEntityDescriptor registerType(Class type) {
		DbEntityDescriptor ded = createDbEntityDescriptor(type);
		DbEntityDescriptor existing = descriptorsMap.put(type, ded);
		if (existing != null) {
			throw new DbOomException("Type registration failed! Type '" + existing.getType() + "' already registered.");
		}
		existing = entityNamesMap.put(ded.getEntityName(), ded);
		if (existing != null) {
			throw new DbOomException("Type registration failed! Name '" + ded.getEntityName() + "' already mapped to an entity class: " + existing.getType());
		}
		return ded;
	}

	/**
	 * Registers entity. {@link #registerType(Class) Registers types} and table names.
	 * Throw exception is type is already registered.
	 */
	public DbEntityDescriptor registerEntity(Class type) {
		DbEntityDescriptor ded = registerType(type);
		DbEntityDescriptor existing = tableNamesMap.put(ded.getTableName(), ded);
		if (existing != null) {
			throw new DbOomException("Entity registration failed! Table '" + ded.getTableName() + "' already mapped to an entity class: " + existing.getType());
		}
		return ded;
	}

	/**
	 * Registers entity. Existing entity will be removed if exist, so no exception will be thrown. 
	 */
	public DbEntityDescriptor registerEntity(Class type, boolean force) {
		if (force == true) {
			removeEntity(type);
		}
		return registerEntity(type);
	}

	/**
	 * Removes entity.
	 */
	public DbEntityDescriptor removeEntity(Class type) {
		DbEntityDescriptor ded = descriptorsMap.remove(type);
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
	protected DbEntityDescriptor createDbEntityDescriptor(Class type) {
		return new DbEntityDescriptor(type, schemaName, tableNames, columnNames);
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

	/**
	 * Creates a new instance of {@link jodd.db.oom.mapper.ResultSetMapper}.
	 */
	public ResultSetMapper createResultSetMapper(ResultSet resultSet, Map<String, ColumnData> columnAliases) {
		return new DefaultResultSetMapper(resultSet, columnAliases, this);
	}

	// ---------------------------------------------------------------- create entity

	/**
	 * Creates new entity instances.
	 */
	public <E> E createEntityInstance(Class<E> type) {
		try {
			return type.newInstance();
		} catch (Exception ex) {
			throw new DbOomException("Unable to create new entity instance using default constructor for type: " + type, ex);
		}
	}

}