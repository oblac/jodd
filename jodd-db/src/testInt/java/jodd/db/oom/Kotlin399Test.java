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

import jodd.db.DbSession;
import jodd.db.oom.fixtures.Status33;
import jodd.db.oom.fixtures.Status33SqlType;
import jodd.db.oom.fixtures.Tester3;
import jodd.db.oom.fixtures.Tester33;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.type.SqlTypeManager;
import org.junit.jupiter.api.Test;

public class Kotlin399Test extends DbBaseTest {

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
	public void test399() {
		DbAccess db = new PostgreSql();

		System.out.println("\t" + db.getClass().getSimpleName());
		init();
		db.initDb();
		connect();

		db.createTables();
		SqlTypeManager.register(Tester3.Status.class, StatusSqlType.class);

		try {
			workoutJavaEntity();
			workoutKotlinEntity();
		} finally {
			db.close();
		}
	}

	private void workoutJavaEntity() {
		DbSession session = new DbSession();

		Tester3 tester = new Tester3();
		tester.setName(Tester3.Status.SELECTED);
		tester.setValue(7);

		DbOomQuery dbOomQuery = DbOomQuery.query(session, DbEntitySql.insert(tester));
		dbOomQuery.setGeneratedKey();
		dbOomQuery.executeUpdate();

		session.closeSession();
	}

	private void workoutKotlinEntity() {
		SqlTypeManager.register(Status33.class, Status33SqlType.class);
		DbSession session = new DbSession();

		Tester33 tester = new Tester33(9, Status33.SELECTED, 3);

		DbOomQuery dbOomQuery = DbOomQuery.query(session, DbEntitySql.insert(tester));
		dbOomQuery.setGeneratedKey();
		dbOomQuery.executeUpdate();

		session.closeSession();
	}

}
