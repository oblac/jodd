// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import junit.framework.TestCase;
import jodd.db.orm.DbOrmManager;
import jodd.db.orm.test.Girl;
import jodd.db.orm.test.Boy;
import jodd.db.orm.test.BadBoy;
import jodd.db.orm.test.BadGirl;

import java.util.Map;

public class DbEntitySqlTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DbOrmManager dbOrm = DbOrmManager.getInstance();
		if (dbOrm.isRegistered(Girl.class) == false) {
			dbOrm.registerType(Boy.class);
			dbOrm.registerType(BadBoy.class);
			dbOrm.registerType(BadGirl.class);
			dbOrm.registerType(Girl.class);
		}
	}

	protected void checkGirl(DbSqlBuilder b) {
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(3, params.entrySet().size());
		assertEquals(Integer.valueOf(1), params.get("girl.id").getValue());
		assertEquals("sanja", params.get("girl.name").getValue());
		assertEquals("c++", params.get("girl.speciality").getValue());
	}
	protected void checkBadGirl1(DbSqlBuilder b) {
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(2, params.entrySet().size());
		assertEquals(Integer.valueOf(2), params.get("badGirl.fooid").getValue());
		assertEquals(".net", params.get("badGirl.foospeciality").getValue());
	}
	protected void checkBadGirl2(DbSqlBuilder b) {
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(3, params.entrySet().size());
		assertEquals(Integer.valueOf(2), params.get("badGirl.fooid").getValue());
		assertEquals(".net", params.get("badGirl.foospeciality").getValue());
		assertNull(params.get("badGirl.fooname").getValue());
	}
	protected void checkBadGirl3(DbSqlBuilder b) {
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(1, params.entrySet().size());
		assertEquals(Integer.valueOf(2), params.get("badGirl.fooid").getValue());
	}
	protected void checkBadGirl4(DbSqlBuilder b) {
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(1, params.entrySet().size());
		assertEquals(Integer.valueOf(2), params.get("p0").getValue());
	}
	protected void checkGirl1(DbSqlBuilder b) {
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(1, params.entrySet().size());
		assertEquals("sanja", params.get("p0").getValue());
	}

	public void testInsert() {
		Girl g = new Girl(1, "sanja", "c++");
		DbSqlBuilder b = DbEntitySql.insert(g);
		assertEquals("insert into GIRL (ID, NAME, SPECIALITY) values (:girl.id, :girl.name, :girl.speciality)", b.generateQuery());
		checkGirl(b);
	}

	public void testTruncate() {
		Girl g = new Girl(1, "sanja", "c++");
		assertEquals("delete from GIRL", DbEntitySql.truncate(g).generateQuery());
		assertEquals("delete from GIRL", DbEntitySql.truncate(Girl.class).generateQuery());
	}

	public void testUpdate() {
		Girl g = new Girl(1, "sanja", "c++");
		DbSqlBuilder b = DbEntitySql.update(g);
		assertEquals("update GIRL Girl set Girl.ID=:girl.id, Girl.NAME=:girl.name, Girl.SPECIALITY=:girl.speciality  where (1=1)",
				b.generateQuery());
		checkGirl(b);

		BadGirl bg = new BadGirl(Integer.valueOf(2), null, ".net");
		b = DbEntitySql.update(bg);
		assertEquals(
				"update GIRL BadGirl set BadGirl.ID=:badGirl.fooid, BadGirl.SPECIALITY=:badGirl.foospeciality  where (BadGirl.ID=:badGirl.fooid)",
				b.generateQuery());
		checkBadGirl1(b);

		b = DbEntitySql.updateAll(bg);
		assertEquals(
				"update GIRL BadGirl set BadGirl.ID=:badGirl.fooid, BadGirl.NAME=:badGirl.fooname, BadGirl.SPECIALITY=:badGirl.foospeciality  where (BadGirl.ID=:badGirl.fooid)",
				b.generateQuery());
		checkBadGirl2(b);
	}

	public void testUpdateColumn() {
		BadGirl bg = new BadGirl(Integer.valueOf(1), "sanja", "c++");
		DbSqlBuilder b = DbEntitySql.updateColumn(bg, "fooname", "Anja");
		assertEquals(
				"update GIRL BadGirl set BadGirl.NAME=:p0 where (BadGirl.ID=:badGirl.fooid)",
				b.generateQuery());
		Map<String, ParameterValue> params = b.getQueryParameters();
		assertEquals(2, params.entrySet().size());
		assertEquals(Integer.valueOf(1), params.get("badGirl.fooid").getValue());
		assertEquals("Anja", params.get("p0").getValue());
	}

	public void testDelete() {
		Girl g = new Girl(1, "sanja", "c++");
		DbSqlBuilder b = DbEntitySql.delete(g);
		assertEquals("delete from GIRL where (GIRL.ID=:girl.id and GIRL.NAME=:girl.name and GIRL.SPECIALITY=:girl.speciality)",
				b.generateQuery());
		checkGirl(b);

		BadGirl bg = new BadGirl(Integer.valueOf(2), null, ".net");
		b = DbEntitySql.delete(bg);
		assertEquals(
				"delete from GIRL where (GIRL.ID=:badGirl.fooid and GIRL.SPECIALITY=:badGirl.foospeciality)",
				b.generateQuery());
		checkBadGirl1(b);

		b = DbEntitySql.deleteByAll(bg);
		assertEquals(
				"delete from GIRL where (GIRL.ID=:badGirl.fooid and GIRL.NAME=:badGirl.fooname and GIRL.SPECIALITY=:badGirl.foospeciality)",
				b.generateQuery());
		checkBadGirl2(b);

		b = DbEntitySql.deleteById(bg);
		assertEquals(
				"delete from GIRL where (GIRL.ID=:badGirl.fooid)",
				b.generateQuery());
		checkBadGirl3(b);

		b = DbEntitySql.deleteById(bg, Integer.valueOf(2));
		assertEquals(
				"delete from GIRL where GIRL.ID=:p0",
				b.generateQuery());
		checkBadGirl4(b);
	}

	public void testFrom() {
		Girl g = new Girl(1, "sanja", "c++");

		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl ",
				DbEntitySql.from(g).generateQuery());

		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl ",
				DbEntitySql.from(BadGirl.class).generateQuery());

		assertEquals("select ggg.ID, ggg.NAME, ggg.SPECIALITY from GIRL ggg ",
				DbEntitySql.from(BadGirl.class, "ggg").generateQuery());
	}

	public void testFind() {
		Girl g = new Girl(1, "sanja", "c++");
		DbSqlBuilder b = DbEntitySql.find(g);
		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl where (Girl.ID=:girl.id and Girl.NAME=:girl.name and Girl.SPECIALITY=:girl.speciality)",
				b.generateQuery());
		checkGirl(b);

		b = DbEntitySql.findByAll(g);
		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl where (Girl.ID=:girl.id and Girl.NAME=:girl.name and Girl.SPECIALITY=:girl.speciality)",
				b.generateQuery());
		checkGirl(b);

		BadGirl bg = new BadGirl(Integer.valueOf(2), null, ".net");
		b = DbEntitySql.find(bg);
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where (BadGirl.ID=:badGirl.fooid and BadGirl.SPECIALITY=:badGirl.foospeciality)",
				b.generateQuery());
		checkBadGirl1(b);

		b = DbEntitySql.findByAll(bg);
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where (BadGirl.ID=:badGirl.fooid and BadGirl.NAME=:badGirl.fooname and BadGirl.SPECIALITY=:badGirl.foospeciality)",
				b.generateQuery());
		checkBadGirl2(b);

		b = DbEntitySql.findByColumn(Girl.class, "name", "sanja");
		assertEquals("select Girl.ID, Girl.NAME, Girl.SPECIALITY from GIRL Girl where Girl.NAME=:p0",
				b.generateQuery());
		checkGirl1(b);

		b = DbEntitySql.findByColumn(BadGirl.class, "fooname", "sanja");
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where BadGirl.NAME=:p0",
				b.generateQuery());
		checkGirl1(b);

		b = DbEntitySql.findForeign(BadBoy.class, bg);
		assertEquals("select BadBoy.ID, BadBoy.GIRL_ID, BadBoy.NAME from BOY BadBoy where BadBoy.GIRL_ID=:p0",
				b.generateQuery());
		checkBadGirl4(b);

		b = DbEntitySql.findById(bg);
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where (BadGirl.ID=:badGirl.fooid)",
				b.generateQuery());
		checkBadGirl3(b);

		b = DbEntitySql.findById(bg, Integer.valueOf(2));
		assertEquals("select BadGirl.ID, BadGirl.NAME, BadGirl.SPECIALITY from GIRL BadGirl where BadGirl.ID=:p0",
				b.generateQuery());
		checkBadGirl4(b);
	}

	public void testCount() {

		Girl g = new Girl(1, "sanja", "c++");
		DbSqlBuilder b = DbEntitySql.count(g);
		assertEquals("select count(1) from GIRL Girl where (Girl.ID=:girl.id and Girl.NAME=:girl.name and Girl.SPECIALITY=:girl.speciality)",
				b.generateQuery());
		checkGirl(b);

		BadGirl bg = new BadGirl();
		b = DbEntitySql.count(bg);
		assertEquals("select count(1) from GIRL BadGirl where (1=1)",
				b.generateQuery());

		bg = new BadGirl(Integer.valueOf(2), null, ".net");
		b = DbEntitySql.count(bg);
		assertEquals("select count(1) from GIRL BadGirl where (BadGirl.ID=:badGirl.fooid and BadGirl.SPECIALITY=:badGirl.foospeciality)",
				b.generateQuery());
		checkBadGirl1(b);

		b = DbEntitySql.countAll(bg);
		assertEquals("select count(1) from GIRL BadGirl where (BadGirl.ID=:badGirl.fooid and BadGirl.NAME=:badGirl.fooname and BadGirl.SPECIALITY=:badGirl.foospeciality)",
				b.generateQuery());
		checkBadGirl2(b);

		b = DbEntitySql.count(BadGirl.class);
		assertEquals("select count(1) from GIRL BadGirl",
				b.generateQuery());

	}

}
