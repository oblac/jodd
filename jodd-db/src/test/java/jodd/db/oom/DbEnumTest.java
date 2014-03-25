// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbHsqldbTestCase;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.tst.Enumerator;
import org.junit.Before;
import org.junit.Test;

import static jodd.db.oom.sqlgen.DbEntitySql.insert;

public class DbEnumTest extends DbHsqldbTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Enumerator.class);
	}

	@Test
	public void testEnums() {
		DbSession session = new DbThreadSession(cp);

		String sql = "create table ENUMERATOR(ID int, NAME varchar(20), STATUS int)";

		DbQuery query = new DbQuery(sql);
		query.executeUpdate();

		Enumerator e = new Enumerator();
		e.id = 2;
		e.name = "Ikigami";
		e.status = Enumerator.STATUS.ONE;

		DbSqlGenerator gen = insert(e);
		query = new DbOomQuery(gen);
		query.executeUpdate();

		session.closeSession();
	}

}