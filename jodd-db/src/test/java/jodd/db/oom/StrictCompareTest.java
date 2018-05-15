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
import jodd.db.oom.fixtures.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrictCompareTest {

	@Test
	void testTableNameDefault() {
		DbOom dbOom = DbOom.create().get();
		DbEntityManager dbEntityManager = DbOom.get().entityManager();

		dbEntityManager.registerEntity(Tester.class);

		DbEntityDescriptor ded = dbEntityManager.lookupType(Tester.class);
		assertEquals("TESTER", ded.getTableName());

		assertNotNull(dbEntityManager.lookupTableName("TESTER"));
		assertNotNull(dbEntityManager.lookupTableName("tester"));
		assertNotNull(dbEntityManager.lookupTableName("Tester"));
		assertNotNull(dbEntityManager.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dbEntityManager.reset();
		DbOom.get().config().getTableNames().setLowercase(true);
		dbEntityManager.registerEntity(Tester.class);

		ded = dbEntityManager.lookupType(Tester.class);
		assertEquals("tester", ded.getTableName());

		assertNotNull(dbEntityManager.lookupTableName("TESTER"));
		assertNotNull(dbEntityManager.lookupTableName("tester"));
		assertNotNull(dbEntityManager.lookupTableName("Tester"));
		assertNotNull(dbEntityManager.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dbEntityManager.reset();
		DbOom.get().config().getTableNames().setChangeCase(false);
		DbOom.get().config().getTableNames().setSplitCamelCase(false);
		dbEntityManager.registerEntity(Tester.class);

		ded = dbEntityManager.lookupType(Tester.class);
		assertEquals("Tester", ded.getTableName());

		assertNotNull(dbEntityManager.lookupTableName("TESTER"));
		assertNotNull(dbEntityManager.lookupTableName("tester"));
		assertNotNull(dbEntityManager.lookupTableName("Tester"));
		assertNotNull(dbEntityManager.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dbOom.shutdown();
	}

}
