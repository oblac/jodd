// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.sqlgen.DbEntitySql;
import static jodd.db.oom.sqlgen.DbEntitySql.insert;
import static jodd.db.oom.sqlgen.DbEntitySql.updateAll;
import static jodd.db.oom.sqlgen.DbEntitySql.findByColumn;
import static jodd.db.oom.DbOomQuery.query;
import jodd.db.DbQuery;
import jodd.log.Log;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import java.util.List;

/**
 * Generic DAO.
 */
@PetiteBean
public class AppDao {

	private static final Log log = Log.getLogger(AppDao.class);

	@PetiteInject
	DbIdGenerator dbIdGenerator;

	public AppDao() {
		setAutogeneratePrimaryKey(true);
	}

	// ---------------------------------------------------------------- config

	protected boolean autogeneratePrimaryKey;

	/**
	 * Returns <code>true</code> if primary keys should be auto-generated.
	 */
	public boolean isAutogeneratePrimaryKey() {
		return autogeneratePrimaryKey;
	}

	/**
	 * Specifies if primary keys should be auto generated.
	 */
	public void setAutogeneratePrimaryKey(boolean autogeneratePrimaryKey) {
		this.autogeneratePrimaryKey = autogeneratePrimaryKey;
		if (log.isDebugEnabled()) {
			if (autogeneratePrimaryKey) {
				log.debug("IDs are automatically incremented by database");
			} else {
				log.debug("IDs are generated");
			}
		}
	}

	// ---------------------------------------------------------------- store

	/**
	 * Saves or updates entity. If ID is not <code>null</code>, entity will be updated.
	 * Otherwise, entity will be inserted into the database.
	 */
	public <E extends Entity> E store(E entity) {
		if (entity.isPersistent() == false) {
			DbQuery q;
			if (autogeneratePrimaryKey == true) {
				q = query(insert(entity));
				q.setGeneratedKey();
				q.executeUpdate();
				long key = q.getGeneratedKey();
				entity.setId(Long.valueOf(key));
			} else {
				long nextId = dbIdGenerator.generateNextId(entity);
				entity.setId(Long.valueOf(nextId));
				q = query(insert(entity));
				q.executeUpdate();
			}
			q.close();
		} else {
			query(updateAll(entity)).executeUpdateAndClose();
		}
		return entity;
	}

	/**
	 * Simply saves entity into the database.
	 */
	public <E extends Entity> void save(E entity) {
		DbQuery q = query(insert(entity));
		q.executeUpdateAndClose();
	}

	// ---------------------------------------------------------------- update

	/**
	 * Updates single property.
	 */
	public <E extends Entity> void updateProperty(E entity, String name, Object value) {
		query(DbEntitySql.updateColumn(entity, name, value)).executeUpdateAndClose();
	}


	// ---------------------------------------------------------------- find

	/**
	 * Finds single entity by its id.
	 */
	public <E extends Entity> E findById(Class<E> entityType, Long id) {
		return query(DbEntitySql.findById(entityType, id)).findOneAndClose(entityType);
	}

	/**
	 * Finds single entity by its id.
	 */
	@SuppressWarnings({"unchecked"})
	public <E extends Entity> E findById(E entity) {
		if (entity == null) {
			return null;
		}
		return (E) query(DbEntitySql.findById(entity)).findOneAndClose(entity.getClass());
	}

	/**
	 * Finds single entity by property match,
	 */
	public <E extends Entity> E findOneByProperty(Class<E> entityType, String name, Object value) {
		return query(findByColumn(entityType, name, value)).findOneAndClose(entityType);
	}

	/**
	 * Finds one entity for given criteria.
	 */
	@SuppressWarnings({"unchecked"})
	public <E extends Entity> E findOne(E criteria) {
		return (E) query(DbEntitySql.find(criteria)).findOneAndClose(criteria.getClass());
	}

	/**
	 * Finds list of entities matching given criteria.
	 */
	@SuppressWarnings({"unchecked"})
	public <E extends Entity> List<E> find(E criteria) {
		return (List<E>) query(DbEntitySql.find(criteria)).listOneAndClose(criteria.getClass());
	}

	// ---------------------------------------------------------------- delete

	/**
	 * Deleted single entity by its id.
	 */
	public void deleteById(Class entityType, Long id) {
		query(DbEntitySql.deleteById(entityType, id)).executeUpdateAndClose();
	}

	/**
	 * Delete single object by its id.
	 */
	public void deleteById(Entity entity) {
		if (entity != null && entity.isPersistent()) {
			query(DbEntitySql.delete(entity)).executeUpdateAndClose();
		}
	}


	// ---------------------------------------------------------------- count

	/**
	 * Counts number of entities.
	 */
	public <E extends Entity> long count(Class<E> entityType) {
		return query(DbEntitySql.count(entityType)).executeCountAndClose();
	}


	// ---------------------------------------------------------------- related

	/**
	 * Finds related entity.
	 */
	public <E extends Entity> List<E> findRelated(Class<E> target, Entity source) {
		return query(DbEntitySql.findForeign(target, source)).listAndClose();
	}

	// ---------------------------------------------------------------- list

	/**
	 * List all entities. 
	 */
	public <E extends Entity> List<E> list(Class<E> target) {
		return query(DbEntitySql.from(target)).list(target);
	}

}
