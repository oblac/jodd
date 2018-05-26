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

import jodd.cache.TypeCache;
import jodd.db.oom.naming.ColumnNamingStrategy;
import jodd.db.oom.naming.TableNamingStrategy;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.util.ClassUtil;
import jodd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * DbOom Entity manager.
 * <p>
 * Important: configure settings before entity registration!
 * <p>
 * Mapping definitions are used <b>only</b> by a result set mapper (such as {@link jodd.db.oom.mapper.ResultSetMapper})
 * to lookup for an entity from table name. Table names are read from result-set meta data, for example.
 * It is not needed to use mappings at all: in that case just provide entity types during the conversion of
 * result set to objects.
 */
public class DbEntityManager {

	private static final Logger log = LoggerFactory.getLogger(DbEntityManager.class);

	private final DbOomConfig dbOomConfig;

	public DbEntityManager(final DbOomConfig dbOomConfig) {
		this.dbOomConfig = dbOomConfig;
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
	public void setPrimitiveEntitiesPrefixes(final String... primitiveEntitiesPrefixes) {
		this.primitiveEntitiesPrefixes = primitiveEntitiesPrefixes;
	}

	protected TypeCache<DbEntityDescriptor> descriptorsMap = TypeCache.createDefault();
	protected Map<String, DbEntityDescriptor> entityNamesMap = new HashMap<>();
	protected Map<String, DbEntityDescriptor> tableNamesMap = new NamedValuesHashMap<>();

	/**
	 * Lookups {@link DbEntityDescriptor} for some type and registers the type if is new.
	 * <p>
	 * Returns <code>null</code> for core classes from <code>java</code> run-time packages!
	 * Some types are <b>not</b> entities, i.e. domain objects. Instead, primitive entities
	 * are simply mapped to one column. 
	 */
	public <E> DbEntityDescriptor<E> lookupType(final Class<E> type) {
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
	 * Lookups for {@link jodd.db.oom.DbEntityDescriptor} that was registered with this manager.
	 * Returns <code>null</code> if name not found.
	 */
	public DbEntityDescriptor lookupName(final String typeName) {
		return entityNamesMap.get(typeName);
	}

	/**
	 * Lookups for {@link jodd.db.oom.DbEntityDescriptor} that was registered with this manager.
	 * Returns <code>null</code> if table name not found. Lookup is case-insensitive.
	 */
	public DbEntityDescriptor lookupTableName(final String tableName) {
		return tableNamesMap.get(tableName);
	}

	/**
	 * Registers just type and entity names. Enough for most usages.
	 */
	public <E> DbEntityDescriptor<E> registerType(final Class<E> type) {
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
	public <E> DbEntityDescriptor<E> registerEntity(final Class<E> type) {
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
	public <E> DbEntityDescriptor<E> registerEntity(final Class<E> type, final boolean force) {
		if (force) {
			removeEntity(type);
		}
		return registerEntity(type);
	}

	/**
	 * Removes entity and returns removed descriptor.
	 */
	public <E> DbEntityDescriptor<E> removeEntity(final Class<E> type) {
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
	protected <E> DbEntityDescriptor<E> createDbEntityDescriptor(final Class<E> type) {
		final String schemaName = dbOomConfig.getSchemaName();
		final TableNamingStrategy tableNames = dbOomConfig.getTableNames();
		final ColumnNamingStrategy columnNames = dbOomConfig.getColumnNames();

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
	 */
	public void reset() {
		descriptorsMap.clear();
		entityNamesMap.clear();
		tableNamesMap.clear();
	}

	/**
	 * Creates new entity instances.
	 */
	public <E> E createEntityInstance(final Class<E> type) {
		try {
			return ClassUtil.newInstance(type);
		} catch (Exception ex) {
			throw new DbOomException(ex);
		}
	}

	public void forEachEntity(final Consumer<DbEntityDescriptor> consumer) {
		descriptorsMap.forEachValue(consumer);
	}

}