// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.orm.mapper.DefaultResultSetMapper;
import jodd.db.orm.mapper.ResultSetMapper;
import jodd.util.StringUtil;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Global DB-ORM mapping definitions, prefixes and cache.
 * <p>
 * Mapping definitions are used <b>only</b> by a result set mapper (such as {@link jodd.db.orm.mapper.ResultSetMapper}
 * to lookup for an entity from table name. Table names are read from result-set meta data, for example.
 * Moreover, it is not needed to use mappings at all: in that case just provide entity types during result set to
 * objects conversion.
 */
public class DbOrmManager {

	protected DbOrmManager() {}

	// ---------------------------------------------------------------- singleton

	private static DbOrmManager dbOrmManager = new DbOrmManager();

	/**
	 * Returns current DB-ORM manager instance.
	 */
	public static DbOrmManager getInstance() {
		return dbOrmManager;
	}

	/**
	 * Sets new instance for DB-ORM manager.
	 */
	public static void setInstance(DbOrmManager ormManager) {
		dbOrmManager = ormManager;
	}


	// ---------------------------------------------------------------- prefix & suffix

	protected boolean tableNameUppercase = true;
	protected boolean columnNameUppercase = true;
	protected String tableNamePrefix;
	protected String tableNameSuffix;
	protected String schemaName;


	/**
	 * Specifies if table names are in upper case.
	 */
	public void setTableNameUppercase(boolean tableNameUppercase) {
		this.tableNameUppercase = tableNameUppercase;
	}

	/**
	 * Returns <code>true</code> if table names are uppercase.
	 */
	public boolean isTableNameUppercase() {
		return tableNameUppercase;
	}

	/**
	 * Specifies if column names are in upper case.
	 */
	public void setColumnNameUppercase(boolean columnNameUppercase) {
		this.columnNameUppercase = columnNameUppercase;
	}

	/**
	 * Returns <code>true</code> if column names are uppercase.
	 */
	public boolean isColumnNameUppercase() {
		return columnNameUppercase;
	}

	/**
	 * Specifies default table prefix for all tables. This prefix affect default
	 * conversions from bean name to table name and vice-versa when no annotations
	 * are used.
	 */
	public void setTableNamePrefix(String prefix) {
		this.tableNamePrefix = prefix;
	}

	/**
	 * Returns current table prefix.
	 */
	public String getTableNamePrefix() {
		return tableNamePrefix;
	}

	/**
	 * Returns table name suffix.
	 */
	public String getTableNameSuffix() {
		return tableNameSuffix;
	}

	/**
	 * Specifies default table name suffix.
	 */
	public void setTableNameSuffix(String suffix) {
		this.tableNameSuffix = suffix;
	}

	/**
	 * Returns default schema name.
	 */
	public String getSchemaName() {
		return schemaName;
	}
	/**
	 * Specifies default schema name.
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	// ---------------------------------------------------------------- registration

	protected String[] primitiveEntitiesPrefixes = new String[] {"java.lang.", "jodd.mutable.",
			int.class.getName(), long.class.getName(), float.class.getName(), double.class.getName(),
			short.class.getName(), boolean.class.getName(), byte.class.getName()};

	public String[] getPrimitiveEntitiesPrefixes() {
		return primitiveEntitiesPrefixes;
	}

	/**
	 * Specifies array of class name prefixes that will be mapped directly to column.
	 */
	public void setPrimitiveEntitiesPrefixes(String... primitiveEntitiesPrefixes) {
		this.primitiveEntitiesPrefixes = primitiveEntitiesPrefixes;
	}

	protected Map<Class, DbEntityDescriptor> descriptors = new HashMap<Class, DbEntityDescriptor>();
	protected Map<String, DbEntityDescriptor> entityNames = new HashMap<String, DbEntityDescriptor>();
	protected Map<String, DbEntityDescriptor> tableNames = new HashMap<String, DbEntityDescriptor>();

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
		DbEntityDescriptor ded = descriptors.get(type);
		if (ded == null) {
			ded = registerType(type);
		}
		return ded;
	}

	/**
	 * Returns <code>true</code> if type is registered withing manager.
	 */
	public boolean isRegistered(Class type) {
		return descriptors.containsKey(type);
	}


	/**
	 * Lookups for {@link jodd.db.orm.DbEntityDescriptor} that was registered with this manager.
	 * Returns <code>null</code> if name not found.
	 */
	public DbEntityDescriptor lookupName(String typeName) {
		return entityNames.get(typeName);
	}

	/**
	 * Lookups for {@link jodd.db.orm.DbEntityDescriptor} that was registered with this manager.
	 * Returns <code>null</code> if table name not found.
	 */
	public DbEntityDescriptor lookupTableName(String typeName) {
		return tableNames.get(typeName);
	}

	/**
	 * Registers just type and entity names. Enough for most usages.
	 */
	public DbEntityDescriptor registerType(Class type) {
		DbEntityDescriptor ded = createDbEntityDescriptor(type);
		DbEntityDescriptor existing = descriptors.put(type, ded);
		if (existing != null) {
			throw new DbOrmException("Type registration failed! Type '" + existing.getType() + "' already registered.");
		}
		existing = entityNames.put(ded.getEntityName(), ded);
		if (existing != null) {
			throw new DbOrmException("Type registration failed! Name '" + ded.getEntityName() + "' already mapped to an entity class: " + existing.getType());
		}
		return ded;
	}

	/**
	 * Registers entity. {@link #registerType(Class) Registers types} and table names.
	 * Throw exception is type is already registered.
	 */
	public DbEntityDescriptor registerEntity(Class type) {
		DbEntityDescriptor ded = registerType(type);
		DbEntityDescriptor existing = tableNames.put(ded.getTableName(), ded);
		if (existing != null) {
			throw new DbOrmException("Entity registration failed! Table '" + ded.getTableName() + "' already mapped to an entity class: " + existing.getType());
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
		DbEntityDescriptor ded = descriptors.remove(type);
		if (ded == null) {
			ded = createDbEntityDescriptor(type);
		}
		entityNames.remove(ded.getEntityName());
		tableNames.remove(ded.getTableName());
		return ded;
	}


	/**
	 * Creates {@link DbEntityDescriptor}.
	 */
	protected DbEntityDescriptor createDbEntityDescriptor(Class type) {
		return new DbEntityDescriptor(type, schemaName, tableNamePrefix, tableNameSuffix, tableNameUppercase, columnNameUppercase);
	}


	// ---------------------------------------------------------------- stats


	/**
	 * Returns total number of registered entity names.
	 */
	public int getTotalNames() {
		return entityNames.size();
	}

	/**
	 * Returns total number of registered table names.
	 */
	public int getTotalTableNames() {
		return tableNames.size();
	}

	/**
	 * Returns total number of registered types.
	 */
	public int getTotalTypes() {
		return descriptors.size();
	}

	/**
	 * Resets the manager and clears descriptors cache.
	 */
	public void reset() {
		descriptors.clear();
		entityNames.clear();
		tableNames.clear();
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

	public void setHintResolver(JoinHintResolver hintResolver) {
		this.hintResolver = hintResolver;
	}


	// ---------------------------------------------------------------- default column alias type

	protected ColumnAliasType defaultColumnAliasType;

	public ColumnAliasType getDefaultColumnAliasType() {
		return defaultColumnAliasType;
	}

	public void setDefaultColumnAliasType(ColumnAliasType defaultColumnAliasType) {
		this.defaultColumnAliasType = defaultColumnAliasType;
	}


	// ---------------------------------------------------------------- result set mapper

	/**
	 * Creates a new instance of {@link jodd.db.orm.mapper.ResultSetMapper}.
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
			throw new DbOrmException("Unable to create new entity instance using default constructor for type '" + type + "'.", ex);
		}
	}

}