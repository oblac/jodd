// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import junit.framework.TestCase;

import jodd.util.collection.IntArrayList;

public class DbQueryTest extends TestCase {

	static class DbQueryEx extends DbQueryParser {
		String prepare(String sql) {
			super.parseSql(sql);
			return this.sql;
		}
	}


	private void doTestSingleNamedParam(DbQueryEx dbp, String paramName, int position) {
		IntArrayList list = dbp.lookupNamedParameterIndices(paramName);
		assertEquals(1, list.size());
		assertEquals(position, list.get(0));
		assertTrue(dbp.prepared);
	}

	private void doTestDoubleNamedParam(DbQueryEx dbp, String paramName, int position1, int position2) {
		IntArrayList list = dbp.lookupNamedParameterIndices(paramName);
		assertEquals(2, list.size());
		assertEquals(position1, list.get(0));
		assertEquals(position2, list.get(1));
		assertTrue(dbp.prepared);
	}

	public void testPrepareSql() {
		DbQueryEx dbp = new DbQueryEx();
		assertEquals("aaa", dbp.prepare("aaa"));
		assertFalse(dbp.prepared);
		assertEquals("", dbp.prepare(""));
		assertFalse(dbp.prepared);
		assertEquals("aaa ? aa", dbp.prepare("aaa ? aa"));
		assertTrue(dbp.prepared);
		assertEquals("? aaa ? aa ?", dbp.prepare("? aaa ? aa ?"));
		assertTrue(dbp.prepared);
		assertEquals("aaa ? aa ? x ? x", dbp.prepare("aaa ? aa ? x ? x"));
		assertTrue(dbp.prepared);

		assertEquals("aaa ? aa", dbp.prepare("aaa :x aa"));
		doTestSingleNamedParam(dbp, "x", 1);

		assertEquals("? aaa ?", dbp.prepare(":x aaa :y"));
		doTestSingleNamedParam(dbp, "x", 1);
		doTestSingleNamedParam(dbp, "y", 2);

		assertEquals("? aaa ?", dbp.prepare(":xxx aaa :x"));
		doTestSingleNamedParam(dbp, "xxx", 1);
		doTestSingleNamedParam(dbp, "x", 2);

		assertEquals("aaa ? aaa ? aaa ? aa ? aaa ?", dbp.prepare("aaa :x aaa :y aaa ? aa :x aaa ?"));
		doTestDoubleNamedParam(dbp, "x", 1, 4);
		doTestSingleNamedParam(dbp, "y", 2);

		assertEquals("aaa ? aaa ? aaa ? aa ? aaa ?", dbp.prepare("aaa :x aaa ?1 aaa ? aa :x aaa ?1"));
		doTestDoubleNamedParam(dbp, "1", 2, 5);
		doTestDoubleNamedParam(dbp, "x", 1, 4);

		assertTrue(dbp.prepared);

	}
}
