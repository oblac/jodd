// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import junit.framework.TestCase;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import static jodd.db.oom.sqlgen.DbSqlBuilder.*;
import jodd.db.oom.test.Boy;
import jodd.db.oom.test.BadBoy;
import jodd.db.oom.test.BadGirl;
import jodd.db.oom.test.Girl;
import static jodd.db.oom.ColumnAliasType.*;

import java.util.List;
import java.util.ArrayList;

public class DbSlqBuilderTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.reset();
		dbOom.registerType(Girl.class);
		dbOom.registerType(BadGirl.class);
		dbOom.registerType(Boy.class);
		dbOom.registerType(BadBoy.class);
	}
	
	@Override
	protected void tearDown() throws Exception {
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.reset();
	}
	
	public void testTable() {
		DbSqlBuilder s;

		// [1]
		s = sql().table("Boy");
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = sql().table("Boy", null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = sql().table("Boy", "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [2]
		s = sql().table("BadBoy");
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = sql().table("BadBoy", null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = sql().table("BadBoy", "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [3]
		s = sql().table(Boy.class);
		assertEquals("BOY Boy", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = sql().table(Boy.class, null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("Boy"));

		s = sql().table(Boy.class, "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [4]
		s = sql().table(BadBoy.class);
		assertEquals("BOY BadBoy", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = sql().table(BadBoy.class, null);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("BadBoy"));

		s = sql().table(BadBoy.class, "bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));


		// [5]
		s = sql().table("bbb").use("bbb", Boy.class);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

		s = sql().table("bbb", null).use("bbb", Boy.class);
		assertEquals("BOY", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

		s = sql().table("bbb", "x").use("bbb", Boy.class);
		assertEquals("BOY x", s.generateQuery());
		assertNotNull(s.getTableDescriptor("x"));

		// [6]
		s = sql().table("Boy bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

		s = sql().table("BadBoy bbb");
		assertEquals("BOY bbb", s.generateQuery());
		assertNotNull(s.getTableDescriptor("bbb"));

	}

	public void testColumn() {
		assertEquals("BOY.ID BOY", sql().column("Boy.id").table("Boy", null).generateQuery());
		assertEquals("Boy.ID BOY Boy", sql().column("Boy.id").table("Boy", "Boy").generateQuery());
		assertEquals("b.ID BOY b", sql().column("b.id").table("Boy", "b").generateQuery());
		assertEquals("Boy.ID BOY Boy", sql().column("Boy.id").table(Boy.class).generateQuery());

		assertEquals("b.ID, b.GIRL_ID, b.NAME BOY b", sql().column("b.*").table("BadBoy", "b").generateQuery());
		assertEquals("b.ID BOY b", sql().column("b.+").table("BadBoy", "b").generateQuery());

		assertEquals("b.ID as col_0_ BOY b", sql().column("b.id").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("b.ID as b$ID BOY b", sql().column("b.id").table("Boy", "b").aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("b.ID as BOY$ID BOY b", sql().column("b.id").table("Boy", "b").aliasColumnsAs(TABLE_NAME).generateQuery());
	}

	public void testReferences() {
		assertEquals("b.ID BOY b", sql().ref("b", "id")._(" ").table("Boy", "b").generateQuery());
		assertEquals("b.ID BOY b", sql().ref("b.+")._(" ").table("BadBoy", "b").generateQuery());
		assertEquals("b BOY b", sql().ref("b").table("Boy", "b").generateQuery());
		assertEquals("BOY BOY", sql().ref("Boy").table("Boy", null).generateQuery());
		assertEquals("Boy BOY Boy", sql().ref("Boy").table("Boy", "Boy").generateQuery());

		assertEquals("b.ID BOY b", sql().ref("b.id")._(" ").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("b.ID BOY b", sql().ref("b.id").table("Boy", "b").aliasColumnsAs(COLUMN_CODE).generateQuery());
		assertEquals("b.ID BOY b", sql().ref("b.id").table("Boy", "b").aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("b.ID BOY b", sql().ref("b.id").table("Boy", "b").aliasColumnsAs(TABLE_NAME).generateQuery());

		assertEquals("b.ID from BOY b", sql("$b.id from $T{b b}").use("b", Boy.class).generateQuery());

		assertEquals("BOY.ID BOY", sql().ref("Boy.id")._(" ").table("Boy", null).aliasColumnsAs(COLUMN_CODE).generateQuery());
	}

	public void testInsert() {
		Boy b = new Boy();

		DbSqlBuilder dbc = sql().insert("Boy", b);
		assertEquals("insert into BOY (GIRL_ID, ID) values (:boy.girlId, :boy.id)", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.girlId").getValue());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.id").getValue());

		dbc = sql().insert(Boy.class, b);
		assertEquals("insert into BOY (GIRL_ID, ID) values (:boy.girlId, :boy.id)", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.girlId").getValue());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.id").getValue());

		dbc = sql().insert(b);
		assertEquals("insert into BOY (GIRL_ID, ID) values (:boy.girlId, :boy.id)", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.girlId").getValue());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("boy.id").getValue());

	}

	@SuppressWarnings({"unchecked"})
	public void testValue() {
		Boy b = new Boy();
		DbSqlBuilder dbc = new DbSqlBuilder();
		assertEquals(":zzz", dbc.value("zzz", Integer.valueOf(b.girlId)).generateQuery());
		assertEquals(1, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(0), dbc.getQueryParameters().get("zzz").getValue());

		assertEquals(":zzz :p0", dbc._().value(Integer.valueOf(3)).generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(3), dbc.getQueryParameters().get("p0").getValue());

		assertEquals(":zzz :p0 :p1", dbc._().valueRef("val").use("val", Integer.valueOf(7)).generateQuery());
		assertEquals(3, dbc.getQueryParameters().size());
		assertEquals(Integer.valueOf(3), dbc.getQueryParameters().get("p0").getValue());
		assertEquals(Integer.valueOf(7), dbc.getQueryParameters().get("p1").getValue());

		dbc = new DbSqlBuilder();
		List l = new ArrayList();
		l.add("v1");
		l.add(Integer.valueOf(2));
		assertEquals(":zzz0, :zzz1", dbc.value("zzz",l).generateQuery());
		assertEquals("v1", dbc.getQueryParameters().get("zzz0").getValue());
		assertEquals(Integer.valueOf(2), dbc.getQueryParameters().get("zzz1").getValue());

	}

	public void testUpdateSet() {
		Boy b = new Boy();

		DbSqlBuilder dbc = sql().set("b", b).table("Boy", "b");
		assertEquals("set b.GIRL_ID=:boy.girlId, b.ID=:boy.id BOY b", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());

		dbc = sql().set("Boy", b).table("Boy", null);
		assertEquals("set BOY.GIRL_ID=:boy.girlId, BOY.ID=:boy.id BOY", dbc.generateQuery());
		assertEquals(2, dbc.getQueryParameters().size());


		dbc = sql().setAll("b", b).table("Boy", "b");
		assertEquals("set b.GIRL_ID=:boy.girlId, b.ID=:boy.id, b.NAME=:boy.name BOY b", dbc.generateQuery());
		assertEquals(3, dbc.getQueryParameters().size());

		BadBoy bb = new BadBoy();

		dbc = sql().set("b", bb).table("BadBoy", "b");
		assertEquals("set BOY b", dbc.generateQuery());

		dbc = sql().set("BadBoy", bb).table("BadBoy", null);
		assertEquals("set BOY", dbc.generateQuery());

		dbc = sql().setAll("b", bb).table("BadBoy", "b");
		assertEquals("set b.ID=:badBoy.ajdi, b.GIRL_ID=:badBoy.girlId, b.NAME=:badBoy.nejm BOY b", dbc.generateQuery());
	}

	public void testStrings() {
		DbSqlBuilder dbc = sql()._("123")._("xxx");
		assertEquals("123xxx", dbc.generateQuery());
	}

	public void testWhere() {
		{
			Boy b = new Boy();
			DbSqlBuilder dbc = sql().match("Boy", b)._(" ").table(b, null);
			assertEquals("(BOY.GIRL_ID=:boy.girlId and BOY.ID=:boy.id) BOY", dbc.generateQuery());
			dbc = sql().match("b", b)._(" ").table(b, "b");
			assertEquals("(b.GIRL_ID=:boy.girlId and b.ID=:boy.id) BOY b", dbc.generateQuery());
		}
		{
			BadBoy bb = new BadBoy();
			DbSqlBuilder dbc = sql().match("BadBoy", bb).table(bb, null);
			assertEquals("(1=1) BOY", dbc.generateQuery());
			dbc = sql().match("b", bb).table(bb, "b");
			assertEquals("(1=1) BOY b", dbc.generateQuery());
		}
		{
			BadBoy bb = new BadBoy();
			DbSqlBuilder dbc = sql()._("where ").match("BadBoy", bb)._(" ").table(bb, null);
			assertEquals("where (1=1) BOY", dbc.generateQuery());
			dbc = sql()._("where")._().match("b", bb)._(" ").table(bb, "b");
			assertEquals("where (1=1) BOY b", dbc.generateQuery());

			dbc = sql()._("where")._().match("BadBoy", bb).table(bb, null);
			assertEquals("where (1=1) BOY", dbc.generateQuery());
			dbc = sql()._("where ").match("b", bb).table(bb, "b");
			assertEquals("where (1=1) BOY b", dbc.generateQuery());

			bb.ajdi = Integer.valueOf(3);
			dbc = sql()._("where ").match("BadBoy", bb)._(" ").table(bb, null);
			assertEquals("where (BOY.ID=:badBoy.ajdi) BOY", dbc.generateQuery());
			dbc = sql()._("where ").match("b", bb)._(" ").table(bb, "b");
			assertEquals("where (b.ID=:badBoy.ajdi) BOY b", dbc.generateQuery());
		}
	}

	public void testCriteria() {
		BadBoy bb = new BadBoy();
		BadGirl bg = new BadGirl();

		DbSqlBuilder dbc = sql()._("select").
				columnsAll("bb").columnsIds("bg")._(" from").
				table(bb, "bb").table(bg, "bg")._().
				match("bb", bb)._().match("bg", bg);

		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg (1=1) (1=1)", dbc.generateQuery());

		dbc = sql()._("select").
				columnsAll("bb").columnsIds("bg")._(" from").
				table(bb, "bb").table(bg, "bg")._(" where ").
				match("bb", bb)._().match("bg", bg)._(" or ").refId("bb")._("=").value(Long.valueOf(5L));


		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg where (1=1) (1=1) or bb.ID=:p0", dbc.generateQuery());

		dbc.reset();
		bb.ajdi = bg.fooid = Integer.valueOf(1);
		assertEquals("select bb.ID, bb.GIRL_ID, bb.NAME, bg.ID from BOY bb, GIRL bg where (bb.ID=:badBoy.ajdi) (bg.ID=:badGirl.fooid) or bb.ID=:p0", dbc.generateQuery());
	}

}
