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
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("FOO_BOO_ZOO", null, null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("FOO_BOO_ZOO_", null, null));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("FOO", null, null));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", null, null));

		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_", null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO_", "QWE_", null));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("QWE_FOO", "QWE_", null));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("BOO", "QWE_", null));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", "QWE_", null));

		assertEquals("FooBoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_", "_ZOO"));
		assertEquals("QweFooBoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO_", null, "_ZOO_"));
		assertEquals("Qwe", DbNameUtil.convertTableNameToClassName("QWE_FOO", null, "_FOO"));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("BOO", "QWE_", "_XXX"));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", "QWE_", "_XXX"));
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
