// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.db.oom.tst.BadBoy;
import jodd.db.oom.tst.BadGirl;
import jodd.db.oom.tst.Boy;
import jodd.db.oom.tst.Girl;
import junit.framework.TestCase;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

public class DbSqlTemplateWithPrefixTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();

		dbOom.getTableNames().setPrefix("PRE_");
		dbOom.getTableNames().setSuffix("_SUF");

		dbOom.registerType(Boy.class);
		dbOom.registerType(BadBoy.class);
		dbOom.registerType(BadGirl.class);
		dbOom.registerType(Girl.class);
	}

	public void testTablePrefixSuffix() {
		DbSqlBuilder st;

		st = sql("$T{Boy} $Boy.id $C{Boy.id}");
		assertEquals("PRE_BOY_SUF PRE_BOY_SUF.ID PRE_BOY_SUF.ID", st.generateQuery());

		st = sql("$T{Boy b} $b.id $C{b.id}");
		assertEquals("PRE_BOY_SUF b b.ID b.ID", st.generateQuery());
	}

}