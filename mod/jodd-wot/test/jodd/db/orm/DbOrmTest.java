// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.DbHsqldbTestCase;
import static jodd.db.orm.ColumnAliasType.COLUMN_CODE;
import jodd.db.orm.sqlgen.DbEntitySql;
import jodd.db.orm.sqlgen.DbSqlBuilder;
import static jodd.db.orm.sqlgen.DbSqlBuilder.sql;
import jodd.db.orm.test.BadBoy;
import jodd.db.orm.test.BadGirl;
import jodd.db.orm.test.Boy;
import jodd.db.orm.test.Girl;
import jodd.db.orm.test.IdName;
import static jodd.db.orm.DbOrmQuery.query;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DbOrmTest extends DbHsqldbTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DbOrmManager dbOrm = DbOrmManager.getInstance();
		dbOrm.registerEntity(Girl.class);
		dbOrm.registerEntity(BadBoy.class);
	}	
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		DbOrmManager dbOrm = DbOrmManager.getInstance();
		dbOrm.reset();
	}
	
	public void testOrm() {
		DbSession session = new DbThreadSession(cp);

		// ---------------------------------------------------------------- insert

		assertEquals(1, DbEntitySql.insert(new Girl(1, "Anna", "seduction")).query().executeUpdateAndClose());
		assertEquals(1, DbEntitySql.insert(new Girl(2, "Sandra", "spying")).query().executeUpdateAndClose());
		assertEquals(0, session.getTotalQueries());

		DbOrmQuery q2 = new DbOrmQuery(DbEntitySql.insert(new Girl(3, "Monica", null)));
		q2.setDebugMode();
		assertEquals("insert into GIRL (ID, NAME) values (:girl.id, :girl.name)", q2.getQueryString());
		q2.init();
		assertEquals("insert into GIRL (ID, NAME) values (3, 'Monica')", q2.getQueryString());
		assertEquals(1, q2.executeUpdateAndClose());
		assertTrue(q2.isClosed());

		assertEquals(1, DbEntitySql.insert(new BadBoy(Integer.valueOf(1), "Johny", Integer.valueOf(3))).query().executeUpdateAndClose());
		assertEquals(0, session.getTotalQueries());

		DbQuery dq = new DbQuery("select count(*) from GIRL where id>:id");
		dq.setInteger("id", 1);
		assertEquals(2, dq.executeCount());

		//dq.reset();
		dq.setInteger("id", 10);
		assertEquals(1, session.getTotalQueries());
		assertEquals(0, dq.executeCountAndClose());

		assertEquals(0, session.getTotalQueries());


		// ---------------------------------------------------------------- girl

		DbOrmQuery q = new DbOrmQuery("select * from GIRL order by ID");

		Girl girl = q.findOne(Girl.class);
		checkGirl1(girl);
		assertTrue(q.isActive());

		IdName idName = q.findOne(IdName.class);
		assertNotNull(idName);
		assertEquals(1, idName.id);
		assertEquals("Anna", idName.name);


		girl = (Girl) q.find(Girl.class);
		checkGirl1(girl);

		// list
		List<Girl> listGirl = q.listOne(Girl.class);
		assertEquals(3, listGirl.size());
		girl = listGirl.get(1);
		checkGirl2(girl);



		listGirl = q.listOne();
		assertEquals(3, listGirl.size());
		girl = listGirl.get(1);
		checkGirl2(girl);

		List list = q.list(Girl.class);
		assertEquals(3, list.size());
		girl = (Girl) list.get(2);
		checkGirl3(girl);
		assertFalse(q.isClosed());

		list = q.list();
		assertEquals(3, list.size());
		girl = (Girl) list.get(2);
		checkGirl3(girl);
		assertFalse(q.isClosed());

		// set
		Set<Girl> setGirl = q.listSetOne(Girl.class);
		assertEquals(3, setGirl.size());
		girl = (setGirl.iterator().next());
		checkGirl1(girl);

		setGirl = q.listSetOne();
		assertEquals(3, setGirl.size());
		girl = (setGirl.iterator().next());
		checkGirl1(girl);

		Set set = q.listSet(Girl.class);
		assertEquals(3, set.size());
		girl = (Girl) set.iterator().next();
		checkGirl1(girl);

		set = q.listSet();
		assertEquals(3, set.size());
		girl = (Girl) set.iterator().next();
		checkGirl1(girl);

		// iterator
		Iterator<Girl> it = q.iterateOne(Girl.class);
		while (it.hasNext()) {
			girl = it.next();
		}
		checkGirl3(girl);

		it = q.iterateOne();
		while (it.hasNext()) {
			girl = it.next();
		}
		checkGirl3(girl);


		Iterator it2 = q.iterate(Girl.class);
		while (it2.hasNext()) {
			girl = (Girl) it2.next();
		}
		checkGirl3(girl);

		it2 = q.iterate();
		while (it2.hasNext()) {
			girl = (Girl) it2.next();
		}
		checkGirl3(girl);

		q.close();

		// ---------------------------------------------------------------- girl2

		q = new DbOrmQuery("select girl.*, girl.* from GIRL girl order by girl.ID");
		list = q.list(Girl.class, Girl.class);
		assertEquals(3, list.size());
		assertEquals(2, ((Object[])list.get(2)).length);
		girl = (Girl) ((Object[]) list.get(2))[0];
		checkGirl3(girl);
		girl = (Girl) ((Object[]) list.get(2))[1];
		checkGirl3(girl);


		list = q.list();
		assertEquals(3, list.size());
		girl = (Girl) ((Object[]) list.get(2))[0];
		checkGirl3(girl);
		assertEquals(2, ((Object[])list.get(2)).length);
		girl = (Girl) ((Object[]) list.get(2))[0];
		checkGirl3(girl);
		girl = (Girl) ((Object[]) list.get(2))[1];
		checkGirl3(girl);

		q.close();

		// ---------------------------------------------------------------- boy

		q = new DbOrmQuery("select * from BOY order by ID");

		BadBoy badBoy = q.findOne(BadBoy.class);
		checkBoy(badBoy);

		badBoy = (BadBoy) q.findOne();
		checkBoy(badBoy);

		badBoy = (BadBoy) q.find(BadBoy.class);
		checkBoy(badBoy);

		badBoy = (BadBoy) q.find();
		checkBoy(badBoy);


		// list
		List<BadBoy> listBadBoyt = q.listOne(BadBoy.class);
		assertEquals(1, listBadBoyt.size());
		badBoy = listBadBoyt.get(0);
		checkBoy(badBoy);

		list = q.list(BadBoy.class);
		assertEquals(1, list.size());
		badBoy = (BadBoy) list.get(0);
		checkBoy(badBoy);


		// set
		Set<BadBoy> setBadBoy = q.listSetOne(BadBoy.class);
		assertEquals(1, setBadBoy.size());
		badBoy = (setBadBoy.iterator().next());
		checkBoy(badBoy);

		set = q.listSet(BadBoy.class);
		assertEquals(1, set.size());
		badBoy = (BadBoy) set.iterator().next();
		checkBoy(badBoy);

		// iterator
		Iterator<BadBoy> itBad = q.iterateOne(BadBoy.class);
		while (itBad.hasNext()) {
			badBoy = itBad.next();
		}
		checkBoy(badBoy);

		Iterator itBad2 = q.iterate(BadBoy.class);
		while (itBad2.hasNext()) {
			badBoy = (BadBoy) itBad2.next();
		}
		checkBoy(badBoy);

		q.close();


		// ---------------------------------------------------------------- join

		
		q = new DbOrmQuery("select * from GIRL join BOY on GIRL.ID=BOY.GIRL_ID");

		girl = q.findOne(Girl.class);
		checkGirl3(girl);

		girl = (Girl) q.findOne();
		checkGirl3(girl);

		Object[] res = (Object[]) q.find(Girl.class, BadBoy.class);
		badBoy = (BadBoy) res[1];
		girl = (Girl) res[0];
		checkGirl3(girl);
		checkBoy(badBoy);

		res = (Object[]) q.find();
		girl = (Girl) res[0];
		badBoy = (BadBoy) res[1];
		checkGirl3(girl);
		checkBoy(badBoy);

		q.close();



		q = new DbOrmQuery(DbSqlBuilder.sql("select $C{girl.*}, $C{BadBoy.*} from $T{Girl girl} join $T{BadBoy} on girl.id=$BadBoy.girlId"));
		badBoy = (BadBoy) q.withHints("BadBoy.girl, BadBoy").find();
		girl = badBoy.girl;
		checkGirl3(girl);
		checkBoy(badBoy);
		q.close();

		q = new DbOrmQuery(DbSqlBuilder.sql("select $C{girl.*}, $C{BadBoy.*} from $T{Girl girl} join $T{BadBoy} on girl.id=$BadBoy.girlId"));
		List<BadBoy> boys1 = q.withHints("BadBoy.girl, BadBoy").list(Girl.class, BadBoy.class);
		assertEquals(1, boys1.size());
		badBoy = boys1.get(0);
		assertNotNull(badBoy);
		girl = badBoy.girl;
		checkGirl3(girl);
		checkBoy(badBoy);
		q.close();

		q = query(sql("select $C{BadBoy.girl.*}, $C{BadBoy.*} from $T{Girl girl} join $T{BadBoy} on girl.id=$BadBoy.girlId"));
		badBoy = (BadBoy) q.find();
		girl = badBoy.girl;
		checkGirl3(girl);
		checkBoy(badBoy);
		q.close();



		// ---------------------------------------------------------------- join

		//q = new DbOrmQuery("select * from GIRL, BOY where BOY.GIRL_ID=GIRL.ID");
		q = new DbOrmQuery("select * from GIRL join BOY on GIRL.ID=BOY.GIRL_ID");

		badBoy = q.findOne(BadBoy.class);
		assertNull(badBoy);         // wrong mapping order, girl is first!

		BadGirl badGirl = q.findOne(BadGirl.class);
		checkBadGirl3(badGirl);

		res = (Object[]) q.find(BadBoy.class, BadGirl.class);
		badBoy = (BadBoy) res[0];
		badGirl = (BadGirl) res[1];
		checkBadGirl3(badGirl);
		assertNull(badBoy);     // order is invalid

		res = (Object[]) q.find(BadGirl.class, BadBoy.class);
		badBoy = (BadBoy) res[1];
		badGirl = (BadGirl) res[0];
		checkBadGirl3(badGirl);
		checkBoy(badBoy);

		res = (Object[]) q.find(Boy.class, Girl.class);
		Boy boy = (Boy) res[0];
		girl = (Girl) res[1];
		assertNull(boy);        // order is invalid
		checkGirl3(girl);

		res = (Object[]) q.find(Girl.class, Boy.class);
		boy = (Boy) res[1];
		girl = (Girl) res[0];
		checkBoy(boy);
		checkGirl3(girl);


		// ---------------------------------------------------------------- left join

		q = new DbOrmQuery("select * from GIRL left join BOY on GIRL.ID=BOY.GIRL_ID order by GIRL.ID");

		list = q.list(Girl.class, Boy.class);
		assertEquals(3, list.size());
		checkGirl1((Girl) ((Object[])list.get(0))[0]);
		assertNull(((Object[])list.get(0))[1]);

		checkGirl2((Girl) ((Object[])list.get(1))[0]);
		assertNull(((Object[])list.get(1))[1]);

		checkGirl3((Girl) ((Object[])list.get(2))[0]);
		checkBoy((Boy) ((Object[])list.get(2))[1]);


		// ---------------------------------------------------------------- etc

		badGirl = new BadGirl();
		badBoy = new BadBoy();
		DbSqlBuilder dt = sql("select g.*, b.* from $T{g}, $T{b} where $M{g=g} and $M{b=b}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		list = q.list(BadBoy.class, BadGirl.class);
		assertEquals(3, list.size());

		dt = sql("select g.*, b.* from $T{g}, $T{b} where $M{g=g} and $M{b=b}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		list = q.list(BadBoy.class, BadGirl.class);
		assertEquals(3, list.size());

		dt = sql("select g.*, b.* from $T{g}, $T{b} where $M{b=b} and $M{g=g}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		list = q.list(BadBoy.class, BadGirl.class);
		assertEquals(3, list.size());




		badGirl.fooname = "Sandra";
		dt = sql("select g.*, b.* from $T{g}, $T{b} where $M{b=b} and $M{g=g}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		Object[] result = (Object[]) q.find(BadGirl.class, BadBoy.class);
		checkBoy((BadBoy) result[1]);
		checkBadGirl2((BadGirl) result[0]);

		dt = sql("select g.*, b.* from $T{g}, $T{b} where $M{b=b} and $M{g=g}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		result = (Object[]) q.find(BadGirl.class, BadBoy.class);
		checkBoy((BadBoy) result[1]);
		checkBadGirl2((BadGirl) result[0]);

		dt = sql("select b.*, g.* from $T{g}, $T{b} where $M{g=g} and $M{b=b}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		result = (Object[]) q.find(BadBoy.class, BadGirl.class);
		checkBoy((BadBoy) result[0]);
		checkBadGirl2((BadGirl) result[1]);



		badBoy.ajdi = Integer.valueOf(1);
		badBoy.nejm = "Johny";
		dt = sql("select b.*, g.* from $T{g}, $T{b} where $M{g=g} and $M{b=b}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		result = (Object[]) q.find(BadBoy.class, BadGirl.class);
		checkBoy((BadBoy) result[0]);
		checkBadGirl2((BadGirl) result[1]);

		dt = sql("select b.*, g.* from $T{g}, $T{b} where $M{g=g} and $M{b=b}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		result = (Object[]) q.find(BadBoy.class, BadGirl.class);
		checkBoy((BadBoy) result[0]);
		checkBadGirl2((BadGirl) result[1]);

		dt = sql("select b.*, g.* from $T{g}, $T{b} where $M{b=b} and $M{g=g}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		result = (Object[]) q.find(BadBoy.class, BadGirl.class);
		checkBoy((BadBoy) result[0]);
		checkBadGirl2((BadGirl) result[1]);


		dt = sql("select $C{g.fooid}, $C{b.*} from $T{g}, $T{b} where $M{g=g} and $M{b=b}").use("g", badGirl).use("b", badBoy);
		q = new DbOrmQuery(dt);
		result = (Object[]) q.find(BadGirl.class, BadBoy.class);
		badGirl = (BadGirl) result[0];
		checkBoy((BadBoy) result[1]);
		assertEquals(2, badGirl.fooid.intValue());
		assertNull(badGirl.fooname);
		assertNull(badGirl.foospeciality);


		// ---------------------------------------------------------------- special

		dt = sql("select $g.fooid * 2 as did, $C{g.+} from $T{g} order by $g.fooid").aliasColumnsAs(COLUMN_CODE).use("g", BadGirl.class);
		q = new DbOrmQuery(dt);
		list = q.list(null, BadGirl.class); // explicitly ignore the first column 'did'
		assertEquals(3, list.size());
		assertEquals(1, ((BadGirl)((Object[])list.get(0))[1]).fooid.intValue());
		assertEquals(2, ((BadGirl)((Object[])list.get(1))[1]).fooid.intValue());
		assertEquals(3, ((BadGirl)((Object[])list.get(2))[1]).fooid.intValue());

		list = q.list(Integer.class, BadGirl.class);
		assertEquals(3, list.size());
		assertEquals(2, ((Integer)((Object[])list.get(0))[0]).intValue());
		assertEquals(1, ((BadGirl)((Object[])list.get(0))[1]).fooid.intValue());
		assertEquals(4, ((Integer)((Object[])list.get(1))[0]).intValue());
		assertEquals(2, ((BadGirl)((Object[])list.get(1))[1]).fooid.intValue());
		assertEquals(6, ((Integer)((Object[])list.get(2))[0]).intValue());
		assertEquals(3, ((BadGirl)((Object[])list.get(2))[1]).fooid.intValue());


		q = new DbOrmQuery("select g.ID * 2 as did, g.ID from Girl g order by g.ID");
		list = q.list(Integer.class, BadGirl.class);
		assertEquals(3, list.size());
		assertEquals(2, ((Integer)((Object[])list.get(0))[0]).intValue());
		assertEquals(1, ((BadGirl)((Object[])list.get(0))[1]).fooid.intValue());
		assertEquals(4, ((Integer)((Object[])list.get(1))[0]).intValue());
		assertEquals(2, ((BadGirl)((Object[])list.get(1))[1]).fooid.intValue());
		assertEquals(6, ((Integer)((Object[])list.get(2))[0]).intValue());
		assertEquals(3, ((BadGirl)((Object[])list.get(2))[1]).fooid.intValue());


		q = new DbOrmQuery(sql("select $g.id+$b.id as total, $C{g.*}, $g.id*2 as gdub, $C{b.*}, $g.id/3.0, $g.name from $T{g}, $T{b} where $b.girlId=$g.id").
				aliasColumnsAs(COLUMN_CODE).use("b", Boy.class).use("g", Girl.class));
		list = q.list(Integer.class, Girl.class, Long.class, Boy.class, Float.class, String.class);
		assertEquals(1, list.size());
		result = (Object[]) list.get(0);
		assertEquals(6, result.length);
		assertEquals(4, ((Integer) result[0]).intValue());
		checkGirl3((Girl) result[1]);
		assertEquals(6, ((Long) result[2]).intValue());		
		checkBoy((Boy) result[3]);

		assertEquals(1.0f, ((Float) result[4]).floatValue(), 0.05);
		assertEquals("Monica", (String) result[5]);


		q = new DbOrmQuery(sql("select $C{g.*}, $C{g.*} from $T{g} where $g.id=3").aliasColumnsAs(COLUMN_CODE).use("g", Girl.class));
		list = q.list(Girl.class, Girl.class);
		assertEquals(1, list.size());
		result = (Object[]) list.get(0);
		checkGirl3((Girl) result[0]);
		checkGirl3((Girl) result[1]);

		q = new DbOrmQuery(sql("select $C{g.*}, $g.name from $T{g} where $g.id=3").aliasColumnsAs(COLUMN_CODE).use("g", Girl.class));
		list = q.list(Girl.class, String.class);
		assertEquals(1, list.size());
		result = (Object[]) list.get(0);
		checkGirl3((Girl) result[0]);
		assertEquals("Monica", result[1]);

		q = new DbOrmQuery(sql("select $g.name, $C{g.*} from $T{g} where $g.id=3").aliasColumnsAs(COLUMN_CODE).use("g", Girl.class));
		list = q.list(String.class, Girl.class);
		assertEquals(1, list.size());
		result = (Object[]) list.get(0);
		checkGirl3((Girl) result[1]);
		assertEquals("Monica", (String) result[0]);

		//q.reset();
		list = q.list(String.class, Girl.class);
		result = (Object[]) list.get(0);
		assertEquals("Monica", (String) result[0]);


		q.close();

		// ---------------------------------------------------------------- finder

		girl = new Girl();
		badGirl = new BadGirl();
		badBoy = new BadBoy();

		DbOrmQuery f = DbEntitySql.find(girl).aliasColumnsAs(null).query();
		f.setDebugMode();
		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl where (Girl.ID=:girl.id)", f.toString());
		f.init();
		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl where (Girl.ID=0)", f.toString());
		f.close();
		f = DbEntitySql.find(badGirl).aliasColumnsAs(null).query();
		f.setDebugMode();
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where (1=1)", f.toString());
		f.close();
		f = DbEntitySql.find(badBoy).aliasColumnsAs(null).query();
		f.setDebugMode();
		assertEquals("select BadBoy.ID, BadBoy.GIRL_ID, BadBoy.NAME from BOY BadBoy where (1=1)", f.toString());
		f.close();

		girl.name = "Monica";
		badGirl.fooname = "Anna";
		badBoy.nejm = "David";

		f = DbEntitySql.find(girl).query();
		f.setDebugMode(); f.init();
		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl where (Girl.ID=0 and Girl.NAME='Monica')", f.toString());
		f.close();
		f = DbEntitySql.find(badGirl).query();
		f.setDebugMode(); f.init();
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where (BadGirl.NAME='Anna')", f.toString());
		f.close();
		f = DbEntitySql.find(badBoy).query();
		f.setDebugMode(); f.init();
		assertEquals("select BadBoy.ID, BadBoy.GIRL_ID, BadBoy.NAME from BOY BadBoy where (BadBoy.NAME='David')", f.toString());
		f.close();

		// ---------------------------------------------------------------- whole round

		badGirl = new BadGirl();
		badGirl.fooid = Integer.valueOf(2);
		f = DbEntitySql.findById(badGirl).query();
		list = f.listOneAndClose(BadGirl.class);
		assertTrue(f.isClosed());
		assertEquals(1, list.size());
		checkBadGirl2((BadGirl) list.get(0));

		f = DbEntitySql.count(badGirl).query();
		int count = (int) f.executeCountAndClose();
		assertEquals(1, count);
		assertTrue(f.isClosed());


		DbSqlGenerator g = DbEntitySql.deleteById(badGirl);
		f = new DbOrmQuery(g);
		f.executeUpdateAndClose();
		assertTrue(f.isClosed());

		f = DbEntitySql.count(badGirl).query();
		count = (int) f.executeCountAndClose();
		assertEquals(0, count);
		assertTrue(f.isClosed());

		badGirl.fooid = null;
		f = DbEntitySql.count(badGirl).query();
		count = (int) f.executeCountAndClose();
		assertEquals(2, count);
		assertTrue(f.isClosed());

		girl = new Girl();
		girl.id = 1;
		girl.name = "A%";
		f = new DbOrmQuery("select * from GIRL where id >= :girl.id and name like :girl.name");
		f.setDebugMode();
		f.setBean("girl", girl);
		assertEquals("select * from GIRL where id >= 1 and name like 'A%'", f.toString());
		count = (int) f.executeCount();
		assertEquals(1, count);

		//f.reset();
		girl.id = -2;
		f.setBean("girl", girl);
		assertEquals("select * from GIRL where id >= -2 and name like 'A%'", f.toString());
		count = (int) f.executeCount();
		assertEquals(1, count);

		//f.reset();

		badGirl = new BadGirl();
		badGirl.fooid = Integer.valueOf(3);
		f = DbEntitySql.findByColumn(BadBoy.class, "girlId", badGirl.fooid).query();
		f.setDebugMode(); f.init();
		assertEquals("select BadBoy.ID, BadBoy.GIRL_ID, BadBoy.NAME from BOY BadBoy where BadBoy.GIRL_ID=3", f.toString());
		f.close();

		f = DbEntitySql.findForeign(BadBoy.class, badGirl).query();
		f.setDebugMode(); f.init();
		assertEquals("select BadBoy.ID, BadBoy.GIRL_ID, BadBoy.NAME from BOY BadBoy where BadBoy.GIRL_ID=3", f.toString());

		f.close();

		badGirl = new BadGirl();
		badGirl.fooid = Integer.valueOf(3);
		BadGirl bbgg = DbEntitySql.findById(badGirl).query().findOneAndClose(BadGirl.class);
		bbgg.boys = DbEntitySql.findForeign(BadBoy.class, bbgg).query().listOneAndClose(BadBoy.class);

		assertNotNull(bbgg);
		assertEquals(3, bbgg.fooid.intValue());
		assertNotNull(bbgg.boys);
		assertEquals(1, bbgg.boys.size());
		assertEquals(1, bbgg.boys.get(0).ajdi.intValue());


		// ---------------------------------------------------------------- update

		badGirl = new BadGirl();
		badGirl.fooid = Integer.valueOf(1);
		badGirl = DbEntitySql.findById(badGirl).query().findOne(badGirl.getClass());
		checkBadGirl1(badGirl);

		badGirl.fooname = "Ticky";
		DbEntitySql.update(badGirl).query().executeUpdate();

		badGirl = new BadGirl();
		badGirl.fooid = Integer.valueOf(1);
		badGirl = DbEntitySql.findById(badGirl).query().findOne(badGirl.getClass());
		checkBadGirl1Alt(badGirl);

		badGirl.foospeciality = null;
		DbEntitySql.updateAll(badGirl).query().executeUpdate();

		badGirl = new BadGirl();
		badGirl.fooid = Integer.valueOf(1);
		badGirl = DbEntitySql.findById(badGirl).query().findOne(badGirl.getClass());
		checkBadGirl1Alt2(badGirl);


		// ---------------------------------------------------------------- joins

		q.close();


		// ---------------------------------------------------------------- double table names
		
		q = new DbOrmQuery("select g.*, g.* from GIRL g order by g.ID");
		//noinspection unchecked
		List<Object[]> g2 = q.list(Girl.class, Girl.class);
		assertEquals(2, g2.size());
		Object[] g2o = g2.get(0);
		assertEquals(2, g2o.length);
		checkGirl1Alt((Girl) g2o[0]);
		checkGirl1Alt((Girl) g2o[1]);
		q.close();

		q = new DbOrmQuery("select g.*, g2.* from GIRL g, GIRL g2 where g.ID=1 and g2.ID=3");
		//noinspection unchecked
		g2 = q.list(Girl.class, Girl.class);
		assertEquals(1, g2.size());
		g2o = g2.get(0);
		assertEquals(2, g2o.length);
		checkGirl1Alt((Girl) g2o[0]);
		checkGirl3Alt((Girl) g2o[1]);
		q.close();

		session.closeSession();

	}

	// ---------------------------------------------------------------- utils

	private void checkGirl1(Girl girl) {
		assertNotNull(girl);		
		assertEquals(1, girl.id);
		assertEquals("Anna", girl.name);
		assertEquals("seduction", girl.speciality);
	}

	private void checkGirl2(Girl girl) {
		assertNotNull(girl);
		assertEquals(2, girl.id);
		assertEquals("Sandra", girl.name);
		assertEquals("spying", girl.speciality);
	}

	private void checkGirl3(Girl girl) {
		assertNotNull(girl);
		assertEquals(3, girl.id);
		assertEquals("Monica", girl.name);
		assertNull(girl.speciality);
	}

	private void checkBadGirl1(BadGirl girl) {
		assertNotNull(girl);
		assertEquals(1, girl.fooid.intValue());
		assertEquals("Anna", girl.fooname);
		assertEquals("seduction", girl.foospeciality);
	}
	private void checkBadGirl1Alt(BadGirl girl) {
		assertNotNull(girl);
		assertEquals(1, girl.fooid.intValue());
		assertEquals("Ticky", girl.fooname);
		assertEquals("seduction", girl.foospeciality);
	}
	private void checkGirl1Alt(Girl girl) {
		assertNotNull(girl);
		assertEquals(1, girl.id);
		assertEquals("Ticky", girl.name);
		assertNull(girl.speciality);
	}
	private void checkGirl3Alt(Girl girl) {
		assertNotNull(girl);
		assertEquals(3, girl.id);
		assertEquals("Monica", girl.name);
		assertNull(girl.speciality);
	}
	private void checkBadGirl1Alt2(BadGirl girl) {
		assertNotNull(girl);
		assertEquals(1, girl.fooid.intValue());
		assertEquals("Ticky", girl.fooname);
		assertNull(girl.foospeciality);
	}


	private void checkBadGirl2(BadGirl girl) {
		assertNotNull(girl);
		assertEquals(2, girl.fooid.intValue());
		assertEquals("Sandra", girl.fooname);
		assertEquals("spying", girl.foospeciality);
	}


	private void checkBadGirl3(BadGirl girl) {
		assertNotNull(girl);
		assertEquals(3, girl.fooid.intValue());
		assertEquals("Monica", girl.fooname);
		assertNull(girl.foospeciality);
	}


	private void checkBoy(Boy boy) {
		assertNotNull(boy);
		assertEquals(1, boy.id);
		assertEquals("Johny", boy.name);
		assertEquals(3, boy.girlId);
	}
	private void checkBoy(BadBoy boy) {
		assertNotNull(boy);
		assertEquals(1, boy.ajdi.intValue());
		assertEquals("Johny", boy.nejm);
		assertEquals(3, boy.girlId.intValue());
	}
}
