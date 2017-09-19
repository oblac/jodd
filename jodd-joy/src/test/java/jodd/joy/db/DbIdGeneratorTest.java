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

import jodd.db.DbSession;
import jodd.db.ThreadDbSessionHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DbIdGeneratorTest extends DbHsqldbTestCase {

	@Test
	public void testIdGen() throws Exception {
		DbSession session = new DbSession(cp);
		ThreadDbSessionHolder.set(session);

		AppDao appDao = new AppDao();
		appDao.setKeysGeneratedByDatabase(false);
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

		assertNotNull(appDao.findById(Girl.class, 1));
		assertNotNull(appDao.findById(Girl.class, 2));
		assertNotNull(appDao.findById(Girl.class, 3));

		session.closeSession();
		ThreadDbSessionHolder.remove();
	}

}
