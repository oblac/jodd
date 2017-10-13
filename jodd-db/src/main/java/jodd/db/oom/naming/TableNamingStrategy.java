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

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Naming strategy for converting entity names
 * (i.e. class names) to database table names.
 */
public class TableNamingStrategy extends BaseNamingStrategy {

	// ---------------------------------------------------------------- properties

	protected String prefix = StringPool.EMPTY;
	protected String suffix = StringPool.EMPTY;
	protected char entityNameTerminator = '$';

	public String getPrefix() {
		return prefix;
	}

	/**
	 * Table prefix, may be <code>null</code>.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	/**
	 * Table suffix, may be <code>null</code>.
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public char getEntityNameTerminator() {
		return entityNameTerminator;
	}

	/**
	 * Specifies the terminator character for entity names.
	 * When some type is proxified, new class name usually contains
	 * some special character, like '$'.
	 */
	public void setEntityNameTerminator(char entityNameTerminator) {
		this.entityNameTerminator = entityNameTerminator;
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Converts entity name to table name.
	 * @see #convertEntityNameToTableName(String)
	 */
	public String convertEntityNameToTableName(Class type) {
		return convertEntityNameToTableName(type.getSimpleName());
	}

	/**
	 * Converts entity (type) name to table name.
	 */
	public String convertEntityNameToTableName(String entityName) {
		int ndx = entityName.indexOf(entityNameTerminator);
		if (ndx != -1) {
			entityName = entityName.substring(0, ndx);
		}

		StringBuilder tableName = new StringBuilder(entityName.length() * 2);

		if (prefix != null) {
			tableName.append(prefix);
		}

		if (splitCamelCase) {
			String convertedTableName = StringUtil.fromCamelCase(entityName, separatorChar);
			tableName.append(convertedTableName);
		} else {
			tableName.append(entityName);
		}

		if (suffix != null) {
			tableName.append(suffix);
		}

		if (!changeCase) {
			return tableName.toString();
		}
		return uppercase ?
				toUppercase(tableName).toString() :
				toLowercase(tableName).toString();

	}

	/**
	 * Converts table name to entity (type) name.
	 */
	public String convertTableNameToEntityName(String tableName) {
		StringBuilder className = new StringBuilder(tableName.length());
		int len = tableName.length();

		int i = 0;
		if (prefix != null) {
			if (tableName.startsWith(prefix)) {
				i = prefix.length();
			}
		}
		if (suffix != null) {
			if (tableName.endsWith(suffix)) {
				len -= suffix.length();
			}
		}

		if (splitCamelCase) {
			boolean toUpper = true;
			for (; i < len; i++) {
				char c = tableName.charAt(i);
				if (c == separatorChar) {
					toUpper = true;
					continue;
				}
				if (toUpper) {
					className.append(Character.toUpperCase(c));
					toUpper = false;
				} else {
					className.append(Character.toLowerCase(c));
				}
			}
			return className.toString();
		}

		return tableName.substring(i, len);
	}

	/**
	 * Applies table naming strategy to given table name hint.
	 * Returns full table name.
	 */
	public String applyToTableName(String tableName) {
		String entityName = convertTableNameToEntityName(tableName);

		return convertEntityNameToTableName(entityName);
	}
}