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

import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbTable;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.db.oom.fixtures.Girl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DbNoTableTest extends DbHsqldbTestCase {

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Bean1.class);
	}

	@Test
	public void testMappingNoTable() {
		DbSession session = new DbThreadSession(cp);

		assertEquals(1, DbEntitySql.insert(new Girl(1, "Anna", "swim")).query().autoClose().executeUpdate());
		assertEquals(0, session.getTotalQueries());

		// one
		DbOomQuery q = new DbOomQuery(DbSqlBuilder.sql("select $C{g.id} + 10, UCASE($C{g.name}) from $T{Girl g}"));
		Object[] row = q.find(Integer.class, String.class);

		assertEquals(Integer.valueOf(11), row[0]);
		assertEquals("ANNA", row[1]);


		// two
		DbSqlBuilder dbSqlBuilder = DbSqlBuilder
				.sql("select $g.id + 10 as $C{Bean1.sum}, UCASE($g.name) as $C{Bean1.bigName}, $C{g.*} from $T{Girl g} where $g.id=1")
				.aliasColumnsAs(ColumnAliasType.COLUMN_CODE);

		assertEquals(
				"select g.ID + 10 as col_0_, UCASE(g.NAME) as col_1_, g.ID as col_2_, g.NAME as col_3_, g.SPECIALITY as col_4_ from GIRL g where g.ID=1",
				dbSqlBuilder.generateQuery());

		dbSqlBuilder.resetAll();

		assertEquals(
				"select g.ID + 10 as Bean1$SUM, UCASE(g.NAME) as Bean1$BIG_NAME, g.ID, g.NAME, g.SPECIALITY from GIRL g where g.ID=1",
				dbSqlBuilder.generateQuery());

		dbSqlBuilder.reset();

		q = new DbOomQuery(dbSqlBuilder);
		row = q.find(Bean1.class, Girl.class);

		Bean1 bean1 = (Bean1) row[0];
		Girl girl = (Girl) row[1];

		assertNotNull(bean1);
		assertEquals(11, bean1.getSum().intValue());
		assertEquals("ANNA", bean1.getBigName());

		assertNotNull(girl);
		assertEquals("Anna", girl.name);
		assertEquals("swim", girl.speciality);
		assertEquals(1, girl.id);


		// three
		dbSqlBuilder = DbSqlBuilder.sql(
				"select $g.id + 10 as $C{Bean1.sum}, UCASE($g.name) as $C{Bean1.bigName}, $C{g.*} from $T{Girl g} where $g.id=1");
		assertEquals(
				"select g.ID + 10 as Bean1$SUM, UCASE(g.NAME) as Bean1$BIG_NAME, g.ID, g.NAME, g.SPECIALITY from GIRL g where g.ID=1",
				dbSqlBuilder.generateQuery());

		dbSqlBuilder.reset();

		q = new DbOomQuery(dbSqlBuilder);
		row = q.find(Bean1.class, Girl.class);

		bean1 = (Bean1) row[0];
		girl = (Girl) row[1];

		assertNotNull(bean1);
		assertEquals(11, bean1.getSum().intValue());
		assertEquals("ANNA", bean1.getBigName());

		assertNotNull(girl);
		assertEquals("Anna", girl.name);
		assertEquals("swim", girl.speciality);
		assertEquals(1, girl.id);

		session.closeSession();
	}

	@DbTable
	public static class Bean1 {

		@DbColumn
		private Integer sum;

		@DbColumn
		private String bigName;

		public Integer getSum() {
			return sum;
		}

		public void setSum(Integer sum) {
			this.sum = sum;
		}

		public String getBigName() {
			return bigName;
		}

		public void setBigName(String bigName) {
			this.bigName = bigName;
		}
	}

}
