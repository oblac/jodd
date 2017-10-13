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

import jodd.db.oom.fixtures.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StrictCompareTest {

	@Test
	public void testTableNameDefault() {
		DbOomManager.resetAll();
		DbOomManager dboom = DbOomManager.getInstance();

		dboom.registerEntity(Tester.class);

		DbEntityDescriptor ded = dboom.lookupType(Tester.class);
		assertEquals("TESTER", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));
		assertNotNull(dboom.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dboom.reset();
		dboom.getTableNames().setLowercase(true);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("tester", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));
		assertNotNull(dboom.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dboom.reset();
		dboom.getTableNames().setChangeCase(false);
		dboom.getTableNames().setSplitCamelCase(false);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("Tester", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));
		assertNotNull(dboom.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));
	}

}
