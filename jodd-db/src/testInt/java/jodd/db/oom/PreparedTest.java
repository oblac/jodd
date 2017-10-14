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

import jodd.db.DbQuery;
import jodd.db.DbSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreparedTest extends DbBaseTest {

	public class PostgreSql extends PostgreSqlDbAccess {
		@Override
		public String getCreateTableSql() {
			return "create table TESTER (" +
				"ID			SERIAL," +
				"NAME		varchar(20)	NOT NULL," +
				"VALUE		integer NULL," +
				"primary key (ID)" +
				')';
		}

		@Override
		public String getTableName() {
			return "TESTER";
		}
	}

	@Test
	public void testPreparedStatementDebugFalse() {
		DbBaseTest.DbAccess db = new PreparedTest.PostgreSql();
		init();
		db.initDb();
		connect();

		db.createTables();
		try {
			test(false);
		} finally {
			db.close();
		}
	}

	@Test
	public void testPrepredStatementDebugTrue() {
		DbBaseTest.DbAccess db = new PreparedTest.PostgreSql();
		init();
		db.initDb();
		connect();

		db.createTables();
		try {
			test(true);
		} finally {
			db.close();
		}
	}

	private void test(final boolean debug) {
		DbSession session = new DbSession();

		DbQuery dbQuery = new DbQuery(session, "select * from TESTER where id=:id and name=:name");
		dbQuery.setDebug(debug);

		dbQuery.setInteger("id", 3);
		dbQuery.setString("name", "John");

		if (debug) {
			assertEquals("select * from TESTER where id=3 and name='John'", dbQuery.toString());
		} else {
			assertEquals("select * from TESTER where id=? and name=?", dbQuery.toString());
		}

		session.closeSession();
	}

}
