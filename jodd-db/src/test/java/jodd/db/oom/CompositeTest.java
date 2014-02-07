// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbHsqldbTestCase;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.tst.User;
import jodd.db.oom.tst.WizUser;
import jodd.db.oom.tst.Wizard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompositeTest extends DbHsqldbTestCase {

	DbSession session;

	@Before
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
		query.executeUpdateAndClose();
	}

	@After
	public void tearDown() {
		session.closeSession();
	}

	@Test
	public void testCustomName() {
		DbOomQuery dbOomQuery = sql("select $C{u.*} from $T{User u}").query();
		User user = dbOomQuery.findAndClose(User.class);

		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);

		// select custom value into target entity
		dbOomQuery = sql("select $C{u.userId}, 'Sauron' as u.name from $T{User u}").query();
		user = dbOomQuery.findAndClose(User.class);

		assertEquals(1, user.userId);
		assertEquals("Sauron", user.name);
	}

	@Test
	public void testAdditionalColumn() {
		// default

		DbOomQuery dbOomQuery = sql("select $C{u.*}, 243 from $T{User u}").query();
		Object[] object = dbOomQuery.findAndClose(User.class, Integer.class);

		assertEquals(2, object.length);
		User user = (User) object[0];
		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);
		assertEquals(0, user.exp);

		assertEquals(243, ((Integer) object[1]).intValue());

		// with manual hints!

		dbOomQuery = sql("select $C{u.*}, 243 as exp from $T{User u}").query();

		user = dbOomQuery.withHints("u", "u.exp").findAndClose(User.class, Integer.class);

		assertEquals(1, user.userId);
		assertEquals("Gandalf", user.name);
		assertEquals(243, user.exp);
	}

	@Test
	public void testExtend() {
		DbOomQuery dbOomQuery = sql("select $C{w.*}, $C{w.user:u.*} from $T{Wizard w} inner join $T{User u} on $w.wizardId=$u.userId").query();

		Wizard wizard = dbOomQuery.findAndClose(/*Wizard.class, User.class*/);

		assertNotNull(wizard);
		assertEquals(1, wizard.wizardId);
		assertEquals(7, wizard.level);
		assertEquals("Gandalf", wizard.getName());

		// all in one class!

		dbOomQuery = sql("select $C{w.%}, $C{u.*} from $T{Wizard w} inner join $T{User u} on $w.wizardId=$u.userId").query();
		WizUser wizUser = dbOomQuery.findAndClose(WizUser.class);

		assertNotNull(wizUser);
		//assertEquals(1, wizUser.wizardId);
		assertEquals(7, wizUser.level);
		assertEquals(1, wizUser.userId);
		assertEquals("Gandalf", wizUser.name);
	}

}