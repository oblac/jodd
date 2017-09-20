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

import java.sql.Types;

import static jodd.util.ArraysUtil.ints;
import static jodd.util.ArraysUtil.longs;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
