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

package jodd.db.oom.dao;

import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.ThreadDbSessionHolder;
import jodd.db.oom.DbOomManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GenericDaoTest extends DbHsqldbTestCase {

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Girl.class);
		dbOom.registerEntity(Boy.class);
	}

	@Test
	public void testAppDao1() {
		DbSession session = new DbSession(cp);
		ThreadDbSessionHolder.set(session);

		GenericDao dao = new GenericDao();

		// save

		Girl girl = new Girl();
		girl.setName("Emma");
		girl.setSpeciality("piano");
		girl.setId(Long.valueOf(1));

		dao.save(girl);
		assertEquals(1, girl.getId().longValue());

		Boy boy1 = new Boy();
		boy1.setId(1);
		boy1.setName("Oleg");
		boy1.setGirlId(1);

		Boy boy2 = new Boy();
		boy2.setId(2);
		boy2.setName("Marco");
		boy2.setGirlId(1);

		ArrayList<Boy> boys = new ArrayList<Boy>();
		boys.add(boy1);
		boys.add(boy2);

		dao.saveAll(boys);

		// find

		Boy dbBoy = dao.findById(Boy.class, 1);
		assertEquals(boy1.getId(), dbBoy.getId());
		assertEquals(boy1.getName(), dbBoy.getName());
		assertEquals(boy1.getGirlId(), dbBoy.getGirlId());

		// update

		girl.setSpeciality("Guitar");
		dao.update(girl);

		long count = dao.count(Girl.class);
		assertEquals(1, count);

		Girl dbGirl = dao.findById(Girl.class, 1);
		assertEquals("Guitar", dbGirl.getSpeciality());
		assertEquals("Emma", dbGirl.getName());

		// update property

		dao.updateProperty(girl, "speciality", "math");

		dbGirl = dao.findById(Girl.class, 1);
		assertEquals("math", dbGirl.getSpeciality());
		assertEquals("math", girl.getSpeciality());
		assertEquals("Emma", dbGirl.getName());

		// add one more girl
		Girl girl2 = new Girl();
		girl2.setName("Lina");
		girl2.setSpeciality("crazy");
		girl2.setId(Long.valueOf(2));

		dao.save(girl2);
		count = dao.count(Girl.class);
		assertEquals(2, count);

		Girl emma = dao.findOneByProperty(Girl.class, "name", "Emma");
		assertNotNull(emma);
		assertEquals(1, emma.getId().longValue());

		Girl none = dao.findOneByProperty(Girl.class, "name", "Www");
		assertNull(none);

		// find by matching

		Girl match = new Girl();
		match.setName("Emma");
		dbGirl = dao.findOne(match);

		assertEquals(emma.getId(), dbGirl.getId());
		assertEquals(emma.getName(), dbGirl.getName());
		assertEquals(emma.getSpeciality(), dbGirl.getSpeciality());

		Boy boyMatch = new Boy();
		boyMatch.setGirlId(1);

		List<Boy> dbBoys = dao.find(boyMatch);
		assertEquals(2, dbBoys.size());

		// find by separate matching class

		boyMatch = new Boy();
		boyMatch.setName("Oleg");
		dbBoys = dao.find(Boy.class, boyMatch);
		assertEquals(1, dbBoys.size());

		// correct way

		BoyCriteria boyCriteria = new BoyCriteria();
		boyCriteria.setName("Oleg");

		dbBoys = dao.find(Boy.class, boyCriteria);
		assertEquals(1, dbBoys.size());


		// list all

		dbBoys = dao.listAll(Boy.class);
		assertEquals(2, dbBoys.size());

		// related

		dbBoys = dao.findRelated(Boy.class, emma);
		assertEquals(2, dbBoys.size());


		// delete

		dao.deleteById(Boy.class, 1);
		dao.deleteById(boy2);

		count = dao.count(Boy.class);
		assertEquals(0, count);

	    // delete all

		List<Girl> girls = dao.listAll(Girl.class);
		dao.deleteAllById(girls);

		count = dao.count(Girl.class);
		assertEquals(0, count);

		session.closeSession();
		ThreadDbSessionHolder.remove();
	}
}
