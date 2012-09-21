// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.naming;

import junit.framework.TestCase;

public class ColumnNamingStrategyTest extends TestCase {

	public void testColumnNameToPropertyName() {
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("FOO_BOO_ZOO"));
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("foo_boo_zoo"));
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("FOO_BOO_ZOO_"));
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("foo_boo_zoo_"));
		assertEquals("foo", convertColumnNameToPropertyName("FOO"));
		assertEquals("foo", convertColumnNameToPropertyName("foo"));
		assertEquals("", convertColumnNameToPropertyName(""));
	}

	public void testColumnNameToPropertyName2() {
		ColumnNamingStrategy cns = new ColumnNamingStrategy();

		assertEquals("foo", cns.convertColumnNameToPropertyName("FOO"));
		assertEquals("fooZoo", cns.convertColumnNameToPropertyName("FOO_ZOO"));

		cns.setSplitCamelCase(false);

		assertEquals("foo", cns.convertColumnNameToPropertyName("foo"));
		assertEquals("fooZoo", cns.convertColumnNameToPropertyName("fooZoo"));
	}

	public void testPropertyNameToColumnName() {
		assertEquals("FOO_BOO_ZOO", convertPropertyNameToColumnName("fooBooZoo", true));
		assertEquals("foo_boo_zoo", convertPropertyNameToColumnName("fooBooZoo", false));
		assertEquals("FOO", convertPropertyNameToColumnName("foo", true));
		assertEquals("foo", convertPropertyNameToColumnName("foo", false));
		assertEquals("", convertPropertyNameToColumnName("", true));
	}

	public void testPropertyNameToColumnName2() {
		ColumnNamingStrategy cns = new ColumnNamingStrategy();

		assertEquals("FOO", cns.convertPropertyNameToColumnName("foo"));
		assertEquals("FOO_BOO", cns.convertPropertyNameToColumnName("fooBoo"));

		cns.setUppercase(false);

		assertEquals("foo", cns.convertPropertyNameToColumnName("foo"));
		assertEquals("foo_boo", cns.convertPropertyNameToColumnName("fooBoo"));

		cns.setSplitCamelCase(false);

		assertEquals("foo", cns.convertPropertyNameToColumnName("foo"));
		assertEquals("fooboo", cns.convertPropertyNameToColumnName("fooBoo"));
		assertEquals("fooboo", cns.convertPropertyNameToColumnName("FOOBOO"));

		cns.setChangeCase(false);

		assertEquals("foo", cns.convertPropertyNameToColumnName("foo"));
		assertEquals("fooBoo", cns.convertPropertyNameToColumnName("fooBoo"));
		assertEquals("FOOBOO", cns.convertPropertyNameToColumnName("FOOBOO"));
	}

	static String convertColumnNameToPropertyName(String columnName) {
		ColumnNamingStrategy columnNamingStrategy = new ColumnNamingStrategy();
		return columnNamingStrategy.convertColumnNameToPropertyName(columnName);
	}

	static String convertPropertyNameToColumnName(String propertyName, boolean toUpperCase) {
		ColumnNamingStrategy columnNamingStrategy = new ColumnNamingStrategy();
		columnNamingStrategy.setUppercase(toUpperCase);
		return columnNamingStrategy.convertPropertyNameToColumnName(propertyName);
	}

}