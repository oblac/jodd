// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

public class DbMiscTest extends DbHsqldbTestCase {

	public void testBig() throws Exception {
		DbSession session = new DbSession(cp);		

		DbQuery query = new DbQuery(session, "select count(*) from GIRL");
		assertEquals(0, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(1, 'Anna', 'seduction')"));
		assertEquals(1, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(2, 'Sandra', 'spying')"));
		assertEquals(2, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(3, 'Monica', 'hacking')"));
		assertEquals(3, query.executeCount());
		assertEquals(0, query.getOpenResultSetCount());
	//	assertEquals(0, DbQuery.totalOpenResultSetCount);
		query.close();


		//  play with the query

		String sql = "select * from GIRL where ID = :id";
		query = new DbProfiledQuery(session, sql);
		query.setDebugMode();
		query.setInteger("id", 2);
		ResultSet rs = query.execute();
		assertEquals(1, query.getOpenResultSetCount());
	//	assertEquals(1, DbQuery.totalOpenResultSetCount);

		assertEquals("select * from GIRL where ID = 2\nExecution time: ", query.getQueryString().substring(0, 48));
		while (rs.next()) {
			assertEquals(2, rs.getInt(1));
			assertEquals("Sandra", rs.getString(2));
			assertEquals("spying", rs.getString(3));
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

		DbQuery query2 = new DbQuery(session2, "select count(*) from GIRL");
		assertEquals(0, query2.getOpenResultSetCount());
//		assertEquals(0, DbQuery.totalOpenResultSetCount);

		rs = query2.execute();
		if (rs.next()) {
			// count before rollback (READ_UNCOMMITTED isolation level)
			assertEquals(4, rs.getInt(1));
		}
		assertEquals(1, query2.getOpenResultSetCount());
//		assertEquals(1, DbQuery.totalOpenResultSetCount);

//		// HSQLDB supports transactions at the READ_UNCOMMITTED level, also known
//		// as level 0 transaction isolation.  This means that during the lifetime of
//		// a transaction, other connections to the database can see the changes made
//		// to the data
//
		session1.rollbackTransaction();

		rs = query2.execute();
		assertEquals(2, query2.getOpenResultSetCount());
//		assertEquals(2, DbQuery.totalOpenResultSetCount);
		if (rs.next()) {
			assertEquals(3, rs.getInt(1));
		}

		session2.closeSession();
		assertEquals(0, query2.getOpenResultSetCount());
//		assertEquals(0, DbQuery.totalOpenResultSetCount);
	
		session1.closeSession();

	}

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
