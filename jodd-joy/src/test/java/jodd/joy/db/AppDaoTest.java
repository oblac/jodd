// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.bean.BeanUtil;
import jodd.db.DbSession;
import jodd.db.ThreadDbSessionHolder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AppDaoTest extends DbHsqldbTestCase {

	@Test
	public void testAppDao1() {
		DbSession session = new DbSession(cp);
		ThreadDbSessionHolder.set(session);

		AppDao appDao = new AppDao();
		appDao.setGeneratedKeys(false);
		DbIdGenerator didg = new DbIdGenerator();
		BeanUtil.setDeclaredProperty(appDao, "dbIdGenerator", didg);

		// store

		Girl girl = new Girl();
		girl.setName("Emma");
		girl.setSpeciality("piano");

		assertNull(girl.getId());
		appDao.store(girl);
		assertEquals(1, girl.getId().longValue());

		Boy boy1 = new Boy();
		boy1.setId(didg.nextId(Boy.class));
		boy1.setName("Oleg");
		boy1.setGirlId(1);

		Boy boy2 = new Boy();
		boy2.setId(didg.nextId(Boy.class));
		boy2.setName("Marco");
		boy2.setGirlId(1);

		ArrayList<Boy> boys = new ArrayList<Boy>();
		boys.add(boy1);
		boys.add(boy2);

		appDao.saveAll(boys);

		// find

		Boy dbBoy = appDao.findById(Boy.class, 1);
		assertEquals(boy1.getId(), dbBoy.getId());
		assertEquals(boy1.getName(), dbBoy.getName());
		assertEquals(boy1.getGirlId(), dbBoy.getGirlId());

		// update

		girl.setSpeciality("Guitar");
		appDao.store(girl);

		long count = appDao.count(Girl.class);
		assertEquals(1, count);

		Girl dbGirl = appDao.findById(Girl.class, 1);
		assertEquals("Guitar", dbGirl.getSpeciality());
		assertEquals("Emma", dbGirl.getName());

		// update property

		appDao.updateProperty(girl, "speciality", "math");

		dbGirl = appDao.findById(Girl.class, 1);
		assertEquals("math", dbGirl.getSpeciality());
		assertEquals("math", girl.getSpeciality());
		assertEquals("Emma", dbGirl.getName());

		// add one more girl
		Girl girl2 = new Girl();
		girl2.setName("Lina");
		girl2.setSpeciality("crazy");
		girl2.setEntityId(didg.nextId(Girl.class));

		appDao.save(girl2);
		count = appDao.count(Girl.class);
		assertEquals(2, count);

		Girl emma = appDao.findOneByProperty(Girl.class, "name", "Emma");
		assertNotNull(emma);
		assertEquals(1, emma.getEntityId());

		Girl none = appDao.findOneByProperty(Girl.class, "name", "Www");
		assertNull(none);

		// find by matching

		Girl match = new Girl();
		match.setName("Emma");
		dbGirl = appDao.findOne(match);

		assertEquals(emma.getId(), dbGirl.getId());
		assertEquals(emma.getName(), dbGirl.getName());
		assertEquals(emma.getSpeciality(), dbGirl.getSpeciality());

		Boy boyMatch = new Boy();
		boyMatch.setGirlId(1);

		List<Boy> dbBoys = appDao.find(boyMatch);
		assertEquals(2, dbBoys.size());

		// find by separate matching class

		boyMatch = new Boy();
		boyMatch.setName("Oleg");
		dbBoys = appDao.find(Boy.class, boyMatch);
		assertEquals(0, dbBoys.size());		// this doesn't work since boy has girldId set to 0

		// correct way

		BoyCriteria boyCriteria = new BoyCriteria();
		boyCriteria.setName("Oleg");

		dbBoys = appDao.find(Boy.class, boyCriteria);
		assertEquals(1, dbBoys.size());


		// list all

		dbBoys = appDao.listAll(Boy.class);
		assertEquals(2, dbBoys.size());

		// related

		dbBoys = appDao.findRelated(Boy.class, emma);
		assertEquals(2, dbBoys.size());


		// delete

		appDao.deleteById(Boy.class, 1);
		appDao.deleteById(boy2);

		count = appDao.count(Boy.class);
		assertEquals(0, count);

	    // delete all

		List<Girl> girls = appDao.listAll(Girl.class);
		appDao.deleteAllById(girls);

		count = appDao.count(Girl.class);
		assertEquals(0, count);

		session.closeSession();
		ThreadDbSessionHolder.remove();
	}
}