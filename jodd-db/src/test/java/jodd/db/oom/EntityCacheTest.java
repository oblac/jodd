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
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.fixtures.Boy;
import jodd.db.oom.fixtures.Girl2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class EntityCacheTest extends DbHsqldbTestCase {

	public static final String TSQL =
			"select $C{g.id, g.name, g.speciality}, $C{b.*} from " +
			"$T{Girl2 g} join $T{Boy b} on $g.id = $b.girlId " +
			"order by $g.id desc";

	public static final String TSQL_LEFT =
			"select $C{g.id, g.name, g.speciality}, $C{b.*} from " +
			"$T{Girl2 g} left join $T{Boy b} on $g.id = $b.girlId " +
			"order by $g.id desc";


	DbSession dbSession;

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Girl2.class);
		dbOom.registerEntity(Boy.class);

		dbSession = new DbThreadSession(cp);

		assertEquals(1, DbEntitySql.insert(new Girl2(1, "Anna", "swim")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Girl2(2, "Sandra", "piano")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Girl2(3, "Emma", "nothing")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Boy(1, "Johny", 2)).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Boy(2, "Marco", 2)).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Boy(3, "Hugo", 1)).query().executeUpdate());
	}

	@AfterEach
	public void tearDown() throws Exception {
		dbSession.closeSession();
		super.tearDown();
	}

	@Test
	public void testMapRows2Types_useCache_noHints() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		List<Object[]> result = q.cacheEntities(true).list(Girl2.class, Boy.class);

		assertEquals(3, result.size());

		Girl2 girl1 = (Girl2) result.get(0)[0];
		Girl2 girl2 = (Girl2) result.get(1)[0];
		Girl2 girl3 = (Girl2) result.get(2)[0];

		assertTrue(girl1.equals(girl2));
		assertTrue(girl1 == girl2);
		assertFalse(girl3 == girl1);

		Boy boy1 = (Boy) result.get(0)[1];
		Boy boy2 = (Boy) result.get(1)[1];
		Boy boy3 = (Boy) result.get(2)[1];

		assertTrue(boy1.id != boy2.id);
		assertFalse(boy1 == boy2);
		assertFalse(boy2 == boy3);

		assertNull(girl1.getBoys());
		assertNull(girl3.getBoys());
	}

	@Test
	public void testMapRows2Types_useCache_noHints_LEFT() {

		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		List<Object[]> result = q.cacheEntities(true).list(Girl2.class, Boy.class);

		assertEquals(4, result.size());

		Girl2 girl0 = (Girl2) result.get(0)[0];
		Girl2 girl1 = (Girl2) result.get(1)[0];
		Girl2 girl2 = (Girl2) result.get(2)[0];
		Girl2 girl3 = (Girl2) result.get(3)[0];

		assertEquals("Emma", girl0.name);
		assertTrue(girl1.equals(girl2));
		assertTrue(girl1 == girl2);
		assertFalse(girl3 == girl1);

		Boy boy0 = (Boy) result.get(0)[1];
		Boy boy1 = (Boy) result.get(1)[1];
		Boy boy2 = (Boy) result.get(2)[1];
		Boy boy3 = (Boy) result.get(3)[1];

		assertNull(boy0);
		assertTrue(boy1.id != boy2.id);
		assertFalse(boy1 == boy2);
		assertFalse(boy2 == boy3);

		assertNull(girl1.getBoys());
		assertNull(girl3.getBoys());
	}

	@Test
	public void testMapRows2Types_useCache_useHints_1perRow() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		List<Girl2> result2 = q.withHints("g", "g.boys").cacheEntities(true).list(Girl2.class, Boy.class);

		assertEquals(3, result2.size());

		Girl2 girl1 = result2.get(0);
		Girl2 girl2 = result2.get(1);
		Girl2 girl3 = result2.get(2);

		assertTrue(girl1.equals(girl2));
		assertTrue(girl1 == girl2);
		assertFalse(girl3 == girl1);

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		assertNotNull(girl3.getBoys());
		assertEquals(1, girl3.getBoys().size());
		assertEquals("Hugo", girl3.getBoys().get(0).name);
	}

	@Test
	public void testMapRows2Types_useCache_useHints_1perRow_LEFT() {
		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		List<Girl2> result2 = q.withHints("g", "g.boys").cacheEntities(true).list(Girl2.class, Boy.class);

		assertEquals(4, result2.size());

		Girl2 girl0 = result2.get(0);
		Girl2 girl1 = result2.get(1);
		Girl2 girl2 = result2.get(2);
		Girl2 girl3 = result2.get(3);

		assertTrue(girl1.equals(girl2));
		assertTrue(girl1 == girl2);
		assertFalse(girl3 == girl1);

		assertNull(girl0.getBoys());
		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		assertNotNull(girl3.getBoys());
		assertEquals(1, girl3.getBoys().size());
		assertEquals("Hugo", girl3.getBoys().get(0).name);
	}

	@Test
	public void testMapRows2Types_entityAware() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		List<Girl2> result2 = q.withHints("g", "g.boys").entityAwareMode(true).list(Girl2.class, Boy.class);

		assertEquals(2, result2.size());

		Girl2 girl1 = result2.get(0);
		Girl2 girl3 = result2.get(1);

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		assertNotNull(girl3.getBoys());
		assertEquals(1, girl3.getBoys().size());
	}

	@Test
	public void testMapRows2Types_entityAware_LEFT() {
		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		List<Girl2> result2 = q.withHints("g", "g.boys").entityAwareMode(true).list(Girl2.class, Boy.class);

		assertEquals(3, result2.size());

		Girl2 girl0 = result2.get(0);
		Girl2 girl1 = result2.get(1);
		Girl2 girl3 = result2.get(2);

		assertNull(girl0.getBoys());

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		assertNotNull(girl3.getBoys());
		assertEquals(1, girl3.getBoys().size());
	}

	@Test
	public void testMapRows2Types_entityAware_List() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		List<Girl2> result2 = q.withHints("g", "g.boys").entityAwareMode(true).list(1, Girl2.class, Boy.class);

		assertEquals(1, result2.size());

		Girl2 girl1 = result2.get(0);

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());
	}

	@Test
	public void testMapRows2Types_entityAware_List_LEFT() {
		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		List<Girl2> result2 = q.withHints("g", "g.boys").entityAwareMode(true).list(2, Girl2.class, Boy.class);

		assertEquals(2, result2.size());

		Girl2 girl0 = result2.get(0);
		Girl2 girl1 = result2.get(1);

		assertNull(girl0.getBoys());
		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());
	}

	@Test
	public void testMapRows2Types_entityAware_Set() {

		DbOomQuery q = new DbOomQuery(sql(TSQL));

		Set<Girl2> set1 = q.withHints("g", "g.boys").entityAwareMode(true).listSet(Girl2.class, Boy.class);

		assertEquals(2, set1.size());

		for (Girl2 girl : set1) {
			if (girl.id.equals(1)) {
				assertEquals(1, girl.getBoys().size());
			}
			if (girl.id.equals(2)) {
				assertEquals(2, girl.getBoys().size());
			}
		}
	}

	@Test
	public void testMapRows2Types_entityAware_Set_LEFT() {

		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		Set<Girl2> set1 = q.withHints("g", "g.boys").entityAwareMode(true).listSet(Girl2.class, Boy.class);

		assertEquals(3, set1.size());

		for (Girl2 girl : set1) {
			if (girl.id.equals(1)) {
				assertEquals(1, girl.getBoys().size());
			}
			if (girl.id.equals(2)) {
				assertEquals(2, girl.getBoys().size());
			}
			if (girl.id.equals(3)) {
				assertNull(girl.getBoys());
			}
		}
	}


	@Test
	public void testMapRows2Types_entityAware_Max() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		Set<Girl2> set1 = q.withHints("g", "g.boys").entityAwareMode(true).listSet(1, Girl2.class, Boy.class);

		assertEquals(1, set1.size());

		for (Girl2 girl : set1) {
			if (girl.id.equals(2)) {
				assertEquals(2, girl.getBoys().size());
			} else {
				fail("error");
			}
		}
	}

	@Test
	public void testMapRows2Types_entityAware_Max_LEFT() {
		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		Set<Girl2> set1 = q.withHints("g", "g.boys").entityAwareMode(true).listSet(2, Girl2.class, Boy.class);

		assertEquals(2, set1.size());

		for (Girl2 girl : set1) {
			if (girl.id.equals(3)) {
				assertNull(girl.getBoys());
			} else if (girl.id.equals(2)) {
				assertEquals(2, girl.getBoys().size());
			} else {
				fail("error");
			}
		}
	}

	@Test
	public void testMapRows2Types_entityAware_Iterator() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		Iterator<Girl2> iterator = q.withHints("g", "g.boys").entityAwareMode(true).iterate(Girl2.class, Boy.class);

		assertTrue(iterator.hasNext());
		assertTrue(iterator.hasNext());
		assertTrue(iterator.hasNext());

		Girl2 girl1 = iterator.next();

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		assertTrue(iterator.hasNext());

		Girl2 girl3 = iterator.next();

		assertNotNull(girl3.getBoys());
		assertEquals(1, girl3.getBoys().size());

		assertFalse(iterator.hasNext());
	}

	@Test
	public void testMapRows2Types_entityAware_Iterator_LEFT() {
		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		Iterator<Girl2> iterator = q.withHints("g", "g.boys").entityAwareMode(true).iterate(Girl2.class, Boy.class);

		assertTrue(iterator.hasNext());

		Girl2 girl0 = iterator.next();
		assertNull(girl0.getBoys());

		Girl2 girl1 = iterator.next();

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());

		assertTrue(iterator.hasNext());

		Girl2 girl3 = iterator.next();

		assertNotNull(girl3.getBoys());
		assertEquals(1, girl3.getBoys().size());

		assertFalse(iterator.hasNext());
	}

	@Test
	public void testMapRows2Types_entityAware_Find() {
		DbOomQuery q = new DbOomQuery(sql(TSQL));

		Girl2 girl1 = q.withHints("g", "g.boys").entityAwareMode(true).find(Girl2.class, Boy.class);

		assertNotNull(girl1.getBoys());
		assertEquals(2, girl1.getBoys().size());
	}

	@Test
	public void testMapRows2Types_entityAware_Find_LEFT() {
		DbOomQuery q = new DbOomQuery(sql(TSQL_LEFT));

		Girl2 girl0 = q.withHints("g", "g.boys").entityAwareMode(true).find(Girl2.class, Boy.class);

		assertNull(girl0.getBoys());
	}

}
