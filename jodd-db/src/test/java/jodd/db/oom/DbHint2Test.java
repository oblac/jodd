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

import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.oom.fixtures.Boy4;
import jodd.db.oom.fixtures.Girl4;
import jodd.db.oom.fixtures.Room;
import jodd.db.oom.sqlgen.DbEntitySql;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbHint2Test extends DbHsqldbTestCase {

	@AfterEach
	@Override
	public void tearDown() throws Exception {
		DbSession session = new DbSession(cp);

		executeUpdate(session, "drop table GIRL if exists");
		executeUpdate(session, "drop table BOY if exists");
		executeUpdate(session, "drop table ROOM if exists");

		session.closeSession();

		super.tearDown();
	}

	@Override
	protected void initDb(DbSession session) {
		executeUpdate(session, "drop table ROOM if exists");
		executeUpdate(session, "drop table BOY if exists");
		executeUpdate(session, "drop table GIRL if exists");

		String sql;

		sql = "create table ROOM (" +
			"ID		integer not null," +
			"NAME varchar(20) null," +
			"primary key (ID)" +
		")";

		executeUpdate(session, sql);

		sql = "create table BOY (" +
				"ID			integer	not null," +
				"ROOM_ID	integer not null," +
				"NAME	varchar(20)	null," +
				"primary key (ID)," +
				"FOREIGN KEY (ROOM_ID) REFERENCES ROOM (ID)" +
				')';

		executeUpdate(session, sql);

		sql = "create table GIRL (" +
				"ID			integer		not null," +
				"BOY_ID		integer		not null," +
				"NAME		varchar(20)	not null," +
				"SPECIALITY	varchar(20)	null," +
				"primary key (ID)," +
				"FOREIGN KEY (BOY_ID) REFERENCES BOY (ID)" +
				')';

		executeUpdate(session, sql);
	}


	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();

		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Boy4.class);
		dbOom.registerEntity(Girl4.class);
		dbOom.registerEntity(Room.class);
	}

	@Test
	public void testHint() {
		DbSession dbSession = new DbThreadSession(cp);

		// prepare data

		assertEquals(1, DbEntitySql.insert(new Room(1, "Room1")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Room(2, "Room2")).query().executeUpdate());

		assertEquals(1, DbEntitySql.insert(new Boy4(1, 1, "Oleg")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Boy4(2, 2, "Stephene")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Boy4(3, 2, "Joe")).query().executeUpdate());

		assertEquals(1, DbEntitySql.insert(new Girl4(1, 1, "Anna")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Girl4(2, 2, "Sandra")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Girl4(3, 3, "Jossy")).query().executeUpdate());


		// select

		DbOomQuery dbOomQuery = new DbOomQuery(sql(
			"select $C{room.*}, $C{boy.*}, $C{girl.*} " +
				"from $T{Room room} join $T{Boy4 boy} on $room.id=$boy.roomId " +
				"join $T{Girl4 girl} on $boy.id=$girl.boyId " +
				"order by $room.id, $boy.id"
			));

		List<Object[]> results = dbOomQuery.autoClose().list(Room.class, Boy4.class, Girl4.class);

		assertEquals(3, results.size());

		Object[] row = results.get(0);
		assertEquals(1, ((Room) row[0]).getId().longValue());
		assertEquals(1, ((Boy4) row[1]).id.longValue());
		assertEquals(1, ((Girl4) row[2]).getId().longValue());

		// ---------------------------------------------------------------- hints

		dbOomQuery = new DbOomQuery(sql(
			"select $C{room.*}, $C{room.boys:boy.*}, $C{room.boys.girl:girl.*} " +
				"from $T{Room room} join $T{Boy4 boy} on $room.id=$boy.roomId " +
				"join $T{Girl4 girl} on $boy.id=$girl.boyId " +
				"order by $room.id, $boy.id"
			));

		dbOomQuery
			.entityAwareMode(true)
			.autoClose();

		List<Room> rooms = dbOomQuery.list(Room.class, Boy4.class, Girl4.class);

		assertEquals(2, rooms.size());

		// room #1
		Room room1 = rooms.get(0);
		assertEquals(1, room1.getId().longValue());
		assertEquals(1, room1.getBoys().size());

		Boy4 boy1 = room1.getBoys().get(0);
		assertEquals(1, boy1.id.intValue());
		Girl4 girl1 = boy1.girl;
		assertEquals(1, girl1.getId().longValue());

		// room #2
		Room room2 = rooms.get(1);
		assertEquals(2, room2.getId().longValue());
		assertEquals(2, room2.getBoys().size());

		Boy4 boy2 = room2.getBoys().get(0);
		Boy4 boy3 = room2.getBoys().get(1);

		assertEquals(2, boy2.id.longValue());
		assertEquals(3, boy3.id.longValue());

		Girl4 girl2 = boy2.girl;
		assertEquals(2, girl2.getId().longValue());
		Girl4 girl3 = boy3.girl;
		assertEquals(3, girl3.getId().longValue());

		dbSession.closeSession();
	}

}
