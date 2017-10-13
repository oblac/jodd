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

package jodd.db.oom.naming;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableNamingStrategyTest {

	@Test
	public void testEntityNameToTableName() {
		assertEquals("FOO_BOO_ZOO", convertClassNameToTableName("FooBooZoo", true));
		assertEquals("foo_boo_zoo", convertClassNameToTableName("FooBooZoo", false));
		assertEquals("FOO", convertClassNameToTableName("Foo", true));
		assertEquals("foo", convertClassNameToTableName("Foo", false));
		assertEquals("", convertClassNameToTableName("", true));
		assertEquals("", convertClassNameToTableName("", false));
		assertEquals("TABLE_NAMING_STRATEGY_TEST", convertClassNameToTableName(this.getClass(), true));
		assertEquals("table_naming_strategy_test", convertClassNameToTableName(this.getClass(), false));

		assertEquals("QWE_FOO_BOO_ZOO", convertClassNameToTableName("FooBooZoo", "QWE_", null, true));
		assertEquals("qwe_foo_boo_zoo", convertClassNameToTableName("FooBooZoo", "QWE_", null, false));
		assertEquals("QWE_FOO_BOO_ZOO_XXX", convertClassNameToTableName("FooBooZoo", "QWE_", "_XXX", true));
		assertEquals("qwe_foo_boo_zoo_xxx", convertClassNameToTableName("FooBooZoo", "QWE_", "_XXX", false));
		assertEquals("QWE_FOO", convertClassNameToTableName("Foo", "QWE_", null, true));
		assertEquals("qwe_foo", convertClassNameToTableName("Foo", "QWE_", null, false));
		assertEquals("FOO_RED", convertClassNameToTableName("Foo", null, "_RED", true));
		assertEquals("foo_red", convertClassNameToTableName("Foo", null, "_RED", false));
		assertEquals("qwe_", convertClassNameToTableName("", "QWE_", null, false));
		assertEquals("QWE_", convertClassNameToTableName("", "QWE_", null, true));
		assertEquals("QWE__XXX", convertClassNameToTableName("", "QWE_", "_XXX", true));
		assertEquals("qwe__xxx", convertClassNameToTableName("", "QWE_", "_XXX", false));
		assertEquals("_XXX", convertClassNameToTableName("", null, "_XXX", true));
		assertEquals("_xxx", convertClassNameToTableName("", null, "_XXX", false));
		assertEquals("QWE_TABLE_NAMING_STRATEGY_TEST", convertClassNameToTableName(this.getClass(), "QWE_", null, true));
		assertEquals("qwe_table_naming_strategy_test", convertClassNameToTableName(this.getClass(), "QWE_", null, false));
	}

	@Test
	public void testEntityNameToTableName2() {
		TableNamingStrategy tns = new TableNamingStrategy();

		assertEquals("JODD_USER", tns.convertEntityNameToTableName("JoddUser"));

		tns.setPrefix("PRE_");
		assertEquals("PRE_JODD_USER", tns.convertEntityNameToTableName("JoddUser"));

		tns.setSuffix("_suf");
		assertEquals("PRE_JODD_USER_SUF", tns.convertEntityNameToTableName("JoddUser"));

		tns.setUppercase(false);
		assertEquals("pre_jodd_user_suf", tns.convertEntityNameToTableName("JoddUser"));

		tns.setChangeCase(false);
		assertEquals("PRE_jodd_user_suf", tns.convertEntityNameToTableName("JoddUser"));

		tns.setSplitCamelCase(false);
		assertEquals("PRE_JoddUser_suf", tns.convertEntityNameToTableName("JoddUser"));

		tns.setPrefix(null);
		tns.setSuffix(null);
		assertEquals("JoddUser", tns.convertEntityNameToTableName("JoddUser"));

	}

	@Test
	public void testSpecialEntityNameToTableName() {
		assertEquals("FOO_BOO_ZOO", convertClassNameToTableName("FooBooZoo$xxx", true));
		assertEquals("foo_boo_zoo", convertClassNameToTableName("FooBooZoo$xxx", false));
		assertEquals("qwe_foo_boo_zoo_xxx", convertClassNameToTableName("FooBooZoo$xxx", "QWE_", "_XXX", false));
	}


	@Test
	public void testTableNameToEntityName() {
		assertEquals("FooBooZoo", convertTableNameToClassName("FOO_BOO_ZOO", null, null));
		assertEquals("FooBooZoo", convertTableNameToClassName("foo_boo_zoo", null, null));
		assertEquals("FooBooZoo", convertTableNameToClassName("FOO_BOO_ZOO_", null, null));
		assertEquals("FooBooZoo", convertTableNameToClassName("foo_boo_zoo_", null, null));
		assertEquals("Foo", convertTableNameToClassName("FOO", null, null));
		assertEquals("Foo", convertTableNameToClassName("foo", null, null));
		assertEquals("", convertTableNameToClassName("", null, null));

		assertEquals("FooBooZoo", convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_", null));
		assertEquals("FooBooZoo", convertTableNameToClassName("QWE_foo_boo_zoo", "QWE_", null));
		assertEquals("FooBooZoo", convertTableNameToClassName("QWE_FOO_BOO_ZOO_", "QWE_", null));
		assertEquals("FooBooZoo", convertTableNameToClassName("QWE_foo_boo_zoo_", "QWE_", null));
		assertEquals("Foo", convertTableNameToClassName("QWE_FOO", "QWE_", null));
		assertEquals("Foo", convertTableNameToClassName("QWE_foo", "QWE_", null));
		assertEquals("Boo", convertTableNameToClassName("BOO", "QWE_", null));
		assertEquals("Boo", convertTableNameToClassName("boo", "QWE_", null));
		assertEquals("", convertTableNameToClassName("", "QWE_", null));

		assertEquals("FooBoo", convertTableNameToClassName("QWE_FOO_BOO_ZOO", "QWE_", "_ZOO"));
		assertEquals("FooBoo", convertTableNameToClassName("QWE_foo_boo_ZOO", "QWE_", "_ZOO"));
		assertEquals("QweFooBoo", convertTableNameToClassName("QWE_FOO_BOO_ZOO_", null, "_ZOO_"));
		assertEquals("QweFooBoo", convertTableNameToClassName("qwe_foo_boo_ZOO_", null, "_ZOO_"));
		assertEquals("Qwe", convertTableNameToClassName("QWE_FOO", null, "_FOO"));
		assertEquals("Qwe", convertTableNameToClassName("qwe_FOO", null, "_FOO"));
		assertEquals("Boo", convertTableNameToClassName("BOO", "QWE_", "_XXX"));
		assertEquals("Boo", convertTableNameToClassName("boo", "QWE_", "_XXX"));
		assertEquals("", convertTableNameToClassName("", "QWE_", "_XXX"));
	}

	@Test
	public void testTableNameToEntityName2() {
		TableNamingStrategy tns = new TableNamingStrategy();

		assertEquals("JoddUser", tns.convertTableNameToEntityName("JODD_USER"));
		assertEquals("JoddUser", tns.convertTableNameToEntityName("jodd_user"));

		tns.setPrefix("PRE_");
		assertEquals("JoddUser", tns.convertTableNameToEntityName("JODD_USER"));
		assertEquals("JoddUser", tns.convertTableNameToEntityName("PRE_JODD_USER"));
		assertEquals("JoddUser", tns.convertTableNameToEntityName("PRE_jodd_user"));

		tns.setSuffix("_suf");
		assertEquals("JoddUser", tns.convertTableNameToEntityName("PRE_JODD_USER"));
		assertEquals("JoddUser", tns.convertTableNameToEntityName("PRE_JODD_USER_suf"));
		assertEquals("JoddUser", tns.convertTableNameToEntityName("PRE_jodd_user_suf"));

		assertEquals("Jodduser", tns.convertTableNameToEntityName("PRE_JoddUser_suf"));
		tns.setSplitCamelCase(false);
		assertEquals("JoddUser", tns.convertTableNameToEntityName("PRE_JoddUser_suf"));

		tns.setSuffix(null);
		tns.setPrefix(null);
		assertEquals("JoddUser", tns.convertTableNameToEntityName("JoddUser"));
	}

	@Test
	public void testApplyToTableName() {
		TableNamingStrategy tns = new TableNamingStrategy();

		assertEquals("JODD_USER", tns.applyToTableName("JODD_USER"));
		assertEquals("JODD", tns.applyToTableName("joDd"));

		tns.setPrefix("SUP_");

		assertEquals("SUP_JODD_USER", tns.applyToTableName("JODD_USER"));
		assertEquals("SUP_JODD_USER", tns.applyToTableName("SUP_JODD_USER"));
		assertEquals("SUP_JODD", tns.applyToTableName("joDd"));

		tns.setSuffix("_EX");
		assertEquals("SUP_JODD_USER_EX", tns.applyToTableName("JODD_USER"));
		assertEquals("SUP_JODD_USER_EX", tns.applyToTableName("SUP_JODD_USER_EX"));

		tns.setLowercase(true);

		assertEquals("sup_jodd_user_ex", tns.applyToTableName("SUP_JODD_USER_EX"));
	}

	@Test
	public void testApplyToColumnName() {
		ColumnNamingStrategy cns = new ColumnNamingStrategy();

		assertEquals("JODD_USER", cns.applyToColumnName("JODD_USER"));
		assertEquals("JODD", cns.applyToColumnName("joDd"));

		cns.setLowercase(true);

		assertEquals("sup_jodd_user_ex", cns.applyToColumnName("SUP_JODD_USER_EX"));
	}

	// ---------------------------------------------------------------- tools

	static String convertClassNameToTableName(Class clazz, boolean toUpperCase) {
		return convertClassNameToTableName(clazz, null, null, toUpperCase);
	}

	static String convertClassNameToTableName(Class clazz, String tablePrefix, String tableSuffix, boolean toUpperCase) {
		return convertClassNameToTableName(clazz.getSimpleName(), tablePrefix, tableSuffix, toUpperCase);
	}

	static String convertClassNameToTableName(String className, boolean toUpperCase) {
		return convertClassNameToTableName(className, null, null, toUpperCase);
	}

	static String convertClassNameToTableName(String className, String tablePrefix, String tableSuffix, boolean toUpperCase) {
		TableNamingStrategy tableNamingStrategy = new TableNamingStrategy();
		tableNamingStrategy.setPrefix(tablePrefix);
		tableNamingStrategy.setSuffix(tableSuffix);
		tableNamingStrategy.setChangeCase(true);
		tableNamingStrategy.setUppercase(toUpperCase);

		return tableNamingStrategy.convertEntityNameToTableName(className);
	}

	static String convertTableNameToClassName(String tableName, String tablePrefix, String tableSuffix) {
		TableNamingStrategy tableNamingStrategy = new TableNamingStrategy();
		tableNamingStrategy.setPrefix(tablePrefix);
		tableNamingStrategy.setSuffix(tableSuffix);

		return tableNamingStrategy.convertTableNameToEntityName(tableName);
	}
}
