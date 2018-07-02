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

package jodd.db.oom;

import jodd.db.DbOom;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.oom.dao.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupNameTest extends DbHsqldbTestCase {

	@Override
	@BeforeEach
	protected void setUp() throws Exception {
		super.setUp();

		final DbEntityManager dbEntityManager = DbOom.get().entityManager();

		dbEntityManager.registerEntity(Group.class);
	}

	@Override
	protected void initDb(DbSession session) {
		executeUpdate(session, "drop table \"GROUP\" if exists");

		final String sql = "create table \"GROUP\" (" +
			"\"GROUP\"			varchar(20)		not null" +
			')';

		executeUpdate(session, sql);
	}

	@Test
	void testGroupNames() {
		DbSession session = new DbThreadSession(cp);

		// ---------------------------------------------------------------- insert

		assertEquals(1, dbOom.entities().insert(new Group("g1")).query().autoClose().executeUpdate());

		List<Group> groups;

		groups = dbOom.entities().findAll(Group.class).query().list();
		assertEquals(1, groups.size());

		groups = dbOom.entities().findByColumn(Group.class, "column", "g1").query().list();
		assertEquals(1, groups.size());

		groups = dbOom.entities().findByColumn(Group.class, "column", "g1").query().list();
		assertEquals(1, groups.size());


		session.close();
	}

}
