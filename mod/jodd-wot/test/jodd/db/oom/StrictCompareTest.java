// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.tst.Tester;
import junit.framework.TestCase;

public class StrictCompareTest extends TestCase {

	public void testStrict() {
		DbOomManager.resetAll();
		DbOomManager dboom = DbOomManager.getInstance();
		dboom.setStrictCompare(true);

		dboom.registerEntity(Tester.class);

		DbEntityDescriptor ded = dboom.lookupType(Tester.class);
		assertEquals("TESTER", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNull(dboom.lookupTableName("tester"));
		assertNull(dboom.lookupTableName("Tester"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNull(ded.findByColumnName("id"));
		assertNull(ded.findByColumnName("Id"));

		dboom.reset();
		dboom.getTableNames().setLowercase(true);
		dboom.getColumnNames().setLowercase(true);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("tester", ded.getTableName());

		assertNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNull(dboom.lookupTableName("Tester"));

		assertNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));

		dboom.reset();
		dboom.getTableNames().setChangeCase(false);
		dboom.getTableNames().setSplitCamelCase(false);
		dboom.getColumnNames().setChangeCase(false);
		dboom.getColumnNames().setSplitCamelCase(false);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("Tester", ded.getTableName());

		assertNull(dboom.lookupTableName("TESTER"));
		assertNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));

		assertNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNull(ded.findByColumnName("Id")); 	// column is mapped to a property, and it starts with uncapitalized char
	}

	public void testTableNameDefault() {
		DbOomManager.resetAll();
		DbOomManager dboom = DbOomManager.getInstance();

		dboom.registerEntity(Tester.class);

		DbEntityDescriptor ded = dboom.lookupType(Tester.class);
		assertEquals("TESTER", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));

		dboom.reset();
		dboom.getTableNames().setLowercase(true);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("tester", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));

		dboom.reset();
		dboom.getTableNames().setChangeCase(false);
		dboom.getTableNames().setSplitCamelCase(false);
		dboom.registerEntity(Tester.class);

		ded = dboom.lookupType(Tester.class);
		assertEquals("Tester", ded.getTableName());

		assertNotNull(dboom.lookupTableName("TESTER"));
		assertNotNull(dboom.lookupTableName("tester"));
		assertNotNull(dboom.lookupTableName("Tester"));

		assertNotNull(ded.findByColumnName("ID"));
		assertNotNull(ded.findByColumnName("id"));
		assertNotNull(ded.findByColumnName("Id"));
	}

}