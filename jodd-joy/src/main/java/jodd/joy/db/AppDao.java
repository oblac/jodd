// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.dao.GenericDao;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

/**
 * Generic APP DAO.
 */
@PetiteBean
public class AppDao extends GenericDao {

	@PetiteInject
	protected DbIdGenerator dbIdGenerator;

	/**
	 * Generates next id by using {@link jodd.joy.db.DbIdGenerator}.
	 */
	@Override
	protected long generateNextId(DbEntityDescriptor ded) {
		return dbIdGenerator.nextId(ded.getType());
	}

}