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

public class ColumnNamingStrategyTest {

	@Test
	public void testColumnNameToPropertyName() {
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("FOO_BOO_ZOO"));
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("foo_boo_zoo"));
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("FOO_BOO_ZOO_"));
		assertEquals("fooBooZoo", convertColumnNameToPropertyName("foo_boo_zoo_"));
		assertEquals("foo", convertColumnNameToPropertyName("FOO"));
		assertEquals("foo", convertColumnNameToPropertyName("foo"));
		assertEquals("", convertColumnNameToPropertyName(""));
	}

	@Test
	public void testColumnNameToPropertyName2() {
		ColumnNamingStrategy cns = new ColumnNamingStrategy();

		assertEquals("foo", cns.convertColumnNameToPropertyName("FOO"));
		assertEquals("fooZoo", cns.convertColumnNameToPropertyName("FOO_ZOO"));

		cns.setSplitCamelCase(false);

		assertEquals("foo", cns.convertColumnNameToPropertyName("foo"));
		assertEquals("fooZoo", cns.convertColumnNameToPropertyName("fooZoo"));
	}

	@Test
	public void testPropertyNameToColumnName() {
		assertEquals("FOO_BOO_ZOO", convertPropertyNameToColumnName("fooBooZoo", true));
		assertEquals("foo_boo_zoo", convertPropertyNameToColumnName("fooBooZoo", false));
		assertEquals("FOO", convertPropertyNameToColumnName("foo", true));
		assertEquals("foo", convertPropertyNameToColumnName("foo", false));
		assertEquals("", convertPropertyNameToColumnName("", true));
	}

	@Test
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
