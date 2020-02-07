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
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalDataTest extends DbBaseTest {

	@DbTable("test")
	public static class test {
		@DbId
		Long id;
		@DbColumn
		String name;
		@DbColumn
		LocalDate registerDate;
		@DbColumn
		LocalDateTime createTime;
	}

	class MySqlTest extends MySqlDbAccess {

		@Override
		public String createTableSql() {
			return "create table test(\n" +
				"id bigint,\n" +
				"name varchar(80),\n" +
				"register_date date,\n" +
				"create_time datetime\n" +
				")";
		}

		@Override
		public String getTableName() {
			return "test";
		}
	}

	@Test
	void testPreparedStatementDebugFalse() {
		DbBaseTest.DbAccess db = new LocalDataTest.MySqlTest();
		init(db);

		db.createTables();
		try {
			test();
		} finally {
			db.close();
		}
	}

	private void test() {
		DbSession db = new DbSession(connectionPool);

		DbQuery q = DbQuery.query(db, "INSERT INTO test VALUES (1,'test',now(),now())");
		q.autoClose().executeUpdate();

		DbOom.get().entityManager().registerEntity(test.class);

		String sql = "SELECT $C{t.*} FROM $T{test t} WHERE $t.createTime BETWEEN :start AND :end and $t.registerDate BETWEEN :start and :end";
		LocalDate start = LocalDate.now().plusDays(-2L);
		LocalDate end = LocalDate.now().plusDays(15L);
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("end", end);
		DbOomQuery query = DbOomQuery.query(db, DbSqlBuilder.sql(sql)).autoClose();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setObject(entry.getKey(), entry.getValue());
		}

		List<Test> testList = query.list();
		assertEquals(1, testList.size());

		db.closeSession();
	}

}
