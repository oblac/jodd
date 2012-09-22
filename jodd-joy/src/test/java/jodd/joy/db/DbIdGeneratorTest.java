// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.ThreadDbSessionHolder;

public class DbIdGeneratorTest extends DbHsqldbTestCase {

	public void testIdGen() throws Exception {
		DbSession session = new DbSession(cp);
		ThreadDbSessionHolder.set(session);

		AppDao appDao = new AppDao();
		appDao.setGeneratedKeys(false);
		appDao.dbIdGenerator = new DbIdGenerator();

		Girl girl = new Girl();
		girl.name = "One";
		girl.speciality = "Code";

		appDao.store(girl);
		assertEquals(1, girl.getId().longValue());

		girl = new Girl();
		girl.name = "Two";
		girl.speciality = "Dddd";

		appDao.store(girl);
		assertEquals(2, girl.getId().longValue());

		appDao.dbIdGenerator.reset();

		girl = new Girl();
		girl.name = "Three";
		girl.speciality = "Ssss";

		appDao.store(girl);
		assertEquals(3, girl.getId().longValue());

		assertEquals(3, appDao.count(Girl.class));

		assertNotNull(appDao.findById(Girl.class, Long.valueOf(1)));
		assertNotNull(appDao.findById(Girl.class, Long.valueOf(2)));
		assertNotNull(appDao.findById(Girl.class, Long.valueOf(3)));
	}

}
