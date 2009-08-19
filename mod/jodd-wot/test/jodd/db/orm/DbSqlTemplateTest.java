// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import static jodd.db.orm.ColumnAliasType.*;
import junit.framework.TestCase;
import jodd.db.orm.test.Boy;
import jodd.db.orm.test.BadBoy;
import jodd.db.orm.test.BadGirl;
import jodd.db.orm.test.Girl;
import jodd.db.orm.sqlgen.DbSqlBuilder;
import jodd.db.orm.sqlgen.ParameterValue;
import static jodd.db.orm.sqlgen.DbSqlBuilder.sql;

import java.util.Map;

public class DbSqlTemplateTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DbOrmManager dbOrm = DbOrmManager.getInstance();
		dbOrm.reset();
		dbOrm.registerType(Boy.class);
		dbOrm.registerType(BadBoy.class);
		dbOrm.registerType(BadGirl.class);
		dbOrm.registerType(Girl.class);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		DbOrmManager dbOrm = DbOrmManager.getInstance();
		dbOrm.reset();
	}
		
	protected void assertContains(String string, String... chunks) {
		for (String chunk : chunks) {
			//noinspection SimplifiableJUnitAssertion
			assertTrue(string.indexOf(chunk) != -1);
		}
	}


	public void testTables() {
		DbSqlBuilder st;

		// 1
		st = sql("$T{Boy}");
		assertEquals("BOY Boy", st.generateQuery());

		st = sql("$T{BadBoy}");
		assertEquals("BOY BadBoy", st.generateQuery());

		st = sql("$T{Boy b}");
		assertEquals("BOY b", st.generateQuery());

		st = sql("$T{BadBoy b}");
		assertEquals("BOY b", st.generateQuery());

		// 2
		st = sql("$T{b}").use("b", new Boy());
		assertEquals("BOY b", st.generateQuery());
		st = sql("$T{b bx}").use("b", new Boy());
		assertEquals("BOY bx", st.generateQuery());

		st = sql("$T{b}").use("b", new BadBoy());
		assertEquals("BOY b", st.generateQuery());

		// 3
		st = sql("$T{b}").use("b", Boy.class);
		assertEquals("BOY b", st.generateQuery());

		st = sql("$T{b}").use("b", BadBoy.class);
		assertEquals("BOY b", st.generateQuery());
	}


	public void testManyTables() {
		DbSqlBuilder st = sql("$T{Boy, Girl girl}");
		assertEquals("BOY Boy, GIRL girl", st.generateQuery());
	}

	public void testColumns1() {
		DbSqlBuilder st;

		// 1
		st = sql("$T{Boy} | $C{Boy.id} | $C{Boy.*}");
		assertEquals("BOY Boy | Boy.ID | Boy.GIRL_ID, Boy.ID, Boy.NAME", st.generateQuery());

		st = sql("$T{BadBoy} | $C{BadBoy.ajdi} | $C{BadBoy.*} | $C{BadBoy.+}");
		assertEquals("BOY BadBoy | BadBoy.ID | BadBoy.ID, BadBoy.GIRL_ID, BadBoy.NAME | BadBoy.ID", st.generateQuery());

		// 2
		st = sql("$T{b b} | $C{b.id}").use("b", new Boy());
		assertEquals("BOY b | b.ID", st.generateQuery());

		st = sql("$T{b bb} | $C{bb.id} | $C{bb.*}").use("b", new Boy());
		assertEquals("BOY bb | bb.ID | bb.GIRL_ID, bb.ID, bb.NAME", st.generateQuery());

		st = sql("$T{b bb} | $C{bb.ajdi} | $C{bb.*} | $C{bb.+}").use("b", new BadBoy());
		assertEquals("BOY bb | bb.ID | bb.ID, bb.GIRL_ID, bb.NAME | bb.ID", st.generateQuery());

		// 3
		st = sql("$T{b b} | $C{b.id}").use("b", Boy.class);
		assertEquals("BOY b | b.ID", st.generateQuery());

		st = sql("$T{b bb} | $C{bb.id} | $C{bb.*}").use("b", Boy.class);
		assertEquals("BOY bb | bb.ID | bb.GIRL_ID, bb.ID, bb.NAME", st.generateQuery());

		st = sql("$T{b bb} | $C{bb.ajdi} | $C{bb.*}").use("b", BadBoy.class);
		assertEquals("BOY bb | bb.ID | bb.ID, bb.GIRL_ID, bb.NAME", st.generateQuery());

		st = sql("$T{b bb} | $C{bb.ajdi} | $C{bb.*} | $C{bb.+}").use("b", BadBoy.class);
		assertEquals("BOY bb | bb.ID | bb.ID, bb.GIRL_ID, bb.NAME | bb.ID", st.generateQuery());

	}


	public void testColumns2() {
		DbSqlBuilder st;

		// 1
		st = sql("$T{Boy} | $C{Boy.id} | $C{Boy.*}");
		assertEquals("BOY Boy | Boy.ID as BOY$ID | Boy.GIRL_ID as BOY$GIRL_ID, Boy.ID as BOY$ID, Boy.NAME as BOY$NAME",
				st.aliasColumnsAs(TABLE_NAME).generateQuery());
		assertEquals("BOY Boy | Boy.ID as Boy$ID | Boy.GIRL_ID as Boy$GIRL_ID, Boy.ID as Boy$ID, Boy.NAME as Boy$NAME",
				st.reset().aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("BOY Boy | Boy.ID as col_0_ | Boy.GIRL_ID as col_1_, Boy.ID as col_2_, Boy.NAME as col_3_",
				st.reset().aliasColumnsAs(COLUMN_CODE).generateQuery());

		st = sql("$T{BadBoy} | $C{BadBoy.ajdi} | $C{BadBoy.*}");
		assertEquals("BOY BadBoy | BadBoy.ID as BOY$ID | BadBoy.ID as BOY$ID, BadBoy.GIRL_ID as BOY$GIRL_ID, BadBoy.NAME as BOY$NAME",
				st.aliasColumnsAs(TABLE_NAME).generateQuery());
		assertEquals("BOY BadBoy | BadBoy.ID as BadBoy$ID | BadBoy.ID as BadBoy$ID, BadBoy.GIRL_ID as BadBoy$GIRL_ID, BadBoy.NAME as BadBoy$NAME",
				st.reset().aliasColumnsAs(TABLE_REFERENCE).generateQuery());
		assertEquals("BOY BadBoy | BadBoy.ID as col_0_ | BadBoy.ID as col_1_, BadBoy.GIRL_ID as col_2_, BadBoy.NAME as col_3_",
				st.reset().aliasColumnsAs(COLUMN_CODE).generateQuery());

	}



	public void testColumns3() {
		DbSqlBuilder st;

		st = sql("$C{b.id,b.name} from $T{b b}").use("b", Boy.class);
		assertEquals("b.ID, b.NAME from BOY b", st.generateQuery());

		st = sql("$C{    a.ajdi ,  a.nejm     } from $T{b a}").use("b", BadBoy.class);
		assertEquals("a.ID, a.NAME from BOY a", st.generateQuery());

		st = sql("$C{b.id,b.name} from $T{b b}").use("b", Boy.class);
		assertEquals("b.ID as BOY$ID, b.NAME as BOY$NAME from BOY b", st.aliasColumnsAs(TABLE_NAME).generateQuery());
		st = sql("$C{    a.ajdi ,  a.nejm     } from $T{b a}").use("b", BadBoy.class);
		assertEquals("a.ID as BOY$ID, a.NAME as BOY$NAME from BOY a", st.aliasColumnsAs(TABLE_NAME).generateQuery());


		st = sql("$b.id as d, $C{b.name} from $T{b}").use("b", Boy.class).aliasColumnsAs(TABLE_NAME);
		assertEquals("b.ID as d, b.NAME as BOY$NAME from BOY b", st.generateQuery());
		st = sql("$a.ajdi as d, $C{    a.nejm     } from $T{b a}").use("b", BadBoy.class).aliasColumnsAs(TABLE_NAME);
		assertEquals("a.ID as d, a.NAME as BOY$NAME from BOY a", st.generateQuery());

		st = sql("$C{b.*} from $T{b}").use("b", Boy.class).aliasColumnsAs(TABLE_NAME);
		assertEquals("b.GIRL_ID as BOY$GIRL_ID, b.ID as BOY$ID, b.NAME as BOY$NAME from BOY b", st.generateQuery());
		st = sql("$C{a.*} from $T{b a}").use("b", BadBoy.class);
		assertEquals("a.ID, a.GIRL_ID, a.NAME from BOY a", st.generateQuery());

		st = sql("$C{a.*} from $T{BadBoy a}");
		assertEquals("a.ID, a.GIRL_ID, a.NAME from BOY a", st.generateQuery());
		st = sql("$C{BadBoy.ajdi} from $T{BadBoy}");
		assertEquals("BadBoy.ID from BOY BadBoy", st.generateQuery());
	}

	public void testReferencesAndEscapes() {
		DbSqlBuilder st;

		assertEquals("...$foo...", new DbSqlBuilder("...\\$foo...").generateQuery());
		assertEquals("$foo", new DbSqlBuilder("\\$foo").generateQuery());
		assertEquals("...\\$foo...", new DbSqlBuilder("...\\\\\\$foo...").generateQuery());
		assertEquals("\\$foo", new DbSqlBuilder("\\\\\\$foo").generateQuery());
		assertEquals("$f", new DbSqlBuilder("\\$f").generateQuery());


		st = sql("$T{BadBoy b} x$b.ajdi=2").aliasColumnsAs(TABLE_NAME);
		assertEquals("BOY b xb.ID=2", st.generateQuery());

		st = sql("$T{BadBoy b} $b.ajdi+2").aliasColumnsAs(TABLE_NAME);
		assertEquals("BOY b b.ID+2", st.generateQuery());

		st = sql("$T{Boy, BadBoy b} - \\$$Boy.id $b.ajdi");
		assertEquals("BOY Boy, BOY b - $Boy.ID b.ID", st.generateQuery());

		st = sql("$T{Boy}, $T{BadBoy b} - \\$$Boy.id $b.ajdi");
		assertEquals("BOY Boy, BOY b - $Boy.ID b.ID", st.generateQuery());

		st = sql("$C{b.ajdi} $T{Boy, BadBoy b} - \\$$Boy.id $b.ajdi").aliasColumnsAs(TABLE_NAME);
		assertEquals("b.ID as BOY$ID BOY Boy, BOY b - $Boy.ID b.ID", st.generateQuery());

		st = sql("\\$C{b.ajdi} $T{Boy, BadBoy b} - \\$$Boy.id $b.ajdi");
		assertEquals("$C{b.ajdi} BOY Boy, BOY b - $Boy.ID b.ID", st.generateQuery());

		st = sql("\\$C{b.*} $T{Boy, BadBoy b} - $Boy.id");
		assertEquals("$C{b.*} BOY Boy, BOY b - Boy.ID", st.generateQuery());

		st = sql("$C{b.*} $T{Boy, BadBoy  b} - $b.ajdi 'foo\\$'").aliasColumnsAs(TABLE_NAME);
		assertEquals("b.ID as BOY$ID, b.GIRL_ID as BOY$GIRL_ID, b.NAME as BOY$NAME BOY Boy, BOY b - b.ID 'foo$'", st.generateQuery());

		st = sql("$T{BadBoy  b} - $b.ajdi=2,$b.ajdi<$b.ajdi").aliasColumnsAs(TABLE_NAME);
		assertEquals("BOY b - b.ID=2,b.ID<b.ID", st.generateQuery());
	}

	

	public void testMatch() {
		DbSqlBuilder st;

		Boy boy = new Boy();
		boy.id = 1;
		boy.girlId = 3;
		st = sql("$T{boy} where $M{boy=boy}").use("boy", boy);
		assertEquals("BOY boy where (boy.GIRL_ID=:boy.girlId and boy.ID=:boy.id)", st.generateQuery());
		Map<String, ParameterValue> map = st.getQueryParameters();
		assertEquals(2, map.size());
		assertEquals(Integer.valueOf(1), map.get("boy.id").getValue());
		assertEquals(Integer.valueOf(3), map.get("boy.girlId").getValue());


		boy.id = 4;
		boy.girlId = 1;
		st = sql("$T{boy b} where $M{b=boy}").use("boy", boy);
		assertEquals("BOY b where (b.GIRL_ID=:boy.girlId and b.ID=:boy.id)", st.generateQuery());
		map = st.getQueryParameters();
		assertEquals(2, map.size());
		assertEquals(Integer.valueOf(4), map.get("boy.id").getValue());
		assertEquals(Integer.valueOf(1), map.get("boy.girlId").getValue());

		BadBoy badBoy = new BadBoy();
		st = sql("$T{boy b} where $M{b=boy}").use("boy", badBoy);
		assertEquals("BOY b where (1=1)", st.generateQuery());
		map = st.getQueryParameters();
		assertNull(map);
		
		st = sql("$T{boy b} where $M{b=boy}").use("boy", badBoy);
		assertEquals("BOY b where (1=1)", st.generateQuery());
		map = st.getQueryParameters();
		assertNull(map);


		badBoy.ajdi = Integer.valueOf(4);
		badBoy.girlId = Integer.valueOf(1);
		st = sql("$T{boy b} where $M{b=boy}").use("boy", badBoy);
		assertEquals("BOY b where (b.ID=:badBoy.ajdi and b.GIRL_ID=:badBoy.girlId)", st.generateQuery());
		map = st.getQueryParameters();
		assertEquals(2, map.size());
		assertEquals(Integer.valueOf(4), map.get("badBoy.ajdi").getValue());
		assertEquals(Integer.valueOf(1), map.get("badBoy.girlId").getValue());

		badBoy.ajdi = null;
		st = sql("$T{boy b} where $M{b=boy.*}").use("boy", badBoy);
		assertEquals("BOY b where (b.ID=:badBoy.ajdi and b.GIRL_ID=:badBoy.girlId and b.NAME=:badBoy.nejm)", st.generateQuery());
		map = st.getQueryParameters();
		assertEquals(3, map.size());
		assertEquals(Integer.valueOf(1), map.get("badBoy.girlId").getValue());
	}


	public void testJoin() {
		DbSqlBuilder st = sql("select $C{bb.*}, $C{bg.+} from $T{BadGirl bg} join $T{Boy bb} on $bg.+=bb.GIRL_ID");
		assertEquals("select bb.GIRL_ID, bb.ID, bb.NAME, bg.ID from GIRL bg join BOY bb on bg.ID=bb.GIRL_ID", st.generateQuery());
	}

}
