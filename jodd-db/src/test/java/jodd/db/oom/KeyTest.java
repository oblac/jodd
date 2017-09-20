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

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

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
			fail("error");
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
