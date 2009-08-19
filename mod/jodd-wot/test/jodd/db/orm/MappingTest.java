// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.orm.test.Foo;
import jodd.db.orm.test.BooSqlType;
import jodd.db.orm.test.Boo;
import jodd.db.orm.test.FooColor;
import jodd.db.orm.test.FooWeight;
import jodd.db.orm.sqlgen.DbEntitySql;
import jodd.db.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.type.SqlTypeManager;

import java.util.List;
import java.sql.SQLException;
import java.math.BigDecimal;

public class MappingTest extends DbHsqldbTestCase {

@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMapping() throws SQLException {
		DbSession session = new DbThreadSession(cp);

		executeUpdate(session, "drop table FOO if exists");
		String sql = "create table FOO (" +
							"ID			integer		not null," +
							"NUMBER		integer 	not null," +
							"STRING		integer		not null," +
							"STRING2	integer		not null," +
							"BOO		integer		not null," +
							"COLOR		varchar		not null," +
							"WEIGHT		integer		not null," +
							"TIMESTAMP	timestamp	not null," +
							"CLOB		longvarchar	not null," +
							"BLOB		longvarbinary not null," +
							"DECIMAL	decimal		not null," +
							"DECIMAL2	varchar		not null," +
							"JDT1		bigint		not null," +
							"JDT2		varchar		not null," +
							"primary key (ID)" +
							')';
		executeUpdate(session, sql);

		sql = "insert into FOO values (1, 555, 173, 7, 999, 'red', 1, '2009-08-07 06:05:04.3333', 'W173', 'ABCDEF', 1.01, '-7.17', 0, '0')";
		executeUpdate(session, sql);

		DbOrmManager dbOrm = DbOrmManager.getInstance();
		dbOrm.registerEntity(Foo.class);
		SqlTypeManager.register(Boo.class, BooSqlType.class);
		//SqlTypeManager.register(FooWeight.class, FooWeigthSqlType.class);		// it is not required to register!

		List<Foo> foos = new DbOrmQuery("select * from FOO").listAndClose(Foo.class);
		assertEquals(1, foos.size());
		Foo foo = foos.get(0);
		assertEquals(1, foo.id);
		assertEquals(555, foo.number.value);
		assertEquals("173", foo.string);
		assertEquals("7", foo.string2);
		assertEquals(999, foo.boo.value);
		assertEquals(FooColor.red, foo.color);
		assertEquals(FooWeight.light, foo.weight);
		assertNotNull(foo.timestamp);
		assertEquals(109, foo.timestamp.getYear());
		assertEquals(6, foo.timestamp.getHours());
		assertEquals(5, foo.timestamp.getMinutes());
		assertNotNull(foo.clob);
		assertEquals(4, foo.clob.length());
		assertEquals("W173", foo.clob.getSubString(1, 4));
		assertEquals(3, foo.blob.length());
		assertEquals((byte)0xAB, foo.blob.getBytes(1,3)[0]);
		assertEquals((byte)0xCD, foo.blob.getBytes(1,3)[1]);
		assertEquals((byte)0xEF, foo.blob.getBytes(1,3)[2]);
		assertEquals("1.01", foo.decimal.toString());
		assertEquals("-7.17", foo.decimal2.toString());
		assertEquals("1970-01-01", foo.jdt1.toString("YYYY-MM-DD"));
		assertEquals("1970-01-01", foo.jdt2.toString("YYYY-MM-DD"));

		foo.string = "371";
		foo.string2 = "007";
		foo.boo.value = 213;
		foo.color = FooColor.yellow;
		foo.weight = FooWeight.heavy;
		foo.number.value = 222;
		foo.timestamp.setYear(108);
		foo.decimal = new BigDecimal("34.12");
		foo.decimal2 = new BigDecimal("1.099");
		DbOrmQuery doq = new DbOrmQuery(DbEntitySql.update(foo));
		foo.jdt1.setDay(2);
		foo.jdt1.setYear(3000);
		foo.jdt2.setDay(3);
		foo.jdt2.setYear(2900);
		doq.executeUpdateAndClose();

		foos = new DbOrmQuery("select * from FOO").listAndClose(Foo.class);
		assertEquals(1, foos.size());
		foo = foos.get(0);
		assertEquals(1, foo.id);
		assertEquals("371", foo.string);
		assertEquals("7", foo.string2);
		assertEquals(213, foo.boo.value);
		assertEquals(222, foo.number.value);
		assertEquals(FooColor.yellow, foo.color);
		assertEquals(FooWeight.heavy, foo.weight);
		assertEquals(108, foo.timestamp.getYear());
		assertEquals(4, foo.clob.length());
		assertEquals("W173", foo.clob.getSubString(1, 4));
		assertEquals(3, foo.blob.length());
		assertEquals("34.12", foo.decimal.toString());
		assertEquals("1.099", foo.decimal2.toString());
		assertEquals("3000-01-02", foo.jdt1.toString("YYYY-MM-DD"));
		assertEquals("2900-01-03", foo.jdt2.toString("YYYY-MM-DD"));

		executeUpdate(session, "drop table FOO if exists");
		session.closeSession();
	}
}
