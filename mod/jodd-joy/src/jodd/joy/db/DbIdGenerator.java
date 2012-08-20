// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.DbOomQuery;
import jodd.log.Log;
import jodd.mutable.MutableLong;
import jodd.petite.meta.PetiteBean;

import java.util.HashMap;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;
import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

@PetiteBean
public class DbIdGenerator {

	private static final Log log = Log.getLogger(DbIdGenerator.class);

	protected Map<Class<? extends Entity>, MutableLong> idmap = new HashMap<Class<? extends Entity>, MutableLong>();

	/**
	 * Resets all memory data.
	 */
	public synchronized void reset() {
		idmap.clear();
	}

	/**
	 * Returns the next ID for given entity.
	 */
	public synchronized long generateNextId(Entity entity) {
		Class<? extends Entity> entityType = entity.getClass();

		MutableLong lastId = idmap.get(entityType);
		if (lastId == null) {
			DbOomQuery dbOomQuery = query(sql()._("select max(id) from ").table(entity));

			System.out.println(dbOomQuery.toString());

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
