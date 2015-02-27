// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.bean.BeanUtil;
import jodd.db.DbSession;
import jodd.db.ThreadDbSessionHolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AppDaoTest extends DbHsqldbTestCase {

	@Test
	public void testAppDao1() {
		DbSession session = new DbSession(cp);
		ThreadDbSessionHolder.set(session);

		AppDao appDao = new AppDao();
		appDao.setKeysGeneratedByDatabase(false);
		DbIdGenerator didg = new DbIdGenerator();
		BeanUtil.setDeclaredProperty(appDao, "dbIdGenerator", didg);

		// store

		Girl girl = new Girl();
		girl.setName("Emma");
		girl.setSpeciality("piano");

		assertNull(girl.getId());
		appDao.store(girl);
		assertEquals(1, girl.getId().longValue());

		// update

		girl.setSpeciality("Guitar");
		appDao.store(girl);

		long count = appDao.count(Girl.class);
		assertEquals(1, count);

		Girl dbGirl = appDao.findById(Girl.class, 1);
		assertEquals("Guitar", dbGirl.getSpeciality());
		assertEquals("Emma", dbGirl.getName());

		session.closeSession();
		ThreadDbSessionHolder.remove();
	}
}