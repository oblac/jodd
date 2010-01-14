// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import junit.framework.TestCase;

public class DbNameUtilTest extends TestCase {

	public void testClassNameToTableName() {
		assertEquals("FOO_BOO_ZOO", DbNameUtil.convertClassNameToTableName("FooBooZoo"));
		assertEquals("FOO", DbNameUtil.convertClassNameToTableName("Foo"));
		assertEquals("", DbNameUtil.convertClassNameToTableName(""));
		assertEquals("DB_NAME_UTIL_TEST", DbNameUtil.convertClassNameToTableName(this.getClass()));

		assertEquals("QWE_FOO_BOO_ZOO", DbNameUtil.convertClassNameToTableName("FooBooZoo", "QWE_", null));
		assertEquals("QWE_FOO_BOO_ZOO_XXX", DbNameUtil.convertClassNameToTableName("FooBooZoo", "QWE_", "_XXX"));
		assertEquals("QWE_FOO", DbNameUtil.convertClassNameToTableName("Foo", "QWE_", null));
		assertEquals("FOO_RED", DbNameUtil.convertClassNameToTableName("Foo", null, "_RED"));
		assertEquals("QWE_", DbNameUtil.convertClassNameToTableName("", "QWE_", null));
		assertEquals("QWE__XXX", DbNameUtil.convertClassNameToTableName("", "QWE_", "_XXX"));
		assertEquals("_XXX", DbNameUtil.convertClassNameToTableName("", null, "_XXX"));
		assertEquals("QWE_DB_NAME_UTIL_TEST", DbNameUtil.convertClassNameToTableName(this.getClass(), "QWE_", null));
	}

	public void testTableNameToClassName() {
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("FOO_BOO_ZOO"));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("FOO_BOO_ZOO_"));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("FOO"));
		assertEquals("", DbNameUtil.convertTableNameToClassName(""));

		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_"));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO_", "QWE_"));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("QWE_FOO", "QWE_"));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("BOO", "QWE_"));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", "QWE_"));
	}

	public void testColumnNameToPropertyName() {
		assertEquals("fooBooZoo", DbNameUtil.convertColumnNameToPropertyName("FOO_BOO_ZOO"));
		assertEquals("fooBooZoo", DbNameUtil.convertColumnNameToPropertyName("FOO_BOO_ZOO_"));
		assertEquals("foo", DbNameUtil.convertColumnNameToPropertyName("FOO"));
		assertEquals("", DbNameUtil.convertColumnNameToPropertyName(""));
	}

	public void testPropertyNameToColumnName() {
		assertEquals("FOO_BOO_ZOO", DbNameUtil.convertPropertyNameToColumnName("fooBooZoo"));
		assertEquals("FOO", DbNameUtil.convertPropertyNameToColumnName("foo"));
		assertEquals("", DbNameUtil.convertPropertyNameToColumnName(""));
	}

}
