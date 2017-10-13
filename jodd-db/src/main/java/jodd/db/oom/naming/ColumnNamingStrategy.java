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

import jodd.util.StringUtil;

/**
 * Naming strategy for converting property names
 * to database column names.
 */
public class ColumnNamingStrategy extends BaseNamingStrategy {

	// ---------------------------------------------------------------- methods

	/**
	 * Converts property name to column name.
	 */
	public String convertPropertyNameToColumnName(String propertyName) {
		StringBuilder tableName = new StringBuilder(propertyName.length() * 2);

		if (splitCamelCase) {
			String convertedTableName = StringUtil.fromCamelCase(propertyName, separatorChar);
			tableName.append(convertedTableName);
		} else {
			tableName.append(propertyName);
		}

		if (!changeCase) {
			return tableName.toString();
		}
		return uppercase ?
				toUppercase(tableName).toString() :
				toLowercase(tableName).toString();

	}

	/**
	 * Converts column name to property name.
	 */
	public String convertColumnNameToPropertyName(String columnName) {
		StringBuilder propertyName = new StringBuilder(columnName.length());
		int len = columnName.length();

		if (splitCamelCase) {
			boolean toUpper = false;
			for (int i = 0; i < len; i++) {
				char c = columnName.charAt(i);
				if (c == separatorChar) {
					toUpper = true;
					continue;
				}
				if (toUpper) {
					propertyName.append(Character.toUpperCase(c));
					toUpper = false;
				} else {
					propertyName.append(Character.toLowerCase(c));
				}
			}
			return propertyName.toString();
		}
		return columnName;
	}

	/**
	 * Applies column naming strategy to given column name hint.
	 * Returns full column name.
	 */
	public String applyToColumnName(String columnName) {
		String propertyName = convertColumnNameToPropertyName(columnName);

		return convertPropertyNameToColumnName(propertyName);
	}

}
