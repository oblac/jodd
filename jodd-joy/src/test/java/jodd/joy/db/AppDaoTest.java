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

import jodd.bean.BeanUtil;
import jodd.db.DbSession;
import jodd.db.ThreadDbSessionHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppDaoTest extends DbHsqldbTestCase {

	@Test
	public void testAppDao1() {
		DbSession session = new DbSession(cp);
		ThreadDbSessionHolder.set(session);

		AppDao appDao = new AppDao();
		appDao.setKeysGeneratedByDatabase(false);
		DbIdGenerator didg = new DbIdGenerator();
		BeanUtil.declared.setProperty(appDao, "dbIdGenerator", didg);

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
