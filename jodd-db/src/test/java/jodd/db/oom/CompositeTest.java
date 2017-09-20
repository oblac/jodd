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

import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.fixtures.User;
import jodd.db.oom.fixtures.WizUser;
import jodd.db.oom.fixtures.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompositeTest extends DbHsqldbTestCase {

	DbSession session;

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(User.class);
		dbOom.registerEntity(Wizard.class);

		session = new DbThreadSession(cp);

		executeUpdate("drop table WIZARD if exists");
		executeUpdate("drop table USER if exists");

		DbQuery query = new DbQuery(
				"create table USER (" +
				"USER_ID	IDENTITY," +
				"NAME		varchar(20)	not null" +
				')');
		query.executeUpdate();

		query = new DbQuery("insert into USER values(1, 'Gandalf')");
		query.executeUpdate();

		query = new DbQuery(
				"create table WIZARD (" +
				"WIZARD_ID	IDENTITY," +
				"LEVEL		INT	not null" +
				')');
		query.executeUpdate();

		query = new DbQuery("insert into WIZARD values(1, 7);");
		query.executeUpdate();
	}

	@AfterEach
	public void tearDown() {
		session.closeSession();
	}

	@Test
	public void testCustomName() {
		DbOomQuery dbOomQuery = sql("select $C{u.*} from $T{User u}").query();
		User user = dbOomQuery.find(User.class);

		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);

		// select custom value into target entity
		dbOomQuery = sql("select $C{u.userId}, 'Sauron' as u.name from $T{User u}").query();
		user = dbOomQuery.find(User.class);

		assertEquals(1, user.userId);
		assertEquals("Sauron", user.name);
	}

	@Test
	public void testAdditionalColumn() {
		// default

		DbOomQuery dbOomQuery = sql("select $C{u.*}, 243 from $T{User u}").query();
		Object[] object = dbOomQuery.find(User.class, Integer.class);

		assertEquals(2, object.length);
		User user = (User) object[0];
		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);
		assertEquals(0, user.exp);

		assertEquals(243, ((Integer) object[1]).intValue());

		// with manual hints!

		dbOomQuery = sql("select $C{u.*}, 243 as exp from $T{User u}").query();

		user = dbOomQuery.withHints("u", "u.exp").find(User.class, Integer.class);

		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);
		assertEquals(243, user.exp);


		// with text hints!

		dbOomQuery = sql("select $C{u.*}, 243 as $C{u.exp:.exp} from $T{User u}").query();

		user = dbOomQuery.find(User.class, Integer.class);

		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);
		assertEquals(243, user.exp);
	}

	@Test
	public void testExtend() {
		DbOomQuery dbOomQuery = sql("select $C{w.*}, $C{w.user:u.*} from $T{Wizard w} inner join $T{User u} on $w.wizardId=$u.userId").query();

		Wizard wizard = dbOomQuery.find(/*Wizard.class, User.class*/);

		assertNotNull(wizard);
		assertEquals(1, wizard.wizardId);
		assertEquals(7, wizard.level);
		assertEquals("Gandalf", wizard.getName());

		// all in one class!

		dbOomQuery = sql("select $C{w.%}, $C{u.*} from $T{Wizard w} inner join $T{User u} on $w.wizardId=$u.userId").query();
		WizUser wizUser = dbOomQuery.find(WizUser.class);

		assertNotNull(wizUser);
		//assertEquals(1, wizUser.wizardId);
		assertEquals(7, wizUser.level);
		assertEquals(1, wizUser.userId);
		assertEquals("Gandalf", wizUser.name);
	}

}
