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

import jodd.datetime.JDateTime;
import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.fixtures.*;
import jodd.db.type.SqlTypeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MappingTest extends DbHsqldbTestCase {

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		DbOomManager.resetAll();
	}

	@Test
	public void testMapping() throws SQLException {
		DbSession session = new DbThreadSession(cp);

		executeUpdate(session, "drop table FOO if exists");
		String sql = "create table FOO (" +
				"ID			integer		not null," +
				"NUMBER		integer 	not null," +
				"STRING		integer		not null," +
				"STRING2	integer		not null," +
				"BOO		integer		not null," +
				"COLOR		varchar(50)		not null," +
				"WEIGHT		integer		not null," +
				"TIMESTAMP	timestamp	not null," +
				"TIMESTAMP2	timestamp	not null," +
				"CLOB		longvarchar	not null," +
				"BLOB		longvarbinary not null," +
				"DECIMAL	real		not null," +
				"DECIMAL2	varchar(50)		not null," +
				"JDT1		bigint		not null," +
				"JDT2		varchar(50)		not null," +
				"primary key (ID)" +
				')';
		executeUpdate(session, sql);

		sql = "insert into FOO values (1, 555, 173, 7, 999, 'red', 1, '2009-08-07 06:05:04.3333', '2010-01-20 01:02:03.4444', 'W173', 'ABCDEF', 1.01, '-7.17', 0, '0')";
		executeUpdate(session, sql);

		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Foo.class);
		SqlTypeManager.register(Boo.class, BooSqlType.class);
		SqlTypeManager.register(FooWeight.class, FooWeigthSqlType.class);

		List<Foo> foos = new DbOomQuery("select * from FOO").list(Foo.class);
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
		assertNotNull(foo.timestamp2);
		assertEquals(2010, foo.timestamp2.getYear());
		assertEquals(1, foo.timestamp2.getHour());
		assertEquals(2, foo.timestamp2.getMinute());
		assertNotNull(foo.clob);
		assertEquals(4, foo.clob.length());
		assertEquals("W173", foo.clob.getSubString(1, 4));
		assertEquals(3, foo.blob.length());
		assertEquals((byte) 0xAB, foo.blob.getBytes(1, 3)[0]);
		assertEquals((byte) 0xCD, foo.blob.getBytes(1, 3)[1]);
		assertEquals((byte) 0xEF, foo.blob.getBytes(1, 3)[2]);
		assertEquals("1.01", foo.decimal.toString().substring(0, 4));
		assertEquals("-7.17", foo.decimal2.toString().substring(0, 5));
		assertEquals("1970-01-01", foo.jdt1.toString("YYYY-MM-DD"));
		assertEquals("1970-01-01", foo.jdt2.toString("YYYY-MM-DD"));

		foo.string = "371";
		foo.string2 = "007";
		foo.boo.value = 213;
		foo.color = FooColor.yellow;
		foo.weight = FooWeight.heavy;
		foo.number.value = 222;
		foo.timestamp.setYear(108);
		foo.decimal = new Double("34.12");
		foo.decimal2 = new BigDecimal("1.099");
		DbOomQuery doq = new DbOomQuery(DbEntitySql.update(foo));
		foo.jdt1.setDay(2);
		foo.jdt1.setYear(3000);
		foo.jdt2.setDay(3);
		foo.jdt2.setYear(2900);
		doq.executeUpdate();


		doq = new DbOomQuery(DbEntitySql.updateColumn(foo, "timestamp2", new JDateTime("2010-02-02 20:20:20.222")));

		doq.executeUpdate();

		foos = new DbOomQuery("select * from FOO").list(Foo.class);
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
		assertEquals(2010, foo.timestamp2.getYear());
		assertEquals(20, foo.timestamp2.getHour());
		assertEquals(20, foo.timestamp2.getMinute());
		assertEquals(4, foo.clob.length());
		assertEquals("W173", foo.clob.getSubString(1, 4));
		assertEquals(3, foo.blob.length());
		assertEquals("34.12", foo.decimal.toString());
		assertEquals("1.099", foo.decimal2.toString().substring(0, 5));
		assertEquals("3000-01-02", foo.jdt1.toString("YYYY-MM-DD"));
		assertEquals("2900-01-03", foo.jdt2.toString("YYYY-MM-DD"));

		executeUpdate(session, "drop table FOO if exists");
		session.closeSession();
	}
}
