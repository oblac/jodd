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

package jodd.joy.db;

import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbOomManager;
import jodd.db.oom.DbOomQuery;
import jodd.mutable.MutableLong;
import jodd.petite.meta.PetiteBean;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static jodd.db.oom.DbOomQuery.query;

/**
 * Database in-memory next ID generator.
 * It finds the last id (max) for every entity on first use.
 * Then it just keeps the count in the memory.
 */
@PetiteBean
public class DbIdGenerator {

	private static final Logger log = LoggerFactory.getLogger(DbIdGenerator.class);

	protected Map<Class<?>, MutableLong> entityIdsMap = new HashMap<>();

	/**
	 * Resets all stored data.
	 */
	public synchronized void reset() {
		entityIdsMap.clear();
	}

	/**
	 * Returns next ID for given entity type.
	 * On the first call, it finds the max value of all IDs and stores it.
	 * On later calls, stored id is incremented and returned.
	 */
	public synchronized long nextId(Class entityType) {
		MutableLong lastId = entityIdsMap.get(entityType);
		if (lastId == null) {
			DbOomManager dbOomManager = DbOomManager.getInstance();

			DbEntityDescriptor ded = dbOomManager.lookupType(entityType);
			String tableName = ded.getTableName();
			String idColumn = ded.getIdColumnName();

			DbOomQuery dbOomQuery = query("select max(" + idColumn + ") from " + tableName);

			long lastLong = dbOomQuery.autoClose().executeCount();

			if (log.isDebugEnabled()) {
				log.debug("Last id for " + entityType.getName() + " is " + lastLong);
			}

			lastId = new MutableLong(lastLong);

			entityIdsMap.put(entityType, lastId);
		}

		lastId.value++;
		return lastId.value;
	}

}