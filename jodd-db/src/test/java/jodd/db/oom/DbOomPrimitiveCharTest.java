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

import jodd.db.DbOom;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.QueryMapper;
import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;
import jodd.db.type.StringSqlType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DbOomPrimitiveCharTest extends DbHsqldbTestCase {

	@Override
	@BeforeEach
	protected void setUp() throws Exception {
		super.setUp();

		DbEntityManager dbEntityManager = DbOom.get().entityManager();

		dbEntityManager.registerEntity(Entity.class);
	}

	@Test
	void testOrm() {
		try (final DbSession session = new DbThreadSession(cp)) {
			final Entity toInsert = new Entity(1, "Jodd", 'g');
			assertEquals(1, dbOom.entities().insert(toInsert).query().autoClose().executeUpdate());
			final Entity afterInsert = loadEntityWithId1();
			assertNotNull(afterInsert);
			assertEquals('g', afterInsert.value);
			// now update given entity
			final Entity toUpdated = new Entity(1, "makes fun", 'h');
			assertEquals(1, dbOom.entities().update(toUpdated).query().autoClose().executeUpdate());
			final Entity afterUpdate = loadEntityWithId1();
			assertNotNull(afterUpdate);
			assertEquals("makes fun", afterUpdate.name);
			assertEquals('h', afterUpdate.value);
		}
	}

	Entity loadEntityWithId1() {
		DbOomQuery q = DbOomQuery.query("select * from Entity_CHAR where id=1");
		Entity entity = q.find(new QueryMapper<Entity>() {
			@Override
			public Entity process(ResultSet resultSet) throws SQLException {
				Entity _entity = new Entity();
				_entity.id = resultSet.getInt("ID");
				_entity.name = resultSet.getString("NAME");
				_entity.value = resultSet.getString("VALUE").charAt(0);
				return _entity;
			}
		});

		return entity;
	}


	@DbTable (value = "ENTITY_CHAR")
	public static class Entity {

		Entity() {
		}

		public Entity(long id, String name, char value) {
			this.id = id;
			this.name = name;
			this.value = value;
		}

		@DbId
		long id;

		@DbColumn
		String name;

		@DbColumn (sqlType = StringSqlType.class)
		char value;
	}

}
