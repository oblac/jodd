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

package jodd.db.oom.sqlgen;

import jodd.util.StringPool;
import jodd.util.StringUtil;

import static jodd.util.CharUtil.isAlpha;
import static jodd.util.CharUtil.isDigit;

/**
 * Internal template parser.
 */
class TemplateParser {

	// ---------------------------------------------------------------- parsing

	protected static final char ESCAPE_CHARACTER = '\\';	
	protected static final String MACRO_TABLE = "$T{";
	protected static final String MACRO_COLUMN = "$C{";
	protected static final String MACRO_MATCH = "$M{";
	protected static final String MACRO_VALUE = "$V{";

	/**
	 * Parses template and returns generated sql builder.
	 */
	public void parse(final DbSqlBuilder sqlBuilder, final String template) {
		int length = template.length();
		int last = 0;
		while (true) {
			int mark = template.indexOf('$', last);
			if (mark == -1) {
				if (last < length) {
					sqlBuilder.appendRaw(template.substring(last));
				}
				break;
			}

			int escapesCount = countEscapes(template, mark);                // check if escaped
			if (escapesCount > 0) {
				boolean isEscaped = escapesCount % 2 != 0;
				int escapesToAdd = escapesCount >> 1;
				sqlBuilder.appendRaw(template.substring(last, mark - escapesCount + escapesToAdd) + '$');
				if (isEscaped) {
					last = mark + 1;
					continue;
				}
			} else {
				sqlBuilder.appendRaw(template.substring(last, mark));
			}

			int end;

			if (template.startsWith(MACRO_TABLE, mark)) {
				mark += MACRO_TABLE.length();
				end = findMacroEnd(template, mark);
				onTable(sqlBuilder, template.substring(mark, end));
			} else if (template.startsWith(MACRO_COLUMN, mark)) {
				mark += MACRO_COLUMN.length();
				end = findMacroEnd(template, mark);
				onColumn(sqlBuilder, template.substring(mark, end));
			} else if (template.startsWith(MACRO_MATCH, mark)) {
				mark += MACRO_MATCH.length();
				end = findMacroEnd(template, mark);
				onMatch(sqlBuilder, template.substring(mark, end));
			} else if (template.startsWith(MACRO_VALUE, mark)) {
				mark += MACRO_VALUE.length();
				end = findMacroEnd(template, mark);
				onValue(sqlBuilder, template.substring(mark, end));
			} else {
				mark++;           // reference found
				end = mark;       // find macro end
				while (end < length) {
					if (!isReferenceChar(template, end)) {
						break;
					}
					end++;
				}
				onReference(sqlBuilder, template.substring(mark, end));
				end--;
			}
			end++;
			last = end;
		}
	}

	protected static boolean isReferenceChar(final String template, final int index) {
		char c = template.charAt(index);
		if ((c == '+') && (template.charAt(index - 1) == '.')) {
			return true;
		}
		return isDigit(c) || isAlpha(c) || (c == '_') || (c == '.');
	}



	/**
	 * Finds macros end.
	 */
	protected int findMacroEnd(final String template, final int fromIndex) {
		int endIndex = template.indexOf('}', fromIndex);
		if (endIndex == -1) {
			throw new DbSqlBuilderException("Template syntax error, some macros are not closed. Error at: '..." + template.substring(fromIndex));
		}
		return endIndex;
	}

	/**
	 * Count escapes to the left.
	 */
	protected int countEscapes(final String template, int macroIndex) {
		macroIndex--;
		int escapeCount = 0;
		while (macroIndex >= 0) {
			if (template.charAt(macroIndex) != ESCAPE_CHARACTER) {
				break;
			}
			escapeCount++;
			macroIndex--;
		}
		return escapeCount;
	}

	// ---------------------------------------------------------------- handlers

	protected void onTable(final DbSqlBuilder sqlBuilder, final String allTables) {
		String[] tables = StringUtil.split(allTables, StringPool.COMMA);
		for (String table : tables) {
			sqlBuilder.table(table);
		}
	}

	protected void onColumn(final DbSqlBuilder sqlBuilder, final String allColumns) {
		int len = allColumns.length();
		int lastNdx = 0;

		for (int i = 0; i < len; i++) {
			char c = allColumns.charAt(i);

			if (c == ',') {
				sqlBuilder.column(allColumns.substring(lastNdx, i));
				lastNdx = i + 1;
				continue;
			}

			if (c == '[') {
				i = allColumns.indexOf(']', i) + 1;
				if (i == 0) {
					i = len;
				}
			}
		}

		sqlBuilder.column(allColumns.substring(lastNdx));
	}

	protected void onReference(final DbSqlBuilder sqlBuilder, final String reference) {
		sqlBuilder.ref(reference);
	}

	protected void onMatch(final DbSqlBuilder sqlBuilder, final String expression) {
		sqlBuilder.match(expression);
	}

	protected void onValue(final DbSqlBuilder sqlBuilder, final String expression) {
		sqlBuilder.columnValue(expression);
	}


}
