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

import jodd.db.fixtures.DbHsqldbTestCase;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DbMiscTest extends DbHsqldbTestCase {

	@Test
	public void testBig() throws Exception {
		DbSession session = new DbSession(cp);

		DbQuery query = new DbQuery(session, "girlCount");
		assertEquals(0, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(1, 'Anna', 'swim')"));
		assertEquals(1, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(2, 'Sandra', 'piano')"));
		assertEquals(2, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(3, 'Monica', 'hacking')"));
		assertEquals(3, query.executeCount());
		assertEquals(0, query.getOpenResultSetCount());
		//	assertEquals(0, DbQuery.totalOpenResultSetCount);
		query.close();


		//  play with the query

		String sql = "select * from GIRL where ID = :id";
		query = new DbQuery(session, sql);
		query.setDebugMode();
		query.setInteger("id", 2);
		ResultSet rs = query.execute();
		assertEquals(1, query.getOpenResultSetCount());
		//	assertEquals(1, DbQuery.totalOpenResultSetCount);

		assertEquals("select * from GIRL where ID = 2", query.getQueryString());
		while (rs.next()) {
			assertEquals(2, rs.getInt(1));
			assertEquals("Sandra", rs.getString(2));
			assertEquals("piano", rs.getString(3));
		}
		assertFalse(query.isClosed());
		session.closeSession();
		assertTrue(query.isClosed());
		assertEquals(0, query.getOpenResultSetCount());

		//	assertEquals(0, DbQuery.totalOpenResultSetCount);


		// thread dbsession

		DbSession dbts = new DbThreadSession(cp);
		DbQuery q = new DbQuery("select count(*) from GIRL");
		assertEquals(3, q.executeCount());
		dbts.closeSession();

		assertNull(DbThreadSession.getCurrentSession());

		// transaction example

		DbSession session1 = new DbSession(cp);
		DbSession session2 = new DbSession(cp);

		session1.beginTransaction(new DbTransactionMode().setReadOnly(false));
		query = new DbQuery(session1, "insert into GIRL values(4, 'Jeniffer', 'fighting')");
		assertEquals(1, query.executeUpdate());
		query.close();


		//
		// In the new implementation (HSQLDB), all isolation levels avoid the "dirty read"
		// phenomenon and do not read uncommitted changes made to rows by other transactions.
		//

//		DbQuery query2 = new DbQuery(session2, "select count(*) from GIRL");
//		assertEquals(0, query2.getOpenResultSetCount());
//		assertEquals(0, DbQuery.totalOpenResultSetCount);

//		rs = query2.execute();
//		if (rs.next()) {
//			// count before rollback (READ_UNCOMMITTED isolation level)
//			assertEquals(4, rs.getInt(1));
//		}
//		assertEquals(1, query2.getOpenResultSetCount());
//		assertEquals(1, DbQuery.totalOpenResultSetCount);

//		// HSQLDB supports transactions at the READ_UNCOMMITTED level, also known
//		// as level 0 transaction isolation.  This means that during the lifetime of
//		// a transaction, other connections to the database can see the changes made
//		// to the data
//
		session1.rollbackTransaction();

		DbQuery query2 = new DbQuery(session2, "select count(*) from GIRL");
		rs = query2.execute();
		assertEquals(1, query2.getOpenResultSetCount());
//		assertEquals(2, DbQuery.totalOpenResultSetCount);
		if (rs.next()) {
			assertEquals(3, rs.getInt(1));
		}

		session2.closeSession();
		assertEquals(0, query2.getOpenResultSetCount());
//		assertEquals(0, DbQuery.totalOpenResultSetCount);

		session1.closeSession();

	}

	@Test
	public void testSetMap() throws SQLException {
		DbSession session = new DbSession(cp);
		DbQuery dbQuery = new DbQuery(session, "select * from GIRL where ID = :id");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", Integer.valueOf(1));
		dbQuery.setMap(map);

		ResultSet rs = dbQuery.execute();
		if (rs.next()) {
			assertEquals(1, rs.getInt(1));
		}

	}

	@Test
	public void testSetObjects() throws SQLException {
		DbSession session = new DbSession(cp);
		DbQuery dbQuery = new DbQuery(session, "select * from GIRL where ID = ?");
		Object[] o = {Integer.valueOf(1)};
		dbQuery.setObjects(o);
		ResultSet rs = dbQuery.execute();
		if (rs.next()) {
			assertEquals(1, rs.getInt(1));
		}
	}

}
