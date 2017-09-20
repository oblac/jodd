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

import jodd.db.fixtures.DbH2TestCase;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.sqlgen.DbSqlBuilder;
import jodd.db.oom.fixtures.Girl;
import jodd.db.oom.fixtures.Girl2;
import jodd.db.oom.fixtures.IdName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DbOom2Test extends DbH2TestCase {

	@Test
	public void testOrm2() {
		DbOomManager.resetAll();

		DbSession session = new DbThreadSession(cp);

		executeUpdate(session, "drop table BOY if exists");
		executeUpdate(session, "drop table GIRL if exists");

		String sql = "create table GIRL (" +
				"ID			IDENTITY," +
				"NAME		varchar(20)	not null," +
				"SPECIALITY	varchar(20)	null," +
				"TIME timestamp not null default CURRENT_TIMESTAMP, " +
				"primary key (ID)" +
				')';

		DbQuery query = new DbQuery(sql);
		query.autoClose().executeUpdate();
		assertTrue(query.isClosed());

		executeUpdate(session, "insert into GIRL(ID, NAME, SPECIALITY) values(1, 'Anna', 'swim')");
		executeUpdate(session, "insert into GIRL(ID, NAME, SPECIALITY) values(2, 'Sandra', 'piano')");
		executeUpdate(session, "insert into GIRL(NAME) values('Monica')");

		session.closeSession();
		assertTrue(session.isSessionClosed());


		// prepare
		session = new DbThreadSession(cp);


		// ---------------------------------------------------------------- girls

		DbOomQuery q = new DbOomQuery("select * from GIRL where ID=1");

		Girl girl = q.find(Girl.class);
		checkGirl1(girl);
		assertTrue(q.isActive());

		IdName idName = q.find(IdName.class);
		assertNotNull(idName);
		assertEquals(1, idName.id);
		assertEquals("Anna", idName.name);

		try {
			q.find();        // this will fail since no entity is registered!
			fail("error");
		} catch (DbOomException doex) {
			// ignore
		}

		assertEquals(2, DbOomManager.getInstance().getTotalTypes());
		assertEquals(0, DbOomManager.getInstance().getTotalTableNames());
		assertEquals(2, DbOomManager.getInstance().getTotalNames());

		DbOomManager.getInstance().registerEntity(Girl.class, true);
		girl = q.find();
		checkGirl1(girl);

		assertEquals(2, DbOomManager.getInstance().getTotalTypes());
		assertEquals(1, DbOomManager.getInstance().getTotalTableNames());
		assertEquals(2, DbOomManager.getInstance().getTotalNames());

		q.close();

		session.closeSession();


		/**
		 * Test fails on HSQLDB 1.8 since generated columns are not supported.
		 */
		session = new DbThreadSession(cp);

		q = new DbOomQuery("insert into GIRL (NAME) values('Janna')");
		q.setGeneratedColumns();
		q.executeUpdate();
		long key = q.getGeneratedKey();
		assertEquals(4, key);
		q.close();

		q = new DbOomQuery("insert into GIRL (NAME) values('Janna2')");
		q.setGeneratedColumns("ID", "TIME");
		q.executeUpdate();
		Long Key = q.findGeneratedKey(Long.class);
		assertEquals(5, Key.longValue());
		q.close();

		q = new DbOomQuery("insert into GIRL (NAME) values('Sasha')");
		q.setGeneratedColumns("ID, TIME");
		q.executeUpdate();
		ResultSet rs = q.getGeneratedColumns();
		assertEquals(1, q.getOpenResultSetCount());
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				assertEquals(6, id);
				try {
					rs.getInt(2);
					fail("error");
				} catch (SQLException sex) {
					// ignore
				}
			}
		} catch (SQLException sex) {
			fail(sex.getMessage());
		}
		q.closeResultSet(rs);
		q.close();
		assertEquals(0, q.getOpenResultSetCount());

		session.closeSession();


		session = new DbThreadSession(cp);
		DbOomManager.getInstance().registerEntity(Girl2.class, true);
		Girl2 g2 = new Girl2("Gwen");
		q = DbEntitySql.insert(g2).query();
		assertEquals("insert into GIRL (NAME) values (:girl2.name)", q.getQueryString());
		q.setGeneratedColumns("ID");
		q.executeUpdate();
		DbOomUtil.populateGeneratedKeys(g2, q);
		//g2.id = Integer.valueOf((int) q.getGeneratedKey());
		q.close();
		assertEquals(7, g2.id.intValue());

		g2 = DbEntitySql.findByColumn(Girl2.class, "name", "Gwen").query().find(Girl2.class);
		assertEquals("Gwen", g2.name);
		assertNull(g2.speciality);
		assertNotNull(g2.time);
		assertEquals(7, g2.id.intValue());

		session.closeSession();


		session = new DbThreadSession(cp);

		q = DbSqlBuilder.sql("select * from $T{Girl g} where $g.name like :what order by $g.id").query();
		q.setString("what", "%anna%");
		List<Girl2> girls = q.list(Girl2.class);
		assertEquals(2, girls.size());
		checkGirl4(girls.get(0));
		checkGirl5(girls.get(1));
		q.close();

		session.closeSession();
	}


	private void checkGirl4(Girl2 girl) {
		assertNotNull(girl);
		assertEquals(4, girl.id.intValue());
		assertEquals("Janna", girl.name);
	}

	private void checkGirl5(Girl2 girl) {
		assertNotNull(girl);
		assertEquals(5, girl.id.intValue());
		assertEquals("Janna2", girl.name);
	}

	private void checkGirl1(Girl girl) {
		assertNotNull(girl);
		assertEquals(1, girl.id);
		assertEquals("Anna", girl.name);
		assertEquals("swim", girl.speciality);
	}


}
