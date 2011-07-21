// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.db.oom.test.BadBoy;
import jodd.db.oom.test.BadGirl;
import jodd.db.oom.test.Boy;
import jodd.db.oom.test.Girl;
import junit.framework.TestCase;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;

public class DbSqlTemplateWithPrefixTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.reset();

		dbOom.setTableNamePrefix("PRE_");
		dbOom.setTableNameSuffix("_SUF");

		dbOom.registerType(Boy.class);
		dbOom.registerType(BadBoy.class);
		dbOom.registerType(BadGirl.class);
		dbOom.registerType(Girl.class);
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.reset();
	}


	public void testTablePrefixSuffix() {

		DbSqlBuilder st;

		st = sql("$T{Boy} $Boy.id $C{Boy.id}");
		assertEquals("PRE_BOY_SUF PRE_BOY_SUF.ID PRE_BOY_SUF.ID", st.generateQuery());

		st = sql("$T{Boy b} $b.id $C{b.id}");
		assertEquals("PRE_BOY_SUF b b.ID b.ID", st.generateQuery());

		DbOomManager.getInstance().setTableNamePrefix(null);
		DbOomManager.getInstance().setTableNameSuffix(null);
	}

}
