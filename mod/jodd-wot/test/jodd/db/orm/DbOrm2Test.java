// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.DbQuery;
import jodd.db.DbH2TestCase;
import jodd.db.orm.test.Girl;
import jodd.db.orm.test.IdName;
import jodd.db.orm.test.Girl2;
import jodd.db.orm.sqlgen.DbEntitySql;
import jodd.db.orm.sqlgen.DbSqlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DbOrm2Test extends DbH2TestCase {


	public void testOrm() {
		DbOrmManager.getInstance().reset();

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
		query.executeUpdateAndClose();
		assertTrue(query.isClosed());

		executeUpdate(session, "insert into GIRL(ID, NAME, SPECIALITY) values(1, 'Anna', 'seduction')");
		executeUpdate(session, "insert into GIRL(ID, NAME, SPECIALITY) values(2, 'Sandra', 'spying')");
		executeUpdate(session, "insert into GIRL(NAME) values('Monica')");

		session.closeSession();
		assertTrue(session.isSessionClosed());


		// prepare
		session = new DbThreadSession(cp);


		// ---------------------------------------------------------------- girls

		DbOrmQuery q = new DbOrmQuery("select * from GIRL where ID=1");

		Girl girl = q.findOne(Girl.class);
		checkGirl1(girl);
		assertTrue(q.isActive());

		IdName idName = q.findOne(IdName.class);
		assertNotNull(idName);
		assertEquals(1, idName.id);
		assertEquals("Anna", idName.name);

		try {
			q.findOne();		// this will fail since no entity is registered!
			fail();
		} catch (DbOrmException doex) {
			// ignore
		}

		assertEquals(2, DbOrmManager.getInstance().getTotalTypes());
		assertEquals(0, DbOrmManager.getInstance().getTotalTableNames());
		assertEquals(2, DbOrmManager.getInstance().getTotalNames());

		DbOrmManager.getInstance().registerEntity(Girl.class, true);
		girl = (Girl) q.findOne();
		checkGirl1(girl);

		assertEquals(2, DbOrmManager.getInstance().getTotalTypes());
		assertEquals(1, DbOrmManager.getInstance().getTotalTableNames());
		assertEquals(2, DbOrmManager.getInstance().getTotalNames());

		q.close();

		session.closeSession();
	}


	/**
	 * Test fails on HSQLDB 1.8 since generated columns are not supported.
	 */
	public void testGenerated() {

		DbSession session = new DbThreadSession(cp);

		DbOrmQuery q = new DbOrmQuery("insert into GIRL (NAME) values('Janna')");
		q.setGeneratedColumns();
		q.executeUpdate();
		long key = q.getGeneratedKey();
		assertEquals(4, key);
		q.close();

		q = new DbOrmQuery("insert into GIRL (NAME) values('Janna2')");
		q.setGeneratedColumns("ID", "TIME");
		q.executeUpdate();
		Long Key = q.findGeneratedKey(Long.class);
		assertEquals(5, Key.longValue());
		q.close();

		q = new DbOrmQuery("insert into GIRL (NAME) values('Sasha')");
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
					fail();
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
	}

	public void testGeneratedValue() {
		DbSession session = new DbThreadSession(cp);
		DbOrmManager.getInstance().registerEntity(Girl2.class, true);
		Girl2 g2 = new Girl2("Gwen");
		DbOrmQuery q = DbEntitySql.insert(g2).query();
		assertEquals("insert into GIRL (NAME) values (:girl2.name)", q.getQueryString());
		q.setGeneratedColumns("ID");
		q.executeUpdate();
		DbOrmUtil.populateGeneratedKeys(g2, q);
		//g2.id = Integer.valueOf((int) q.getGeneratedKey());
		q.close();
		assertEquals(7, g2.id.intValue());

		g2 = DbEntitySql.findByColumn(Girl2.class, "name", "Gwen").query().findOne(Girl2.class);
		assertEquals("Gwen", g2.name);
		assertNull(g2.speciality);
		assertNotNull(g2.time);
		assertEquals(7, g2.id.intValue());

		session.closeSession();
	}



	public void testLike() {
		DbSession session = new DbThreadSession(cp);

		DbOrmQuery q = DbSqlBuilder.sql("select * from $T{Girl g} where $g.name like :what order by $g.id").query();
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
		assertEquals("seduction", girl.speciality);
	}


}
