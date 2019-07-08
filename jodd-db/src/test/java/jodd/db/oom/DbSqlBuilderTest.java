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
import jodd.db.oom.sqlgen.ParameterValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jodd.db.oom.ColumnAliasType.COLUMN_CODE;
import static jodd.db.oom.ColumnAliasType.TABLE_NAME;
import static jodd.db.oom.ColumnAliasType.TABLE_REFERENCE;
import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DbSqlBuilderTest {

	@BeforeEach
	void setUp() {
		final DbOom dbOom = DbOom.create().get();

		DbEntityManager dbEntityManager = DbOom.get().entityManager();
		dbEntityManager.registerType(Girl.class);
		dbEntityManager.registerType(BadGirl.class);
		dbEntityManager.registerType(Boy.class);
		dbEntityManager.registerType(BadBoy.class);
	}

	@AfterEach
	void teardown() {
		DbOom.get().shutdown();
	}

	@Test
	void testTable() {
		DbOom dbOom = DbOom.get();

		DbSqlBuilder s;

		// [1]
		s = dbOom.sql().table("Boy");
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = dbOom.sql().table("Boy", null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = dbOom.sql().table("Boy", "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [2]
		s = dbOom.sql().table("BadBoy");
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = dbOom.sql().table("BadBoy", null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = dbOom.sql().table("BadBoy", "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [3]
		s = dbOom.sql().table(Boy.class);
		assertEquals("BOY Boy", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = dbOom.sql().table(Boy.class, null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = dbOom.sql().table(Boy.class, "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [4]
		s = dbOom.sql().table(BadBoy.class);
		assertEquals("BOY BadBoy", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = dbOom.sql().table(BadBoy.class, null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = dbOom.sql().table(BadBoy.class, "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [5]
		s = dbOom.sql().table("bbb").use("bbb", Boy.class);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

		s = dbOom.sql().table("bbb", null).use("bbb", Boy.class);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

		s = dbOom.sql().table("bbb", "x").use("bbb", Boy.class);
		assertEquals("BOY x", s.generateQuery());
		assertNotNull(s.getTableDescriptor("x"));

		// [6]
		s = dbOom.sql().table("Boy bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

		s = dbOom.sql().table("BadBoy bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

	}

	@Test
	void testColumn() {
		DbOom dbOom = DbOom.get();

		assertEquals("BOY.ID BOY", dbOom.sql().column("Boy.id").table("Boy", null).generateQuery());
		assertEquals("BOY.ID BOY", dbOom.sql().column("Boy", "id").table("Boy", null).generateQuery());
		assertEquals("Boy.ID BOY Boy", dbOom.sql().column("Boy.id").table("Boy", "Boy").generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().column("b.id").table("Boy", "b").generateQuery());
		assertEquals("Boy$ID BOY b", dbOom.sql().column("Boy.id").table("Boy", "b").generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().column("b", "id").table("Boy", "b").generateQuery());
		assertEquals("Boy.ID BOY Boy", dbOom.sql().column("Boy.id").table(Boy.class).generateQuery());

		assertEquals("b.ID, b.GIRL_ID, b.NAME BOY b", dbOom.sql().column("b.*").table("BadBoy", "b").generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().column("b.+").table("BadBoy", "b").generateQuery());

		assertEquals("b.ID as col_0_ BOY b", dbOom.sql().column("b.id").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("b.ID as b$ID BOY b", dbOom.sql().column("b.id").table("Boy", "b").aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("b.ID as BOY$ID BOY b", dbOom.sql().column("b.id").table("Boy", "b").aliasColumnsAs(TABLE_NAME).generateQuery());

		assertEquals("col_0_ BOY b", dbOom.sql().column("Boy.id").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("Boy$ID BOY b", dbOom.sql().column("Boy.id").table("Boy", "b").aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("BOY$ID BOY b", dbOom.sql().column("Boy.id").table("Boy", "b").aliasColumnsAs(TABLE_NAME).generateQuery());
	}

	@Test
	void testReferences() {
		DbOom dbOom = DbOom.get();

		assertEquals("b.ID BOY b", dbOom.sql().ref("b", "id").$(" ").table("Boy", "b").generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().ref("b.+").$(" ").table("BadBoy", "b").generateQuery());
		assertEquals("b BOY b", dbOom.sql().ref("b").table("Boy", "b").generateQuery());
		assertEquals("BOY BOY", dbOom.sql().ref("Boy").table("Boy", null).generateQuery());
		assertEquals("Boy BOY Boy", dbOom.sql().ref("Boy").table("Boy", "Boy").generateQuery());

		assertEquals("b.ID BOY b", dbOom.sql().ref("b.id").$(" ").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().ref("b.id").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().ref("b.id").table("Boy", "b").aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("b.ID BOY b", dbOom.sql().ref("b.id").table("Boy", "b").aliasColumnsAs(TABLE_NAME).generateQuery());

		assertEquals("b.ID from BOY b", sql("$b.id from $T{b b}").use("b", Boy.class).generateQuery());

		assertEquals("BOY.ID BOY", dbOom.sql().ref("Boy.id").$(" ").table("Boy", null).aliasColumnsAs(COLUMN_CODE).generateQuery());
	}

	@Test
	void testInsert() {
		DbOom dbOom = DbOom.get();

		Boy b = new Boy();

		DbSqlBuilder dbc = dbOom.sql().insert("Boy", b);
		assertEquals("insert into BOY (GIRL_ID, ID) values (:boy.girlId, :boy.id)", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.girlId").getValue());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.id").getValue());

		dbc = dbOom.sql().insert(Boy.class, b);
		assertEquals("insert into BOY (GIRL_ID, ID) values (:boy.girlId, :boy.id)", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.girlId").getValue());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.id").getValue());

		dbc = dbOom.sql().insert(b);
		assertEquals("insert into BOY (GIRL_ID, ID) values (:boy.girlId, :boy.id)", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.girlId").getValue());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.id").getValue());

	}

	@Test
	@SuppressWarnings({"unchecked"})
	void testValue() {
		Boy b = new Boy();
		DbSqlBuilder dbc = new DbSqlBuilder();
		assertEquals(":zzz", dbc.value("zzz", Integer.valueOf(b.girlId)).generateQuery());
		assertEquals(1, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("zzz").getValue());

		assertEquals(":zzz :p0", dbc.$().value(Integer.valueOf(3)).generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(3), dbc.getQueryParameters().get("p0").getValue());

		assertEquals(":zzz :p0 :p1", dbc.$().valueRef("val").use("val", Integer.valueOf(7)).generateQuery());
		assertEquals(3, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(3), dbc.getQueryParameters().get("p0").getValue());
		assertEquals(Integer.valueOf(7), dbc.getQueryParameters().get("p1").getValue());

		dbc = new DbSqlBuilder();
		List l = new ArrayList();
		l.add("v1");
		l.add(Integer.valueOf(2));
		assertEquals(":zzz0, :zzz1", dbc.value("zzz", l).generateQuery());
		assertEquals("v1", dbc.getQueryParameters().get("zzz0").getValue());
		assertEquals(Integer.valueOf(2), dbc.getQueryParameters().get("zzz1").getValue());

	}

	@Test
	void testUpdateSet() {
		DbOom dbOom = DbOom.get();

		Boy b = new Boy();
		b.id = 1;
		b.girlId = 2;

		DbSqlBuilder dbc = dbOom.sql().set("b", b).table("Boy", "b");
		assertEquals("set GIRL_ID=:boy.girlId, ID=:boy.id BOY b", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());

		dbc = dbOom.sql().set("Boy", b).table("Boy", null);
		assertEquals("set GIRL_ID=:boy.girlId, ID=:boy.id BOY", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());


		dbc = dbOom.sql().setAll("b", b).table("Boy", "b");
		assertEquals("set GIRL_ID=:boy.girlId, ID=:boy.id, NAME=:boy.name BOY b", dbc.generateQuery());
		assertEquals(3, dbc.getQueryParameters().size());

		BadBoy bb = new BadBoy();

		dbc = dbOom.sql().set("b", bb).table("BadBoy", "b");
		assertEquals("set BOY b", dbc.generateQuery());

		dbc = dbOom.sql().set("BadBoy", bb).table("BadBoy", null);
		assertEquals("set BOY", dbc.generateQuery());

		dbc = dbOom.sql().setAll("b", bb).table("BadBoy", "b");
		assertEquals("set ID=:badBoy.ajdi, GIRL_ID=:badBoy.girlId, NAME=:badBoy.nejm BOY b", dbc.generateQuery());
	}

	@Test
	void testStrings() {
		DbOom dbOom = DbOom.get();

		DbSqlBuilder dbc = dbOom.sql().$("123").$("xxx");
		assertEquals("123xxx", dbc.generateQuery());
	}

	@Test
	void testWhere() {
		DbOom dbOom = DbOom.get();

		{
			Boy b = new Boy();
			b.id = 1;
			b.girlId = 2;
			DbSqlBuilder dbc = dbOom.sql().match("Boy", b).$(" ").table(b, null);
			assertEquals("(BOY.GIRL_ID=:boy.girlId and BOY.ID=:boy.id) BOY", dbc.generateQuery());
			dbc = dbOom.sql().match("b", b).$(" ").table(b, "b");
			assertEquals("(b.GIRL_ID=:boy.girlId and b.ID=:boy.id) BOY b", dbc.generateQuery());
		}
		{
			BadBoy bb = new BadBoy();
			DbSqlBuilder dbc = dbOom.sql().match("BadBoy", bb).table(bb, null);
			assertEquals("(1=1) BOY", dbc.generateQuery());
			dbc = dbOom.sql().match("b", bb).table(bb, "b");
			assertEquals("(1=1) BOY b", dbc.generateQuery());
		}
		{
			BadBoy bb = new BadBoy();
			DbSqlBuilder dbc = dbOom.sql().$("where ").match("BadBoy", bb).$(" ").table(bb, null);
			assertEquals("where (1=1) BOY", dbc.generateQuery());
			dbc = dbOom.sql().$("where").$().match("b", bb).$(" ").table(bb, "b");
			assertEquals("where (1=1) BOY b", dbc.generateQuery());

			dbc = dbOom.sql().$("where").$().match("BadBoy", bb).table(bb, null);
			assertEquals("where (1=1) BOY", dbc.generateQuery());
			dbc = dbOom.sql().$("where ").match("b", bb).table(bb, "b");
			assertEquals("where (1=1) BOY b", dbc.generateQuery());

			bb.ajdi = Integer.valueOf(3);
			dbc = dbOom.sql().$("where ").match("BadBoy", bb).$(" ").table(bb, null);
			assertEquals("where (BOY.ID=:badBoy.ajdi) BOY", dbc.generateQuery());
			dbc = dbOom.sql().$("where ").match("b", bb).$(" ").table(bb, "b");
			assertEquals("where (b.ID=:badBoy.ajdi) BOY b", dbc.generateQuery());

			bb.ajdi = null;
			bb.nejm = "";
			dbc = dbOom.sql().$("where ").match("BadBoy", bb).$(" ").table(bb, null);
			assertEquals("where (1=1) BOY", dbc.generateQuery());
			dbc = dbOom.sql().$("where ").match("b", bb).$(" ").table(bb, "b");
			assertEquals("where (1=1) BOY b", dbc.generateQuery());

			bb.ajdi = null;
			bb.nejm = "foo";
			dbc = dbOom.sql().$("where ").match("BadBoy", bb).$(" ").table(bb, null);
			assertEquals("where (BOY.NAME=:badBoy.nejm) BOY", dbc.generateQuery());
			dbc = dbOom.sql().$("where ").match("b", bb).$(" ").table(bb, "b");
			assertEquals("where (b.NAME=:badBoy.nejm) BOY b", dbc.generateQuery());
		}
	}

	@Test
	void testCriteria() {
		DbOom dbOom = DbOom.get();

		BadBoy bb = new BadBoy();
		BadGirl bg = new BadGirl();

		DbSqlBuilder dbc = dbOom.sql().$("select").
				columnsAll("bb").columnsIds("bg").$(" from").
				table(bb, "bb").table(bg, "bg").$().
				match("bb", bb).$().match("bg", bg);

		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg (1=1) (1=1)", dbc.generateQuery());

		dbc = dbOom.sql().$("select").
				columnsAll("bb").columnsIds("bg").$(" from").
				table(bb, "bb").table(bg, "bg").$(" where ").
				match("bb", bb).$().match("bg", bg).$(" or ").refId("bb").$("=").value(Long.valueOf(5L));


		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg where (1=1) (1=1) or bb.ID=:p0", dbc.generateQuery());

		dbc.reset();
		bb.ajdi = bg.fooid = Integer.valueOf(1);
		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg where (bb.ID=:badBoy.ajdi) (bg.ID=:badGirl.fooid) or bb.ID=:p0", dbc.generateQuery());

		// test double call
		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg where (bb.ID=:badBoy.ajdi) (bg.ID=:badGirl.fooid) or bb.ID=:p0", dbc.generateQuery());
	}

	@Test
	void testCriteria2() {
		DbOom dbOom = DbOom.get();

		Girl girl = new Girl();
		girl.speciality = "piano";

		Girl girl_condition = new Girl();
		girl_condition.speciality = "swim";

		String tableRef = "ggg";

		DbSqlBuilder dsb = dbOom.sql().$("update ").table(girl, tableRef).set(tableRef, girl).$("where ").
		           match(tableRef, "conditionRef").use("conditionRef",girl_condition);

		String sql = dsb.generateQuery();

		Map<String, ParameterValue> params = dsb.getQueryParameters();
		assertEquals(2, params.size());

		assertEquals("piano", params.get("girl.speciality").getValue());
		assertEquals("swim", params.get("conditionRef.speciality").getValue());

		assertEquals(
				"update GIRL ggg set SPECIALITY=:girl.speciality " +
				"where (ggg.SPECIALITY=:conditionRef.speciality)",
				sql);

	}

}
