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

package jodd.db;

import jodd.util.collection.IntArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DbQueryTest {

	static class DbQueryEx extends DbQueryParser {
		public DbQueryEx() {
			super("");
		}

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

	@Test
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
