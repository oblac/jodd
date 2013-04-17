// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.tst.Boy;
import jodd.db.oom.tst.Girl2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EntityCacheTest extends DbHsqldbTestCase {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Girl2.class);
		dbOom.registerEntity(Boy.class);
	}

	@Test
	@SuppressWarnings("SimplifiableJUnitAssertion")
	public void testJoin() {
		DbSession session = new DbThreadSession(cp);

		assertEquals(1, DbEntitySql.insert(new Girl2(1, "Anna", "seduction")).query().executeUpdateAndClose());
		assertEquals(1, DbEntitySql.insert(new Girl2(2, "Sandra", "spying")).query().executeUpdateAndClose());
		assertEquals(1, DbEntitySql.insert(new Boy(1, "Johny", 2)).query().executeUpdateAndClose());
		assertEquals(1, DbEntitySql.insert(new Boy(2, "Marco", 2)).query().executeUpdateAndClose());

		// no hints

		DbOomQuery q = new DbOomQuery(sql("select $C{g.id, g.name, g.speciality}, $C{b.*} from $T{Girl2 g} join $T{Boy b} on $g.id = $b.girlId"));
		q.cacheEntities(true);

		List<Object[]> result = q.listAndClose(Girl2.class, Boy.class);

		assertEquals(2, result.size());

		Girl2 girl1 = (Girl2) result.get(0)[0];
		Girl2 girl2 = (Girl2) result.get(1)[0];

		assertTrue(girl1.equals(girl2));
		assertTrue(girl1 == girl2);

		Boy boy1 = (Boy) result.get(0)[1];
		Boy boy2 = (Boy) result.get(1)[1];

		assertTrue(boy1.id != boy2.id);
		assertFalse(boy1 == boy2);


		// use hints!

		q = new DbOomQuery(sql("select $C{g.id, g.name, g.speciality}, $C{b.*} from $T{Girl2 g} join $T{Boy b} on $g.id = $b.girlId"));

		List<Girl2> result2 = q.withHints("g", "g.boys").cacheEntities(true).listAndClose(Girl2.class, Boy.class);

		assertEquals(2, result2.size());

		girl1 = result2.get(0);
		girl2 = result2.get(1);

		assertTrue(girl1.equals(girl2));
		assertTrue(girl1 == girl2);

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		session.closeSession();
	}

}