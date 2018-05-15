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
import jodd.db.oom.fixtures.BadBoy;
import jodd.db.oom.fixtures.BadGirl;
import jodd.db.oom.fixtures.Boy;
import jodd.db.oom.fixtures.Girl;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DbSqlTemplateWithPrefixTest {

	@BeforeEach
	void setUp() {
		DbOom dbOom = DbOom.create().get();
		DbEntityManager dbEntityManager = dbOom.entityManager();

		dbOom.config().getTableNames().setPrefix("PRE_");
		dbOom.config().getTableNames().setSuffix("_SUF");

		dbEntityManager.registerType(Boy.class);
		dbEntityManager.registerType(BadBoy.class);
		dbEntityManager.registerType(BadGirl.class);
		dbEntityManager.registerType(Girl.class);
	}

	@AfterEach
	void teardown() {
		DbOom.get().shutdown();
	}

	@Test
	void testTablePrefixSuffix() {
		DbOom dbOom = DbOom.get();

		DbSqlBuilder st;

		st = dbOom.sql("$T{Boy} $Boy.id $C{Boy.id}");
		assertEquals("PRE_BOY_SUF PRE_BOY_SUF.ID PRE_BOY_SUF.ID", st.generateQuery());

		st = sql("$T{Boy b} $b.id $C{b.id}");
		assertEquals("PRE_BOY_SUF b b.ID b.ID", st.generateQuery());
	}

}
