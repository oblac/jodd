// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import junit.framework.TestCase;

public class DbNameUtilTest extends TestCase {

	public void testClassNameToTableName() {
		assertEquals("FOO_BOO_ZOO", DbNameUtil.convertClassNameToTableName("FooBooZoo", true));
		assertEquals("foo_boo_zoo", DbNameUtil.convertClassNameToTableName("FooBooZoo", false));
		assertEquals("FOO", DbNameUtil.convertClassNameToTableName("Foo", true));
		assertEquals("foo", DbNameUtil.convertClassNameToTableName("Foo", false));
		assertEquals("", DbNameUtil.convertClassNameToTableName("", true));
		assertEquals("", DbNameUtil.convertClassNameToTableName("", false));
		assertEquals("DB_NAME_UTIL_TEST", DbNameUtil.convertClassNameToTableName(this.getClass(), true));
		assertEquals("db_name_util_test", DbNameUtil.convertClassNameToTableName(this.getClass(), false));

		assertEquals("QWE_FOO_BOO_ZOO", DbNameUtil.convertClassNameToTableName("FooBooZoo", "QWE_", null, true));
		assertEquals("QWE_foo_boo_zoo", DbNameUtil.convertClassNameToTableName("FooBooZoo", "QWE_", null, false));
		assertEquals("QWE_FOO_BOO_ZOO_XXX", DbNameUtil.convertClassNameToTableName("FooBooZoo", "QWE_", "_XXX", true));
		assertEquals("QWE_foo_boo_zoo_XXX", DbNameUtil.convertClassNameToTableName("FooBooZoo", "QWE_", "_XXX", false));
		assertEquals("QWE_FOO", DbNameUtil.convertClassNameToTableName("Foo", "QWE_", null, true));
		assertEquals("QWE_foo", DbNameUtil.convertClassNameToTableName("Foo", "QWE_", null, false));
		assertEquals("FOO_RED", DbNameUtil.convertClassNameToTableName("Foo", null, "_RED", true));
		assertEquals("foo_RED", DbNameUtil.convertClassNameToTableName("Foo", null, "_RED", false));
		assertEquals("QWE_", DbNameUtil.convertClassNameToTableName("", "QWE_", null, true));
		assertEquals("QWE_", DbNameUtil.convertClassNameToTableName("", "QWE_", null, false));
		assertEquals("QWE__XXX", DbNameUtil.convertClassNameToTableName("", "QWE_", "_XXX", true));
		assertEquals("QWE__XXX", DbNameUtil.convertClassNameToTableName("", "QWE_", "_XXX", false));
		assertEquals("_XXX", DbNameUtil.convertClassNameToTableName("", null, "_XXX", true));
		assertEquals("_XXX", DbNameUtil.convertClassNameToTableName("", null, "_XXX", false));
		assertEquals("QWE_DB_NAME_UTIL_TEST", DbNameUtil.convertClassNameToTableName(this.getClass(), "QWE_", null, true));
		assertEquals("QWE_db_name_util_test", DbNameUtil.convertClassNameToTableName(this.getClass(), "QWE_", null, false));
	}

	public void testClassNameToTableName2() {
		assertEquals("FOO_BOO_ZOO", DbNameUtil.convertClassNameToTableName("FooBooZoo$xxx", true));
		assertEquals("foo_boo_zoo", DbNameUtil.convertClassNameToTableName("FooBooZoo$xxx", false));
		assertEquals("QWE_foo_boo_zoo_XXX", DbNameUtil.convertClassNameToTableName("FooBooZoo$xxx", "QWE_", "_XXX", false));
	}


	public void testTableNameToClassName() {
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("FOO_BOO_ZOO", null, null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("foo_boo_zoo", null, null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("FOO_BOO_ZOO_", null, null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("foo_boo_zoo_", null, null));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("FOO", null, null));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("foo", null, null));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", null, null));

		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_", null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_foo_boo_zoo", "QWE_", null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO_", "QWE_", null));
		assertEquals("FooBooZoo", DbNameUtil.convertTableNameToClassName("QWE_foo_boo_zoo_", "QWE_", null));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("QWE_FOO", "QWE_", null));
		assertEquals("Foo", DbNameUtil.convertTableNameToClassName("QWE_foo", "QWE_", null));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("BOO", "QWE_", null));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("boo", "QWE_", null));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", "QWE_", null));

		assertEquals("FooBoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_", "_ZOO"));
		assertEquals("FooBoo", DbNameUtil.convertTableNameToClassName("QWE_foo_boo_ZOO", "QWE_", "_ZOO"));
		assertEquals("QweFooBoo", DbNameUtil.convertTableNameToClassName("QWE_FOO_BOO_ZOO_", null, "_ZOO_"));
		assertEquals("QweFooBoo", DbNameUtil.convertTableNameToClassName("qwe_foo_boo_ZOO_", null, "_ZOO_"));
		assertEquals("Qwe", DbNameUtil.convertTableNameToClassName("QWE_FOO", null, "_FOO"));
		assertEquals("Qwe", DbNameUtil.convertTableNameToClassName("qwe_FOO", null, "_FOO"));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("BOO", "QWE_", "_XXX"));
		assertEquals("Boo", DbNameUtil.convertTableNameToClassName("boo", "QWE_", "_XXX"));
		assertEquals("", DbNameUtil.convertTableNameToClassName("", "QWE_", "_XXX"));
	}

	public void testColumnNameToPropertyName() {
		assertEquals("fooBooZoo", DbNameUtil.convertColumnNameToPropertyName("FOO_BOO_ZOO"));
		assertEquals("fooBooZoo", DbNameUtil.convertColumnNameToPropertyName("foo_boo_zoo"));
		assertEquals("fooBooZoo", DbNameUtil.convertColumnNameToPropertyName("FOO_BOO_ZOO_"));
		assertEquals("fooBooZoo", DbNameUtil.convertColumnNameToPropertyName("foo_boo_zoo_"));
		assertEquals("foo", DbNameUtil.convertColumnNameToPropertyName("FOO"));
		assertEquals("foo", DbNameUtil.convertColumnNameToPropertyName("foo"));
		assertEquals("", DbNameUtil.convertColumnNameToPropertyName(""));
	}

	public void testPropertyNameToColumnName() {
		assertEquals("FOO_BOO_ZOO", DbNameUtil.convertPropertyNameToColumnName("fooBooZoo", true));
		assertEquals("foo_boo_zoo", DbNameUtil.convertPropertyNameToColumnName("fooBooZoo", false));
		assertEquals("FOO", DbNameUtil.convertPropertyNameToColumnName("foo", true));
		assertEquals("foo", DbNameUtil.convertPropertyNameToColumnName("foo", false));
		assertEquals("", DbNameUtil.convertPropertyNameToColumnName("", true));
	}

}
