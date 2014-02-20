// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.tst.Tester;
import org.junit.Test;

import static org.junit.Assert.*;

public class StrictCompareTest {

	@Test
	public void testTableNameDefault() {
		DbOomManager.resetAll();
		DbOomManager dboom = DbOomManager.getInstance();

		dboom.registerEntity(Tester.class);

		DbEntityDescriptor ded = dboom.lookupType(Tester.class);
		assertEquals("TESTER", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));
		assertNotNull(dboom.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dboom.reset();
		dboom.getTableNames().setLowercase(true);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("tester", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));
		assertNotNull(dboom.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));

		dboom.reset();
		dboom.getTableNames().setChangeCase(false);
		dboom.getTableNames().setSplitCamelCase(false);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("Tester", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));
		assertNotNull(dboom.lookupTableName("TesTer"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
		assertNotNull(ded.findByColumnName("iD"));
	}

}
