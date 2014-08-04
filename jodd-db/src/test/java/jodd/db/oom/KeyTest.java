// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class KeyTest {

	@Test
	public void testEqualEntities() {
		DbOomManager dbOomManager = DbOomManager.getInstance();
		dbOomManager.reset();

		DbEntityDescriptor<Foo> fooded = dbOomManager.registerEntity(Foo.class);
		DbEntityDescriptor<Bar> barded = dbOomManager.registerEntity(Bar.class);
		DbEntityDescriptor<User> userded = dbOomManager.registerEntity(User.class);

		Foo foo = new Foo();
		Bar bar = new Bar();
		BarBar barbar = new BarBar();

		foo.id = 1;
		bar.barId = Long.valueOf(1);
		barbar.barId = Long.valueOf(2);

		String keyFoo = fooded.getKeyValue(foo);
		String keyBar = barded.getKeyValue(bar);
		String keyBarBar = barded.getKeyValue(barbar);

		assertEquals(Foo.class.getName() + ":1", keyFoo);
		assertEquals(Bar.class.getName() + ":1", keyBar);
		assertEquals(Bar.class.getName() + ":2", keyBarBar);	// because we are using barded

		assertFalse(keyFoo.equals(keyBar));
		assertFalse(keyBarBar.equals(keyBar));

		assertEquals("idid", userded.getIdColumnName());
		assertEquals(User.class.getName() + ":null", userded.getKeyValue(new User()));

		try {
			DbEntityDescriptor<User2> user2 = dbOomManager.registerEntity(User2.class);
			user2.getColumnDescriptors();
			fail();
		}
		catch (Exception ignore) {}

	}

	@DbTable
	public static class Foo {
		@DbId
		public long id;
	}

	public static class Bar {
		@DbId
		public Long barId;
	}

	public static class BarBar extends Bar {

	}

	public static class User {
		@DbId("idid")
		Integer id;
		@DbColumn("aaa")
		String aaa;
		@DbColumn("bbb")
		String bbb;
	}

	public static class User2 {
		@DbId("idid")
		Integer id;
		@DbColumn("bbb")
		String aaa;
		@DbColumn("bbb")
		String bbb;
	}

}