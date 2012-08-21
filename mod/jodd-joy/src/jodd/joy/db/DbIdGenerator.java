// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbOomManager;
import jodd.db.oom.DbOomQuery;
import jodd.log.Log;
import jodd.mutable.MutableLong;
import jodd.petite.meta.PetiteBean;

import java.util.HashMap;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;

/**
 * Database next-ID in-memory generator.
 */
@PetiteBean
public class DbIdGenerator {

	private static final Log log = Log.getLogger(DbIdGenerator.class);

	protected Map<Class<? extends Entity>, MutableLong> idmap = new HashMap<Class<? extends Entity>, MutableLong>();

	/**
	 * Resets all stored data.
	 */
	public synchronized void reset() {
		idmap.clear();
	}

	/**
	 * Returns the next ID for given entity.
	 */
	public long nextId(Entity entity) {
		Class<? extends Entity> entityType = entity.getClass();
		return nextId(entityType);
	}

	/**
	 * Returns next ID for given entity type.
	 * On the first call, it finds the max value of all IDs and stores it.
	 * On later calls, stored id is incremented and returned.
	 */
	public synchronized long nextId(Class<? extends Entity> entityType) {
		MutableLong lastId = idmap.get(entityType);
		if (lastId == null) {
			DbOomManager dbOomManager = DbOomManager.getInstance();

			DbEntityDescriptor ded = dbOomManager.lookupType(entityType);
			String tableName = ded.getTableName();
			String idColumn = ded.getIdColumnName();

			DbOomQuery dbOomQuery = query("select max(" + idColumn + ") from " + tableName);

			long lastLong = dbOomQuery.executeCountAndClose();

			if (log.isDebugEnabled()) {
				log.debug("Last id for " + entityType.getName() + " is " + lastLong);
			}

			lastId = new MutableLong(lastLong);
		}

		lastId.value++;
		return lastId.value;
	}
}
