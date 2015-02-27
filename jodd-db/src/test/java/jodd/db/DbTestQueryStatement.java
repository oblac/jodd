// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import org.junit.Test;

import java.sql.Types;

import static jodd.util.ArraysUtil.ints;
import static jodd.util.ArraysUtil.longs;
import static org.junit.Assert.assertEquals;

public class DbTestQueryStatement extends DbHsqldbTestCase {

	@Test
	public void testParams() throws Exception {
		DbSession session = new DbSession(cp);

		DbQuery query = new DbQuery(session, "!girlCount");
		assertEquals(0, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(1, 'Anna', 'swim')"));
		assertEquals(1, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(2, 'Sandra', 'piano')"));
		assertEquals(2, query.executeCount());
		assertEquals(1, executeUpdate(session, "insert into GIRL values(3, 'Monica', 'hacking')"));
		assertEquals(3, query.executeCount());
		assertEquals(0, query.getOpenResultSetCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setInteger("girlId", 1);
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setLong("girlId", 1);
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setShort("girlId", (short) 1);
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setInteger("girlId", Integer.valueOf(1));
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setObject("girlId", Integer.valueOf(1));
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setObject("girlId", Long.valueOf(1));
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id=:girlId");
		query.setObject("girlId", "1");
		assertEquals(1, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id in (:ids!5)");
		query.setInteger("ids.1", 1);
		query.setInteger("ids.2", 2);
		query.setInteger("ids.3", 3);
		query.setNull("ids.4", Types.INTEGER);
		query.setNull("ids.5", Types.INTEGER);
		assertEquals("select count(*) from GIRL where id in (?,?,?,?,?)", query.getQueryString());
		assertEquals(3, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id in (:ids!5)");
		long[] ids = longs(1,2,3);
		query.setBatch("ids", ids, 0);
		assertEquals(3, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id in (:ids!5)");
		int[] idss = ints(1, 2, 3);
		query.setBatch("ids", idss, 0);
		assertEquals(3, query.executeCount());
		query.close();

		query = new DbQuery(session, "select count(*) from GIRL where id in (:ids!5)");
		Long[] ids2 = new Long[]{1l,2l,3l,4l};
		query.setBatch("ids", ids2, 0);
		assertEquals(3, query.executeCount());
		query.close();
	}

}